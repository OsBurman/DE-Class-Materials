/**
 * Day 23 — Part 2: API Documentation with Swagger / OpenAPI
 * ===========================================================
 * Topics demonstrated:
 *   ✓ OpenAPI 3.0 specification structure
 *   ✓ swagger-jsdoc — generates spec from JSDoc annotations
 *   ✓ swagger-ui-express — serves interactive Swagger UI
 *   ✓ Defining paths, operations, parameters, and schemas
 *   ✓ Request body schemas with validation rules
 *   ✓ Response schemas for success and error cases
 *   ✓ Reusable components ($ref)
 *   ✓ API testing via Swagger UI playground
 *   ✓ Postman concepts (Collections, Environments, Tests)
 *   ✓ Error handling patterns and status codes in APIs
 *
 * Run  : npm install && npm start
 * Docs : http://localhost:3000/api-docs   ← Swagger UI
 * Spec : http://localhost:3000/api-docs.json
 */

const express       = require('express');
const swaggerJsdoc  = require('swagger-jsdoc');
const swaggerUi     = require('swagger-ui-express');
const app  = express();
const PORT = 3000;

// ─────────────────────────────────────────────────────────────────────────────
//  In-memory data store
// ─────────────────────────────────────────────────────────────────────────────
let courses = [
  { id: 1, code: 'CS101', title: 'Intro to Programming',    instructor: 'Dr. Adams',  credits: 3, maxSeats: 30, enrolledCount: 28 },
  { id: 2, code: 'CS201', title: 'Data Structures',         instructor: 'Dr. Brown',  credits: 4, maxSeats: 25, enrolledCount: 20 },
  { id: 3, code: 'CS301', title: 'Database Systems',        instructor: 'Dr. Chen',   credits: 3, maxSeats: 20, enrolledCount: 15 },
  { id: 4, code: 'MATH101', title: 'Calculus I',            instructor: 'Dr. Davis',  credits: 4, maxSeats: 40, enrolledCount: 35 },
  { id: 5, code: 'BUS101', title: 'Business Fundamentals',  instructor: 'Dr. Foster', credits: 3, maxSeats: 50, enrolledCount: 42 },
];
let nextId = 6;

// ─────────────────────────────────────────────────────────────────────────────
//  OpenAPI / Swagger configuration
// ─────────────────────────────────────────────────────────────────────────────
const swaggerOptions = {
  definition: {
    openapi: '3.0.0',
    info: {
      title:       'Academy Course API',
      version:     '1.0.0',
      description: 'A RESTful API for managing university courses — built for Day 23 of the DE bootcamp.',
      contact: {
        name:  'Academy Dev Team',
        email: 'dev@academy.com',
      },
    },
    servers: [
      { url: 'http://localhost:3000', description: 'Local development server' },
    ],
    components: {
      schemas: {
        // ── Reusable Course schema ──
        Course: {
          type: 'object',
          properties: {
            id:            { type: 'integer',  example: 1               },
            code:          { type: 'string',   example: 'CS101'         },
            title:         { type: 'string',   example: 'Intro to Programming' },
            instructor:    { type: 'string',   example: 'Dr. Adams'     },
            credits:       { type: 'integer',  minimum: 1, maximum: 6, example: 3 },
            maxSeats:      { type: 'integer',  example: 30              },
            enrolledCount: { type: 'integer',  example: 28              },
          },
        },
        // ── Create/update payload (no id) ──
        CourseInput: {
          type: 'object',
          required: ['code', 'title', 'instructor', 'credits', 'maxSeats'],
          properties: {
            code:       { type: 'string',  minLength: 2, maxLength: 10, example: 'CS401'      },
            title:      { type: 'string',  minLength: 3, maxLength: 100, example: 'AI Basics'  },
            instructor: { type: 'string',  example: 'Dr. Nguyen'                              },
            credits:    { type: 'integer', minimum: 1, maximum: 6,       example: 3             },
            maxSeats:   { type: 'integer', minimum: 1,                    example: 25            },
          },
        },
        // ── Partial update payload ──
        CourseUpdate: {
          type: 'object',
          properties: {
            title:      { type: 'string'  },
            instructor: { type: 'string'  },
            credits:    { type: 'integer', minimum: 1, maximum: 6 },
            maxSeats:   { type: 'integer', minimum: 1             },
          },
        },
        // ── Error response ──
        Error: {
          type: 'object',
          properties: {
            error:   { type: 'string', example: 'Course not found'                },
            details: { type: 'array',  items: { type: 'string' }, example: ['code is required'] },
          },
        },
      },
    },
  },
  // Where to look for JSDoc comments with @swagger annotations
  apis: ['./index.js'],
};

