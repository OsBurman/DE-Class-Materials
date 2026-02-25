/**
 * Day 23 — Part 1: REST & HTTP Fundamentals
 * ===========================================
 * Topics demonstrated:
 *   ✓ HTTP methods: GET, POST, PUT, PATCH, DELETE
 *   ✓ HTTP status codes (200, 201, 204, 400, 404, 409, 500)
 *   ✓ HTTP request/response lifecycle
 *   ✓ HTTP headers (Content-Type, Accept, custom headers)
 *   ✓ RESTful resource naming conventions
 *   ✓ API versioning strategy (/api/v1/...)
 *   ✓ Request logging middleware
 *   ✓ Input validation and error responses
 *   ✓ Idempotency of PUT vs PATCH vs POST
 *
 * Run  : npm install && npm start
 * Test : use curl, Postman, or a browser
 *
 * Quick curl examples (in a second terminal):
 *   GET all     : curl http://localhost:3000/api/v1/students
 *   GET one     : curl http://localhost:3000/api/v1/students/1
 *   POST        : curl -X POST http://localhost:3000/api/v1/students \
 *                      -H "Content-Type: application/json" \
 *                      -d '{"name":"Zara","email":"zara@uni.edu","major":"CS","gpa":3.7}'
 *   PUT         : curl -X PUT http://localhost:3000/api/v1/students/1 \
 *                      -H "Content-Type: application/json" \
 *                      -d '{"name":"Alice","email":"alice@uni.edu","major":"CS","gpa":3.9}'
 *   PATCH       : curl -X PATCH http://localhost:3000/api/v1/students/1 \
 *                      -H "Content-Type: application/json" \
 *                      -d '{"gpa":3.95}'
 *   DELETE      : curl -X DELETE http://localhost:3000/api/v1/students/1
 *   Filter      : curl "http://localhost:3000/api/v1/students?major=Mathematics&sort=gpa"
 *   HTTP Info   : curl http://localhost:3000/api/v1/http-reference
 */

const express = require('express');
const app = express();
const PORT = 3000;
const BASE = '/api/v1';

// ─────────────────────────────────────────────────────────────────────────────
// In-memory data store (simulates a database)
// ─────────────────────────────────────────────────────────────────────────────
let students = [
  { id: 1, name: 'Alice Johnson',  email: 'alice@uni.edu',  major: 'Computer Science', gpa: 3.8 },
  { id: 2, name: 'Bob Smith',      email: 'bob@uni.edu',    major: 'Mathematics',      gpa: 3.2 },
  { id: 3, name: 'Carol Davis',    email: 'carol@uni.edu',  major: 'Computer Science', gpa: 3.9 },
  { id: 4, name: 'David Wilson',   email: 'david@uni.edu',  major: 'Business',         gpa: 2.9 },
  { id: 5, name: 'Emma Brown',     email: 'emma@uni.edu',   major: 'Physics',          gpa: 3.5 },
];
let nextId = 6;

// ─────────────────────────────────────────────────────────────────────────────
// Middleware
// ─────────────────────────────────────────────────────────────────────────────

// 1. Parse JSON request bodies
app.use(express.json());

// 2. Request logger — demonstrates HTTP request/response lifecycle
app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    const ms = Date.now() - start;
    const color = res.statusCode < 300 ? '\x1b[32m' : res.statusCode < 400 ? '\x1b[33m' : '\x1b[31m';
    console.log(`${color}[${res.statusCode}]\x1b[0m ${req.method.padEnd(7)} ${req.path} (${ms}ms)`);
  });
  next();
});

// 3. Add demo response headers to show HTTP header concepts
app.use((req, res, next) => {
  res.set('X-API-Version', '1.0');
  res.set('X-Powered-By', 'Academy REST Demo');
  next();
});

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────
const findStudent = (id) => students.find(s => s.id === parseInt(id));

const validate = (body, requireAll = true) => {
  const errors = [];
  if (requireAll || body.name    !== undefined) {
    if (!body.name || body.name.trim().length < 2) errors.push('name must be at least 2 characters');
  }
  if (requireAll || body.email   !== undefined) {
    if (!body.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(body.email))
      errors.push('email must be a valid address');
  }
  if (requireAll || body.major   !== undefined) {
    if (!body.major || body.major.trim().length < 2) errors.push('major is required');
  }
  if (requireAll || body.gpa     !== undefined) {
    if (body.gpa === undefined || body.gpa < 0.0 || body.gpa > 4.0)
      errors.push('gpa must be between 0.0 and 4.0');
  }
  return errors;
};

// ─────────────────────────────────────────────────────────────────────────────
// Routes — /api/v1/students
// ─────────────────────────────────────────────────────────────────────────────

