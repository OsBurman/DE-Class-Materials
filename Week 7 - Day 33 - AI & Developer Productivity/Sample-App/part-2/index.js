import express from 'express';

const app = express();
const PORT = 3000;

app.use(express.json());

// ─── GET / ────────────────────────────────────────────────────────────────────
app.get('/', (req, res) => {
  res.send(`
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>AI Tools in Practice</title>
      <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; background: #f5f5f5; }
        h1   { color: #333; border-bottom: 2px solid #6a0dad; padding-bottom: 10px; }
        h2   { color: #555; margin-top: 30px; }
        .endpoint { background: #fff; border-left: 4px solid #6a0dad; padding: 12px 16px; margin: 10px 0; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .get  { display: inline-block; background: #6a0dad; color: #fff; padding: 2px 8px; border-radius: 3px; font-size: 0.85em; font-weight: bold; margin-right: 8px; }
        .post { display: inline-block; background: #2e7d32; color: #fff; padding: 2px 8px; border-radius: 3px; font-size: 0.85em; font-weight: bold; margin-right: 8px; }
        .path  { font-family: monospace; font-weight: bold; }
        .desc  { color: #666; margin-top: 6px; font-size: 0.95em; }
        .note  { color: #888; font-size: 0.88em; margin-top: 4px; font-style: italic; }
        .setup { background: #1e1e1e; color: #d4d4d4; padding: 16px; border-radius: 6px; font-family: monospace; margin: 20px 0; }
        a      { color: #6a0dad; text-decoration: none; }
        a:hover { text-decoration: underline; }
      </style>
    </head>
    <body>
      <h1>🛠️ AI Tools in Practice</h1>
      <p>Hands-on demo of AI-assisted developer workflows — prompt templates, Copilot guide, debugging strategies, and mock code analysis.</p>

      <div class="setup">
        <strong>Setup:</strong><br/>
        npm install &amp;&amp; npm start
      </div>

      <h2>Available Endpoints</h2>

      <div class="endpoint">
        <span class="get">GET</span>
        <a href="/" class="path">/</a>
        <div class="desc">This welcome page</div>
      </div>

      <div class="endpoint">
        <span class="get">GET</span>
        <a href="/api/prompt-templates" class="path">/api/prompt-templates</a>
        <div class="desc">Reusable prompt templates for code review, test generation, bug fixing, documentation, refactoring, architecture, SQL, and explanations</div>
      </div>

      <div class="endpoint">
        <span class="get">GET</span>
        <a href="/api/copilot-guide" class="path">/api/copilot-guide</a>
        <div class="desc">GitHub Copilot practical guide — keyboard shortcuts, chat commands, tips for better suggestions, and limitations</div>
      </div>

      <div class="endpoint">
        <span class="get">GET</span>
        <a href="/api/ai-debugging" class="path">/api/ai-debugging</a>
        <div class="desc">AI-assisted debugging workflow — step-by-step guide, good vs bad prompt examples, and common mistakes</div>
      </div>

      <div class="endpoint">
        <span class="post">POST</span>
        <span class="path">/api/analyze-code</span>
        <div class="desc">Mock code analysis demo — simulates AI code review response</div>
        <div class="note">Body: {"code": "...", "language": "java"}</div>
      </div>
    </body>
    </html>
  `);
});