const swaggerSpec = swaggerJsdoc(swaggerOptions);

// ─────────────────────────────────────────────────────────────────────────────
//  Middleware
// ─────────────────────────────────────────────────────────────────────────────
app.use(express.json());

// Request logger
app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    const ms = Date.now() - start;
    console.log(`[${res.statusCode}] ${req.method.padEnd(7)} ${req.path} (${ms}ms)`);
  });
  next();
});

// Mount Swagger UI at /api-docs
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec, {
  customSiteTitle: 'Academy API Docs',
  swaggerOptions: { persistAuthorization: true },
}));

// Expose raw OpenAPI JSON
app.get('/api-docs.json', (_req, res) => res.json(swaggerSpec));

// ─────────────────────────────────────────────────────────────────────────────
//  Helpers
// ─────────────────────────────────────────────────────────────────────────────
const findCourse = (id) => courses.find(c => c.id === parseInt(id));

const validateInput = (body, requireAll) => {
  const e = [];
  if (requireAll || body.code      !== undefined) if (!body.code  || body.code.trim().length  < 2) e.push('code must be at least 2 characters');
  if (requireAll || body.title     !== undefined) if (!body.title || body.title.trim().length < 3) e.push('title must be at least 3 characters');
  if (requireAll || body.instructor!== undefined) if (!body.instructor)                            e.push('instructor is required');
  if (requireAll || body.credits   !== undefined) if (!body.credits || body.credits < 1 || body.credits > 6) e.push('credits must be 1–6');
  if (requireAll || body.maxSeats  !== undefined) if (!body.maxSeats || body.maxSeats < 1)         e.push('maxSeats must be at least 1');
  return e;
};

// ─────────────────────────────────────────────────────────────────────────────
//  Route handlers — annotated with OpenAPI JSDoc
// ─────────────────────────────────────────────────────────────────────────────

/**
 * @swagger
 * /api/v1/courses:
 *   get:
 *     summary: List all courses
 *     description: Returns all courses. Supports optional filtering by instructor and sorting.
 *     tags: [Courses]
 *     parameters:
 *       - in: query
 *         name: instructor
 *         schema:
 *           type: string
 *         description: Filter courses by instructor name (partial match)
 *       - in: query
 *         name: sort
 *         schema:
 *           type: string
 *           enum: [title, credits]
 *         description: Sort results by field
 *       - in: query
 *         name: availableOnly
 *         schema:
 *           type: boolean
 *         description: If true, return only courses with available seats
 *     responses:
 *       200:
 *         description: List of courses
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 total:
 *                   type: integer
 *                 data:
 *                   type: array
 *                   items:
 *                     $ref: '#/components/schemas/Course'
 */
app.get('/api/v1/courses', (req, res) => {
  let result = [...courses];

  if (req.query.instructor) {
    result = result.filter(c => c.instructor.toLowerCase().includes(req.query.instructor.toLowerCase()));
  }
  if (req.query.availableOnly === 'true') {
    result = result.filter(c => c.enrolledCount < c.maxSeats);
  }
  if (req.query.sort === 'title')   result.sort((a, b) => a.title.localeCompare(b.title));
  if (req.query.sort === 'credits') result.sort((a, b) => b.credits - a.credits);

  res.status(200).json({ total: result.length, data: result });
});