/**
 * GET /api/v1/students
 * Returns the full list; supports ?major=X and ?sort=gpa|name query params.
 * HTTP 200 OK
 */
app.get(`${BASE}/students`, (req, res) => {
  let result = [...students];

  // Filter by major
  if (req.query.major) {
    result = result.filter(s =>
      s.major.toLowerCase().includes(req.query.major.toLowerCase())
    );
  }

  // Sort
  if (req.query.sort === 'gpa') {
    result.sort((a, b) => b.gpa - a.gpa);
  } else if (req.query.sort === 'name') {
    result.sort((a, b) => a.name.localeCompare(b.name));
  }

  // Pagination
  const page  = parseInt(req.query.page)  || 1;
  const limit = parseInt(req.query.limit) || result.length;
  const start = (page - 1) * limit;
  const paged = result.slice(start, start + limit);

  // 200 OK — resource found
  res.status(200).json({
    total: result.length,
    page,
    limit,
    data: paged,
  });
});

/**
 * GET /api/v1/students/:id
 * Returns a single student.
 * HTTP 200 OK  | 404 Not Found
 */
app.get(`${BASE}/students/:id`, (req, res) => {
  const student = findStudent(req.params.id);
  if (!student) {
    // 404 Not Found — resource doesn't exist
    return res.status(404).json({ error: `Student with id ${req.params.id} not found` });
  }
  res.status(200).json(student);
});

/**
 * POST /api/v1/students
 * Creates a new student.  NOT idempotent — calling twice creates two records.
 * HTTP 201 Created | 400 Bad Request | 409 Conflict
 */
app.post(`${BASE}/students`, (req, res) => {
  const errors = validate(req.body, true);
  if (errors.length > 0) {
    // 400 Bad Request — invalid input
    return res.status(400).json({ error: 'Validation failed', details: errors });
  }

  // Check duplicate email
  if (students.some(s => s.email === req.body.email)) {
    // 409 Conflict — resource already exists
    return res.status(409).json({ error: `Email '${req.body.email}' already in use` });
  }

  const newStudent = {
    id:    nextId++,
    name:  req.body.name.trim(),
    email: req.body.email.trim(),
    major: req.body.major.trim(),
    gpa:   parseFloat(req.body.gpa),
  };
  students.push(newStudent);

  // 201 Created + Location header pointing to new resource
  res
    .status(201)
    .set('Location', `${BASE}/students/${newStudent.id}`)
    .json(newStudent);
});

/**
 * PUT /api/v1/students/:id
 * Full replacement — all fields required.  Idempotent.
 * HTTP 200 OK | 400 Bad Request | 404 Not Found
 */
app.put(`${BASE}/students/:id`, (req, res) => {
  const student = findStudent(req.params.id);
  if (!student) return res.status(404).json({ error: `Student ${req.params.id} not found` });

  const errors = validate(req.body, true);
  if (errors.length > 0) return res.status(400).json({ error: 'Validation failed', details: errors });

  // PUT replaces the entire resource
  student.name  = req.body.name.trim();
  student.email = req.body.email.trim();
  student.major = req.body.major.trim();
  student.gpa   = parseFloat(req.body.gpa);

  res.status(200).json(student);
});

/**
 * PATCH /api/v1/students/:id
 * Partial update — only provided fields are changed.  Idempotent.
 * HTTP 200 OK | 400 Bad Request | 404 Not Found
 */
app.patch(`${BASE}/students/:id`, (req, res) => {
  const student = findStudent(req.params.id);
  if (!student) return res.status(404).json({ error: `Student ${req.params.id} not found` });

  const errors = validate(req.body, false); // partial validation
  if (errors.length > 0) return res.status(400).json({ error: 'Validation failed', details: errors });

  // PATCH only merges the supplied fields
  if (req.body.name  !== undefined) student.name  = req.body.name.trim();
  if (req.body.email !== undefined) student.email = req.body.email.trim();
  if (req.body.major !== undefined) student.major = req.body.major.trim();
  if (req.body.gpa   !== undefined) student.gpa   = parseFloat(req.body.gpa);

  res.status(200).json(student);
});

/**
 * DELETE /api/v1/students/:id
 * Removes a student.  Idempotent.
 * HTTP 204 No Content | 404 Not Found
 */
app.delete(`${BASE}/students/:id`, (req, res) => {
  const idx = students.findIndex(s => s.id === parseInt(req.params.id));
  if (idx === -1) return res.status(404).json({ error: `Student ${req.params.id} not found` });

  students.splice(idx, 1);
  // 204 No Content — success but no body
  res.status(204).send();
});

// ─────────────────────────────────────────────────────────────────────────────
// Reference endpoints
// ─────────────────────────────────────────────────────────────────────────────

/**
 * GET /api/v1/http-reference
 * Returns an HTTP methods and status codes reference guide.
 */