// ─── GET /api/prompt-templates ───────────────────────────────────────────────
app.get('/api/prompt-templates', (req, res) => {
  res.json([
    {
      name: 'Code Review',
      purpose: 'Get a thorough AI review of any code for quality, correctness, and security',
      template:
        'You are a senior {{LANGUAGE}} developer. Review the following code for: correctness, performance, security, readability, and best practices. For each issue, provide the line/section, the problem, and the fix.\n\nCode to review:\n```{{LANGUAGE}}\n{{CODE}}\n```\n\nRespond in this format:\n## Summary\n[overall assessment]\n## Issues Found\n[numbered list of issues with fix for each]\n## Positive Aspects\n[what was done well]',
      example:
        'You are a senior Java developer. Review the following code for: correctness, performance, security, readability, and best practices. For each issue, provide the line/section, the problem, and the fix.\n\nCode to review:\n```Java\npublic User getUserById(int id) {\n    return userList.stream()\n        .filter(u -> u.getId() == id)\n        .findFirst().get();\n}\n```\n\nRespond in this format:\n## Summary\n[overall assessment]\n## Issues Found\n[numbered list of issues with fix for each]\n## Positive Aspects\n[what was done well]',
      tips: [
        'Replace {{LANGUAGE}} with: Java, JavaScript, TypeScript, Python, SQL, etc.',
        'You can add specific concerns: "pay special attention to SQL injection"',
        'Add "focus on Spring Boot best practices" for Spring-specific review',
      ],
    },
    {
      name: 'Unit Test Generation',
      purpose: 'Generate comprehensive unit tests for a method or class',
      template:
        'You are a testing expert. Generate comprehensive JUnit 5 unit tests for this {{LANGUAGE}} method. Include: happy path tests, edge cases, boundary conditions, and error cases. Use @ParameterizedTest where appropriate.\n\nMethod to test:\n```{{LANGUAGE}}\n{{CODE}}\n```\n\nExisting context (imports, class structure):\n{{CONTEXT}}',
      example:
        'You are a testing expert. Generate comprehensive JUnit 5 unit tests for this Java method. Include: happy path tests, edge cases, boundary conditions, and error cases. Use @ParameterizedTest where appropriate.\n\nMethod to test:\n```Java\npublic int divide(int a, int b) {\n    return a / b;\n}\n```\n\nExisting context (imports, class structure):\npublic class Calculator { ... }',
      tips: [
        'Fill {{CONTEXT}} with your class structure so AI understands how to write the test class',
        'Add "use Mockito for dependencies" if the method has injected services',
        'Specify "use AssertJ instead of JUnit assertions" if preferred',
      ],
    },
    {
      name: 'Bug Fix Assistant',
      purpose: 'Get targeted help fixing a specific bug with full context',
      template:
        'I have a bug in my {{LANGUAGE}} code. Help me fix it.\n\nError message:\n{{ERROR_MESSAGE}}\n\nStack trace (if available):\n{{STACK_TRACE}}\n\nRelevant code:\n```{{LANGUAGE}}\n{{CODE}}\n```\n\nWhat I expected to happen: {{EXPECTED}}\nWhat actually happened: {{ACTUAL}}\nWhat I\'ve already tried: {{TRIED}}',
      example:
        "I have a bug in my Java code. Help me fix it.\n\nError message:\njava.lang.NullPointerException: Cannot invoke method getName() on null\n\nStack trace (if available):\nat StudentService.getStudentById(StudentService.java:45)\n\nRelevant code:\n```Java\npublic String getStudentName(int id) {\n    Student s = repository.findById(id);\n    return s.getName();\n}\n```\n\nWhat I expected to happen: Returns the student's name\nWhat actually happened: Throws NullPointerException\nWhat I've already tried: Checked that the ID exists in the database",
      tips: [
        'Always include the full stack trace, not just the error message',
        'Keep {{CODE}} to the minimal relevant section — not the whole file',
        'Be specific in {{TRIED}} to avoid AI suggesting things you already tested',
      ],
    },
    {
      name: 'Documentation Generator',
      purpose: 'Generate professional documentation for your code',
      template:
        'Generate {{DOC_STYLE}} documentation for the following {{LANGUAGE}} code. Include: purpose, parameters (name, type, description for each), return value, throws, and a usage example.\n\nCode:\n```{{LANGUAGE}}\n{{CODE}}\n```',
      example:
        'Generate JavaDoc documentation for the following Java code. Include: purpose, parameters (name, type, description for each), return value, throws, and a usage example.\n\nCode:\n```Java\npublic List<Student> findByGradeAndActive(int grade, boolean active) {\n    return repository.findByGradeAndActive(grade, active);\n}\n```',
      tips: [
        '{{DOC_STYLE}} options: JavaDoc, JSDoc, Python docstring, XML doc comments',
        'Add "include @since and @author tags" for enterprise-style docs',
        'Works great for documenting legacy code you inherited',
      ],
    },
    {
      name: 'Refactoring Guide',
      purpose: 'Improve existing code with explained refactoring steps',
      template:
        'Refactor the following {{LANGUAGE}} code to improve it. Apply these principles: {{PRINCIPLES}} (e.g., SOLID, DRY, extract method). Explain each change you make and why.\n\nOriginal code:\n```{{LANGUAGE}}\n{{CODE}}\n```',
      example:
        "Refactor the following Java code to improve it. Apply these principles: Single Responsibility, DRY, extract method. Explain each change you make and why.\n\nOriginal code:\n```Java\npublic void processOrder(Order order) {\n    // 80-line method doing validation, pricing, tax, email, and DB save\n}\n```",
      tips: [
        '{{PRINCIPLES}} examples: SOLID, DRY, YAGNI, extract method, replace conditional with polymorphism',
        'Add "do not change external behavior or method signatures" to preserve compatibility',
        'Ask for refactoring in stages if the method is very large',
      ],
    },
    {
      name: 'Architecture Advisor',
      purpose: 'Get architecture recommendations for a new project or feature',
      template:
        'I am building a {{PROJECT_TYPE}} application with these requirements: {{REQUIREMENTS}}. My tech stack is: {{TECH_STACK}}.\n\nPlease recommend: 1) Overall architecture pattern, 2) Key components and their responsibilities, 3) Data flow, 4) Potential challenges and mitigations, 5) A simple diagram description.',
      example:
        'I am building a REST API application with these requirements: multi-tenant SaaS, 10k concurrent users, CRUD for students and courses. My tech stack is: Java 21, Spring Boot 3, PostgreSQL, deployed on AWS.\n\nPlease recommend: 1) Overall architecture pattern, 2) Key components and their responsibilities, 3) Data flow, 4) Potential challenges and mitigations, 5) A simple diagram description.',
      tips: [
        'Be specific about scale — "10k concurrent users" vs "small internal tool"',
        'Mention existing constraints: "must integrate with legacy Oracle DB"',
        'Follow up with: "What are the trade-offs of this approach vs microservices?"',
      ],
    },
    {
      name: 'SQL Query Builder',
      purpose: 'Generate optimized SQL queries with explanations',
      template:
        'Generate a {{SQL_DIALECT}} query for: {{REQUIREMENT}}. Schema: {{SCHEMA_DESCRIPTION}}. Requirements: should be performant, use appropriate indexes, handle NULLs properly. Explain the query.',
      example:
        "Generate a PostgreSQL query for: find all students enrolled in more than 3 active courses with an average grade above 80. Schema: students(id, name, email), courses(id, title, active), enrollments(student_id, course_id, grade). Requirements: should be performant, use appropriate indexes, handle NULLs properly. Explain the query.",
      tips: [
        '{{SQL_DIALECT}} options: MySQL, PostgreSQL, Oracle, SQL Server, SQLite',
        'Include sample data rows if your schema is complex',
        'Ask "also show the execution plan and which indexes would help"',
      ],
    },
    {
      name: 'Explain Code',
      purpose: 'Get a clear explanation of unfamiliar code at the right level',
      template:
        'Explain this {{LANGUAGE}} code to a {{AUDIENCE}} developer. Cover: what it does, how it works step by step, any design patterns used, and potential improvements.\n\nCode:\n```{{LANGUAGE}}\n{{CODE}}\n```',
      example:
        'Explain this Java code to a junior developer. Cover: what it does, how it works step by step, any design patterns used, and potential improvements.\n\nCode:\n```Java\npublic class EventBus {\n    private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();\n    // ...\n}\n```',
      tips: [
        '{{AUDIENCE}} options: junior, mid-level, senior, non-technical manager, student',
        'Add "use analogies to real-world concepts" for beginners',
        'Follow up: "What are common mistakes developers make with this pattern?"',
      ],
    },
  ]);
});

