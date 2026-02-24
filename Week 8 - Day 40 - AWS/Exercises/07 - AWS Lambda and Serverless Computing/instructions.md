# Exercise 07: AWS Lambda and Serverless Computing

## Objective

Write, deploy, and trigger an AWS Lambda function in Node.js. Understand the serverless execution model, event-driven invocation, execution roles, environment variables, and API Gateway integration.

## Background

**AWS Lambda** runs your code without provisioning or managing servers. You upload a function, configure a trigger, and AWS handles scaling, patching, and capacity. You pay only for the compute time your code actually consumes (in 1ms increments). Lambda is the foundation of serverless architecture on AWS — commonly used for event-driven processing (S3 uploads, DynamoDB Streams, SQS messages) and REST API backends via API Gateway.

## Requirements

### Part 1 — Write the Lambda Function

Create a Node.js Lambda function (`index.js`) for an **Order Processor** that:

1. Accepts an event with the following shape (API Gateway proxy integration format):
   ```json
   {
     "httpMethod": "POST",
     "body": "{\"orderId\": \"ORD-001\", \"userId\": \"user-42\", \"total\": 149.99}"
   }
   ```

2. Parses `event.body` (it arrives as a JSON string from API Gateway)

3. Validates that `orderId`, `userId`, and `total` are present — if any are missing, return HTTP 400 with an error message

4. Logs the order using `console.log` (this goes to CloudWatch Logs automatically)

5. Uses an **environment variable** `STAGE` (e.g., `"dev"` or `"prod"`) in the log message

6. Returns a successful HTTP 200 response with:
   ```json
   {
     "message": "Order received",
     "orderId": "ORD-001",
     "stage": "dev"
   }
   ```

7. Returns HTTP 500 if an unexpected error occurs (wrap processing in try/catch)

### Part 2 — Lambda Configuration (CLI)

Write the AWS CLI commands to:

a. Create an **IAM execution role** for Lambda with the `AWSLambdaBasicExecutionRole` managed policy (allows writing CloudWatch logs)

b. Create a deployment package:
   ```bash
   zip function.zip index.js
   ```

c. **Create the Lambda function** using `aws lambda create-function`:
   - Runtime: `nodejs20.x`
   - Handler: `index.handler`
   - Environment variable: `STAGE=dev`
   - Memory: 128 MB
   - Timeout: 10 seconds

d. **Invoke the function** locally using `aws lambda invoke` with a test payload and print the response

e. **Update the environment variable** to `STAGE=prod` using `aws lambda update-function-configuration`

### Part 3 — Reflection Questions

Answer the following:

1. What is the **Lambda handler signature** in Node.js? What are `event`, `context`, and the return value used for?

2. Why does Lambda need an **execution role**? What would happen if you ran a Lambda function that tries to read from S3 without the right permissions?

3. What is a **cold start**? What causes it, and what are two ways to reduce cold start latency?

4. Lambda functions are **stateless** — what does this mean, and how do you persist state between invocations?

5. Compare Lambda to a traditional EC2-based API server:
   - Cost model
   - Scaling behavior
   - Maximum execution time (Lambda vs EC2)
   - When would you NOT use Lambda?

## Hints

- The API Gateway proxy integration always passes `event.body` as a **string**, not a parsed object — you must call `JSON.parse(event.body)`.
- Return value format for API Gateway: `{ statusCode: 200, headers: {...}, body: JSON.stringify({...}) }` — the `body` must also be a string.
- `process.env.STAGE` accesses the `STAGE` environment variable inside Node.js Lambda.
- Lambda maximum timeout is **15 minutes**. Default memory is 128 MB; max is 10 GB.
- The `--zip-file fileb://function.zip` syntax tells the AWS CLI to read the file as binary.

## Expected Output

```
Part 1: index.js — working Lambda handler for order processing
Part 2: CLI commands for role, package, deploy, invoke, update
Part 3: 5 reflection answers
```