app.get(`${BASE}/http-reference`, (_req, res) => {
  res.status(200).json({
    httpMethods: {
      GET:    { description: 'Retrieve resource(s)',              idempotent: true,  safe: true  },
      POST:   { description: 'Create a new resource',             idempotent: false, safe: false },
      PUT:    { description: 'Full replacement of a resource',    idempotent: true,  safe: false },
      PATCH:  { description: 'Partial update of a resource',      idempotent: true,  safe: false },
      DELETE: { description: 'Remove a resource',                 idempotent: true,  safe: false },
      HEAD:   { description: 'Like GET but no response body',     idempotent: true,  safe: true  },
      OPTIONS:{ description: 'Describe communication options',    idempotent: true,  safe: true  },
    },
    statusCodes: {
      '2xx — Success': {
        200: 'OK — request succeeded',
        201: 'Created — new resource created (use with POST)',
        202: 'Accepted — async processing started',
        204: 'No Content — success but no body (DELETE, PUT)',
      },
      '3xx — Redirection': {
        301: 'Moved Permanently',
        302: 'Found (temporary redirect)',
        304: 'Not Modified (cached)',
      },
      '4xx — Client Error': {
        400: 'Bad Request — invalid input / malformed JSON',
        401: 'Unauthorized — authentication required',
        403: 'Forbidden — authenticated but not allowed',
        404: 'Not Found — resource does not exist',
        405: 'Method Not Allowed',
        409: 'Conflict — e.g. duplicate email',
        422: 'Unprocessable Entity — semantic validation error',
        429: 'Too Many Requests — rate limited',
      },
      '5xx — Server Error': {
        500: 'Internal Server Error',
        502: 'Bad Gateway',
        503: 'Service Unavailable',
        504: 'Gateway Timeout',
      },
    },
    restPrinciples: [
      'Stateless — each request contains all info needed (no server-side sessions)',
      'Client-Server — UI and data storage are separated',
      'Cacheable — responses declare whether they can be cached',
      'Uniform Interface — consistent resource naming and methods',
      'Layered System — client cannot tell whether connected directly to server',
      'Code on Demand (optional) — server can send executable code',
    ],
    resourceNaming: {
      good: [
        'GET    /api/v1/students',
        'GET    /api/v1/students/42',
        'POST   /api/v1/students',
        'PUT    /api/v1/students/42',
        'DELETE /api/v1/students/42',
        'GET    /api/v1/students/42/courses   (nested resources)',
      ],
      avoid: [
        'GET /getStudents         (verb in URL)',
        'POST /createStudent      (verb in URL)',
        'GET /student             (singular; prefer plural)',
        'DELETE /students/delete/42 (verb in URL)',
      ],
    },
    apiVersioning: {
      urlPath:   '/api/v1/students  — most visible, easy to test in browser',
      header:    'Accept: application/vnd.academy.v1+json',
      queryParam:'/api/students?version=1',
      recommended: 'URL path versioning for public APIs',
    },
  });
});

// ─────────────────────────────────────────────────────────────────────────────
// 404 Fallback
// ─────────────────────────────────────────────────────────────────────────────
app.use((req, res) => {
  res.status(404).json({ error: `Route ${req.method} ${req.path} not found` });
});

// ─────────────────────────────────────────────────────────────────────────────
// Global Error Handler
// ─────────────────────────────────────────────────────────────────────────────
// eslint-disable-next-line no-unused-vars
app.use((err, _req, res, _next) => {
  console.error('Unhandled error:', err);
  res.status(500).json({ error: 'Internal server error', message: err.message });
});

// ─────────────────────────────────────────────────────────────────────────────
// Start
// ─────────────────────────────────────────────────────────────────────────────
app.listen(PORT, () => {
  console.log('\n╔══════════════════════════════════════════════════════════════╗');
  console.log('║   Day 23 · Part 1 — REST & HTTP Fundamentals               ║');
  console.log('╚══════════════════════════════════════════════════════════════╝');
  console.log(`\n  Server running at http://localhost:${PORT}`);
  console.log('\n  Endpoints:');
  console.log(`  GET    ${BASE}/students            — list all (supports ?major=X&sort=gpa&page=1&limit=3)`);
  console.log(`  GET    ${BASE}/students/:id         — get one`);
  console.log(`  POST   ${BASE}/students            — create`);
  console.log(`  PUT    ${BASE}/students/:id         — full update`);
  console.log(`  PATCH  ${BASE}/students/:id         — partial update`);
  console.log(`  DELETE ${BASE}/students/:id         — remove`);
  console.log(`  GET    ${BASE}/http-reference       — HTTP methods/codes reference`);
  console.log('\n  Try: curl http://localhost:3000/api/v1/students | json_pp');
  console.log('  Or open the URL in a browser.\n');
});