// ─── GET /api/copilot-guide ──────────────────────────────────────────────────
app.get('/api/copilot-guide', (req, res) => {
  res.json({
    title: 'GitHub Copilot Practical Guide',
    keyboardShortcuts: {
      Tab: 'Accept entire suggestion',
      'Ctrl+Right (Cmd+Right on Mac)': 'Accept next word of suggestion',
      Esc: 'Dismiss suggestion',
      'Alt+] (Option+] on Mac)': 'Next suggestion',
      'Alt+[ (Option+[ on Mac)': 'Previous suggestion',
      'Ctrl+Enter': 'Open Copilot panel with multiple suggestions',
    },
    chatCommands: [
      {
        command: '/explain',
        description: 'Explain selected code',
        example: 'Select a method, type /explain',
      },
      {
        command: '/fix',
        description: 'Fix a bug in selected code',
        example: 'Select broken code, type /fix',
      },
      {
        command: '/tests',
        description: 'Generate unit tests for selected code',
        example: 'Select a class, type /tests',
      },
      {
        command: '/doc',
        description: 'Generate documentation for selected code',
        example: 'Select a method, type /doc',
      },
      {
        command: '@workspace',
        description: 'Ask questions about your entire codebase',
        example: '@workspace where is the authentication logic?',
      },
      {
        command: '#file',
        description: 'Reference a specific file in your question',
        example: '#UserService.java What does the createUser method do?',
      },
      {
        command: '@terminal',
        description: 'Get help with terminal commands',
        example: '@terminal how do I list all running Java processes?',
      },
    ],
    gettingBetterSuggestions: [
      {
        tip: 'Write a descriptive comment first',
        bad: '// process',
        good: '// Validate email format, check for duplicates in DB, hash password with BCrypt, save to DB',
      },
      {
        tip: 'Use descriptive function names',
        bad: 'void process(String s)',
        good: 'void registerNewStudent(String email)',
      },
      {
        tip: 'Keep functions small and focused (Single Responsibility)',
        description: 'Copilot works best with clear, focused functions',
      },
      {
        tip: 'Provide type information',
        description: 'Typed parameters give Copilot more context for suggestions',
      },
      {
        tip: 'Open related files',
        description: 'Copilot uses open editor tabs as context',
      },
    ],
    limitations: [
      'May generate plausible-looking but incorrect code',
      'May not know about recent library versions or APIs',
      'May introduce subtle security vulnerabilities',
      'May miss edge cases and error handling',
      'Always review, test, and understand generated code',
      'Never include API keys, passwords, or secrets in prompts',
    ],
  });
});

