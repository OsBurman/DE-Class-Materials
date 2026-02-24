'use strict';

/**
 * Exercise 07 — AWS Lambda Order Processor
 *
 * This handler receives an API Gateway proxy event, validates the order payload,
 * logs the order to CloudWatch, and returns a structured HTTP response.
 *
 * TODO: Implement the function body following the requirements below.
 */
exports.handler = async (event, context) => {
  try {
    // TODO: Parse event.body (it is a JSON string from API Gateway)
    // Hint: const body = JSON.parse(event.body);

    // TODO: Validate that orderId, userId, and total are present.
    // If any field is missing, return statusCode 400 with an error message.

    // TODO: Log the order using console.log.
    // Include the STAGE environment variable: process.env.STAGE
    // Example: `[dev] Processing order ORD-001 for user user-42 — total: $149.99`

    // TODO: Return a 200 response with message, orderId, and stage.
    // Remember: the body must be JSON.stringify()'d
    return {
      statusCode: 200,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        message: 'TODO',
        orderId: 'TODO',
        stage: 'TODO',
      }),
    };
  } catch (err) {
    // TODO: Return a 500 response if an unexpected error occurs
    console.error('Unexpected error:', err);
    return {
      statusCode: 500,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: 'TODO' }),
    };
  }
};
