'use strict';

/**
 * Exercise 07 — AWS Lambda Order Processor: SOLUTION
 *
 * Handler signature:
 *   exports.handler = async (event, context) => { ... }
 *
 * - event    : the trigger payload (API Gateway, S3, SQS, etc.)
 * - context  : Lambda runtime metadata (function name, remaining time, request ID)
 * - return   : for API Gateway proxy integration, must return { statusCode, headers, body }
 *              where body is a JSON *string* (not object)
 */
exports.handler = async (event, context) => {
  const STAGE = process.env.STAGE || 'dev';

  try {
    // --- 1. Parse body ---
    // API Gateway proxy passes body as a raw JSON string, not a parsed object.
    let body;
    try {
      body = JSON.parse(event.body);
    } catch (parseErr) {
      return {
        statusCode: 400,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: 'Invalid JSON in request body' }),
      };
    }

    // --- 2. Validate required fields ---
    const { orderId, userId, total } = body;
    const missing = ['orderId', 'userId', 'total'].filter((f) => body[f] === undefined || body[f] === null);
    if (missing.length > 0) {
      return {
        statusCode: 400,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          message: `Missing required fields: ${missing.join(', ')}`,
        }),
      };
    }

    // --- 3. Log the order (goes to CloudWatch Logs automatically) ---
    console.log(
      `[${STAGE}] Processing order ${orderId} for user ${userId} — total: $${total} | requestId: ${context.awsRequestId}`
    );

    // --- 4. Return success ---
    return {
      statusCode: 200,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        message: 'Order received',
        orderId,
        stage: STAGE,
      }),
    };
  } catch (err) {
    // --- 5. Catch unexpected errors ---
    console.error('Unexpected error processing order:', err);
    return {
      statusCode: 500,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: 'Internal server error' }),
    };
  }
};

// =============================================================================
// Part 2 — CLI Commands
// =============================================================================

/*
# ------------------------------------------------------------------
# a. Create IAM execution role for Lambda
# ------------------------------------------------------------------

# Trust policy — allows Lambda service to assume this role
cat > lambda-trust-policy.json << 'EOF'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": { "Service": "lambda.amazonaws.com" },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

aws iam create-role \
  --role-name order-processor-lambda-role \
  --assume-role-policy-document file://lambda-trust-policy.json

# Attach managed policy for CloudWatch Logs (basic execution)
aws iam attach-role-policy \
  --role-name order-processor-lambda-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Get the role ARN for the next step
ROLE_ARN=$(aws iam get-role \
  --role-name order-processor-lambda-role \
  --query "Role.Arn" --output text)

echo "Role ARN: $ROLE_ARN"

# ------------------------------------------------------------------
# b. Create deployment package
# ------------------------------------------------------------------
zip function.zip index.js

# ------------------------------------------------------------------
# c. Create the Lambda function
# ------------------------------------------------------------------
# Wait ~10s after role creation for IAM propagation before this command
aws lambda create-function \
  --function-name order-processor \
  --runtime nodejs20.x \
  --handler index.handler \
  --role "$ROLE_ARN" \
  --zip-file fileb://function.zip \
  --environment "Variables={STAGE=dev}" \
  --memory-size 128 \
  --timeout 10 \
  --description "Order processor Lambda — Exercise 07"

# ------------------------------------------------------------------
# d. Invoke the function with a test payload
# ------------------------------------------------------------------
aws lambda invoke \
  --function-name order-processor \
  --payload '{"httpMethod":"POST","body":"{\"orderId\":\"ORD-001\",\"userId\":\"user-42\",\"total\":149.99}"}' \
  --cli-binary-format raw-in-base64-out \
  response.json

cat response.json
# Expected output:
# {"message":"Order received","orderId":"ORD-001","stage":"dev"}

# ------------------------------------------------------------------
# e. Update environment variable to STAGE=prod
# ------------------------------------------------------------------
aws lambda update-function-configuration \
  --function-name order-processor \
  --environment "Variables={STAGE=prod}"
*/

// =============================================================================
// Part 3 — Reflection Answers
// =============================================================================

/*
1. Lambda handler signature:
   exports.handler = async (event, context) => { ... }

   - event   : the input payload from the trigger source (API Gateway, S3 event, SQS
                message, scheduled event, etc.). Shape varies by trigger.
   - context : Lambda runtime metadata — includes awsRequestId, functionName,
                getRemainingTimeInMillis() (ms until timeout), logStreamName, etc.
                Useful for logging and tracking execution.
   - return  : For API Gateway proxy integration, must return:
                { statusCode: number, headers: {}, body: string }
                The body MUST be JSON.stringify()'d — API Gateway passes it as-is to the client.
                For non-HTTP triggers (SQS, S3), return value is usually ignored.

2. Execution role:
   Lambda needs an IAM execution role because it runs in AWS's infrastructure, not
   under your IAM user credentials. The role is attached at function creation and
   defines what AWS services the function can call.

   Without the right permissions: if a Lambda function calls s3.getObject() without
   an S3 read permission in its execution role, the SDK call fails with an
   "AccessDenied" error. The function itself runs, but the AWS API call is rejected.
   This is the principle of least privilege in action.

3. Cold start:
   A cold start occurs when Lambda needs to initialize a new execution environment:
   download your code, start the runtime (Node.js/JVM/Python), and run initialization
   code outside the handler. This adds 100ms–1s of latency on the first request.

   Causes: function hasn't been invoked recently, traffic spike creates new containers,
   function was updated/redeployed.

   Two ways to reduce cold starts:
   a. Provisioned Concurrency — pre-warms a fixed number of execution environments
      so they're always ready. Eliminates cold starts for that concurrency level.
   b. Use a lighter runtime — Node.js and Python cold starts are much faster than
      Java (JVM startup is slow). Keep deployment package small (no unused deps).

4. Stateless functions:
   Each Lambda invocation gets a fresh execution environment (or a reused, but
   unguaranteed one). Global variables may persist between invocations on the same
   container (warm reuse), but you CANNOT rely on this behavior.

   To persist state between invocations:
   - Use an external store: DynamoDB (for structured data), ElastiCache (for session
     caching), S3 (for files/blobs), RDS (for relational data).
   - Lambda is intentionally stateless to enable horizontal scaling — 1000 concurrent
     invocations run on 1000 separate containers.

5. Lambda vs EC2 API Server:

   | Dimension          | Lambda                          | EC2                                 |
   |--------------------|---------------------------------|-------------------------------------|
   | Cost model         | Per-request + per-100ms compute | Per-hour (whether idle or not)      |
   | Scaling            | Automatic, up to 1000 concurrent| Manual/ASG — slower to scale out    |
   | Max execution time | 15 minutes                      | Unlimited                           |
   | Idle cost          | Zero (no invocations = no cost) | EC2 runs continuously               |

   When NOT to use Lambda:
   - Long-running tasks > 15 minutes (use ECS, Glue, or Step Functions instead)
   - WebSocket servers or persistent connections (Lambda is request-response only)
   - Apps needing >10 GB memory or GPU compute
   - Workloads where cold starts are unacceptable and Provisioned Concurrency is
     too expensive (sometimes EC2 + Auto Scaling is cheaper at consistent high load)
*/