// ─── GET /api/ai-debugging ───────────────────────────────────────────────────
app.get('/api/ai-debugging', (req, res) => {
  res.json({
    title: 'Using AI for Debugging',
    workflow: [
      {
        step: 1,
        action: 'Isolate the minimal reproducing example',
        why: 'Smaller code = better AI analysis. Remove unrelated code.',
      },
      {
        step: 2,
        action: 'Collect the full error message and stack trace',
        why: 'AI needs the exact error text, not paraphrased',
      },
      {
        step: 3,
        action: 'Note expected vs actual behavior',
        why: 'Helps AI understand the intent of the code',
      },
      {
        step: 4,
        action: "List what you've already tried",
        why: "Prevents AI from suggesting things that didn't work",
      },
      {
        step: 5,
        action: 'Include relevant code context',
        why: 'The method with the bug + called methods + data structures',
      },
    ],
    promptComparison: {
      bad: {
        prompt: "my code doesn't work fix it",
        why: 'No context, no error, no code provided — AI cannot help',
      },
      mediocre: {
        prompt:
          "I'm getting a NullPointerException in my Spring Boot app. Here's the code: [entire 500-line file]",
        why: 'Too much code, no specific error location, no description of expected behavior',
      },
      good: {
        prompt:
          "I'm getting a NullPointerException at StudentService.java:45. Expected: getStudentById returns a Student object. Actual: throws NullPointerException.\n\nError:\njava.lang.NullPointerException: Cannot invoke method getName() on null\n  at StudentService.getStudentById(StudentService.java:45)\n\nRelevant code:\n[just the 20-line method]\n\nI've already checked: studentRepository.findById() returns Optional which I'm handling with orElseThrow",
        why: 'Specific error location, exact error message, minimal code, expected vs actual, prior attempts',
      },
    },
    commonMistakes: [
      'Pasting your entire codebase — include only relevant methods',
      'Not including the error message — always include the full stack trace',
      "Vague descriptions — 'doesn't work' vs 'throws NullPointerException at line 45'",
      'Not mentioning your framework/version — Spring Boot 3 vs Spring Boot 2 have different APIs',
      'Accepting the first AI suggestion without understanding it',
    ],
  });
});