/**
 * @swagger
 * /api/v1/courses/{id}:
 *   get:
 *     summary: Get a course by ID
 *     tags: [Courses]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: integer
 *         description: Course ID
 *     responses:
 *       200:
 *         description: The requested course
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Course'
 *       404:
 *         description: Course not found
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Error'
 */
app.get('/api/v1/courses/:id', (req, res) => {
  const course = findCourse(req.params.id);
  if (!course) return res.status(404).json({ error: `Course ${req.params.id} not found` });
  res.status(200).json(course);
});

/**
 * @swagger
 * /api/v1/courses:
 *   post:
 *     summary: Create a new course
 *     tags: [Courses]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CourseInput'
 *     responses:
 *       201:
 *         description: Course created
 *         headers:
 *           Location:
 *             description: URL of the newly created course
 *             schema:
 *               type: string
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Course'
 *       400:
 *         description: Validation error
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Error'
 *       409:
 *         description: Course code already exists
 */
app.post('/api/v1/courses', (req, res) => {
  const errors = validateInput(req.body, true);
  if (errors.length) return res.status(400).json({ error: 'Validation failed', details: errors });

  if (courses.some(c => c.code === req.body.code)) {
    return res.status(409).json({ error: `Course code '${req.body.code}' already exists` });
  }

  const course = {
    id:            nextId++,
    code:          req.body.code.trim().toUpperCase(),
    title:         req.body.title.trim(),
    instructor:    req.body.instructor.trim(),
    credits:       parseInt(req.body.credits),
    maxSeats:      parseInt(req.body.maxSeats),
    enrolledCount: 0,
  };
  courses.push(course);
  res.status(201).set('Location', `/api/v1/courses/${course.id}`).json(course);
});

/**
 * @swagger
 * /api/v1/courses/{id}:
 *   put:
 *     summary: Fully replace a course (idempotent)
 *     tags: [Courses]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: integer
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CourseInput'
 *     responses:
 *       200:
 *         description: Updated course
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Course'
 *       400:
 *         $ref: '#/components/schemas/Error'
 *       404:
 *         $ref: '#/components/schemas/Error'
 */
app.put('/api/v1/courses/:id', (req, res) => {
  const course = findCourse(req.params.id);
  if (!course) return res.status(404).json({ error: `Course ${req.params.id} not found` });
  const errors = validateInput(req.body, true);
  if (errors.length) return res.status(400).json({ error: 'Validation failed', details: errors });

  course.code       = req.body.code.trim().toUpperCase();
  course.title      = req.body.title.trim();
  course.instructor = req.body.instructor.trim();
  course.credits    = parseInt(req.body.credits);
  course.maxSeats   = parseInt(req.body.maxSeats);

  res.status(200).json(course);
});

/**
 * @swagger
 * /api/v1/courses/{id}:
 *   patch:
 *     summary: Partially update a course (idempotent)
 *     tags: [Courses]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: integer
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/CourseUpdate'
 *     responses:
 *       200:
 *         description: Partially updated course
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Course'
 *       400:
 *         $ref: '#/components/schemas/Error'
 *       404:
 *         $ref: '#/components/schemas/Error'
 */
app.patch('/api/v1/courses/:id', (req, res) => {
  const course = findCourse(req.params.id);
  if (!course) return res.status(404).json({ error: `Course ${req.params.id} not found` });
  const errors = validateInput(req.body, false);
  if (errors.length) return res.status(400).json({ error: 'Validation failed', details: errors });

  if (req.body.title      !== undefined) course.title      = req.body.title.trim();
  if (req.body.instructor !== undefined) course.instructor = req.body.instructor.trim();
  if (req.body.credits    !== undefined) course.credits    = parseInt(req.body.credits);
  if (req.body.maxSeats   !== undefined) course.maxSeats   = parseInt(req.body.maxSeats);

  res.status(200).json(course);
});

/**
 * @swagger
 * /api/v1/courses/{id}:
 *   delete:
 *     summary: Delete a course
 *     tags: [Courses]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: integer
 *     responses:
 *       204:
 *         description: Course deleted — no content returned
 *       404:
 *         description: Course not found
 */
app.delete('/api/v1/courses/:id', (req, res) => {
  const idx = courses.findIndex(c => c.id === parseInt(req.params.id));
  if (idx === -1) return res.status(404).json({ error: `Course ${req.params.id} not found` });
  courses.splice(idx, 1);
  res.status(204).send();
});

// ─────────────────────────────────────────────────────────────────────────────
//  Postman & Testing Reference
// ─────────────────────────────────────────────────────────────────────────────

/**
 * @swagger
 * /api/v1/testing-guide:
 *   get:
 *     summary: Postman & API testing reference
 *     tags: [Reference]
 *     responses:
 *       200:
 *         description: Testing concepts and best practices
 */
app.get('/api/v1/testing-guide', (_req, res) => {
  res.status(200).json({
    postman: {
      features: [
        'Collections — group related requests; export/import as JSON',
        'Environments — store base URLs, tokens, IDs as variables ({{baseUrl}})',
        'Pre-request Scripts — run JS before the request (set token, timestamp)',
        'Tests — run JS assertions after response (pm.test, pm.expect)',
        'Runner — execute entire collection; useful for regression testing',
        'Mock Servers — create a fake API without a real server',
      ],
      testExamples: {
        'Check status': "pm.test('Status 200', () => pm.response.to.have.status(200));",
        'Check body':   "pm.test('Has data', () => { const b = pm.response.json(); pm.expect(b.data).to.be.an('array'); });",
        'Set variable': "pm.environment.set('courseId', pm.response.json().id);",
      },
    },
    swaggerOpenApi: {
      purpose:    'Machine-readable API contract (YAML or JSON)',
      version:    'OpenAPI 3.0 used in this demo',
      sections: {
        info:       'Title, version, description, contact',
        servers:    'Base URLs for each environment',
        paths:      'Each endpoint with its operations, parameters, request body, responses',
        components: 'Reusable schemas ($ref), security schemes, parameters',
        security:   'JWT Bearer, API Key, OAuth2 flows',
      },
      tools: ['Swagger UI — interactive browser playground (this server)',
              'Redoc — beautiful read-only documentation',
              'Stoplight Studio — GUI editor for OpenAPI specs',
              'Postman — can import OpenAPI spec to generate a collection'],
    },
    errorHandlingPatterns: {
      structured: '{ "error": "...", "details": [...], "code": "VALIDATION_ERROR" }',
      httpCodes:  'Use correct HTTP status codes — never return 200 for errors',
      pagination: '{ "total": 5, "page": 1, "limit": 10, "data": [...] }',
      versioning: 'Increment major version on breaking changes: v1 → v2',
    },
  });
});

// ─────────────────────────────────────────────────────────────────────────────
//  404 handler
// ─────────────────────────────────────────────────────────────────────────────
app.use((req, res) => {
  res.status(404).json({ error: `Route ${req.method} ${req.path} not found` });
});

// ─────────────────────────────────────────────────────────────────────────────
//  Start
// ─────────────────────────────────────────────────────────────────────────────
app.listen(PORT, () => {
  console.log('\n╔══════════════════════════════════════════════════════════════╗');
  console.log('║   Day 23 · Part 2 — API Documentation with Swagger/OpenAPI  ║');
  console.log('╚══════════════════════════════════════════════════════════════╝');
  console.log(`\n  Server      : http://localhost:${PORT}`);
  console.log(`  Swagger UI  : http://localhost:${PORT}/api-docs   ← interactive docs`);
  console.log(`  OpenAPI JSON: http://localhost:${PORT}/api-docs.json`);
  console.log(`  Testing ref : http://localhost:${PORT}/api/v1/testing-guide`);
  console.log('\n  Try every endpoint directly in the Swagger UI playground!\n');
});