// ─── POST /api/analyze-code ──────────────────────────────────────────────────
app.post('/api/analyze-code', (req, res) => {
  const { code = '', language = 'unknown' } = req.body;

  res.json({
    disclaimer:
      'This is a DEMO mock response. In a real implementation, this would call OpenAI/Claude API with your code. To implement real AI code analysis, see /api/prompt-templates for the Code Review template.',
    realImplementationSteps: [
      '1. Sign up for OpenAI API key at platform.openai.com',
      '2. npm install openai',
      '3. Use the Code Review prompt template from /api/prompt-templates',
      '4. Call openai.chat.completions.create() with your prompt',
    ],
    mockAnalysis: {
      language,
      linesAnalyzed: code.length > 0 ? code.split('\n').length : 0,
      summary:
        'This appears to be a well-structured piece of code. Here are observations from a simulated analysis:',
      observations: [
        {
          category: 'Code Style',
          finding:
            'Variable and method names appear descriptive and follow conventions',
          severity: 'info',
        },
        {
          category: 'Error Handling',
          finding:
            'Consider adding try-catch blocks for potential runtime exceptions',
          severity: 'suggestion',
        },
        {
          category: 'Performance',
          finding:
            'If iterating over large collections, consider streams or parallel processing',
          severity: 'suggestion',
        },
        {
          category: 'Security',
          finding: 'Ensure all user inputs are validated before processing',
          severity: 'warning',
        },
        {
          category: 'Testing',
          finding:
            'Add unit tests covering happy path, null inputs, and boundary conditions',
          severity: 'suggestion',
        },
      ],
      suggestedTests: [
        'Test with null inputs',
        'Test with empty strings/collections',
        'Test boundary values (0, negative numbers, max values)',
        'Test the happy path with valid data',
      ],
    },
  });
});

// ─── Start server ─────────────────────────────────────────────────────────────
app.listen(PORT, () => {
  console.log('=================================');
  console.log('AI Tools in Practice Demo');
  console.log('=================================');
  console.log(`Server running at http://localhost:${PORT}`);
  console.log('');
  console.log('Available endpoints:');
  console.log('  GET /                      - Welcome page');
  console.log('  GET /api/prompt-templates  - Reusable prompt templates');
  console.log('  GET /api/copilot-guide     - GitHub Copilot practical guide');
  console.log('  GET /api/ai-debugging      - AI-assisted debugging workflow');
  console.log('  POST /api/analyze-code     - Mock code analysis demo');
  console.log('                               Body: {"code":"...", "language":"java"}');
  console.log('');
  console.log('Run: npm install && npm start');
  console.log('=================================');
});
