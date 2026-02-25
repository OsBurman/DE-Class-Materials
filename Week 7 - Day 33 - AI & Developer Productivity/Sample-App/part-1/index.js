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
      <title>AI & Developer Productivity</title>
      <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 0 20px; background: #f5f5f5; }
        h1   { color: #333; border-bottom: 2px solid #007acc; padding-bottom: 10px; }
        h2   { color: #555; margin-top: 30px; }
        .endpoint { background: #fff; border-left: 4px solid #007acc; padding: 12px 16px; margin: 10px 0; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
        .method  { display: inline-block; background: #007acc; color: #fff; padding: 2px 8px; border-radius: 3px; font-size: 0.85em; font-weight: bold; margin-right: 8px; }
        .path    { font-family: monospace; font-weight: bold; }
        .desc    { color: #666; margin-top: 6px; font-size: 0.95em; }
        .setup   { background: #1e1e1e; color: #d4d4d4; padding: 16px; border-radius: 6px; font-family: monospace; margin: 20px 0; }
        a        { color: #007acc; text-decoration: none; }
        a:hover  { text-decoration: underline; }
      </style>
    </head>
    <body>
      <h1>🤖 AI &amp; Developer Productivity</h1>
      <p>Welcome! This demo app covers key AI concepts and tools for developers.</p>

      <div class="setup">
        <strong>Setup:</strong><br/>
        npm install &amp;&amp; npm start
      </div>

      <h2>Available Endpoints</h2>

      <div class="endpoint">
        <span class="method">GET</span>
        <a href="/" class="path">/</a>
        <div class="desc">This welcome page — lists all endpoints</div>
      </div>

      <div class="endpoint">
        <span class="method">GET</span>
        <a href="/api/ai-concepts" class="path">/api/ai-concepts</a>
        <div class="desc">AI &amp; LLM fundamentals — LLMs, tokens, temperature, prompting strategies, RAG, embeddings, hallucination</div>
      </div>

      <div class="endpoint">
        <span class="method">GET</span>
        <a href="/api/prompt-engineering" class="path">/api/prompt-engineering</a>
        <div class="desc">Prompt engineering techniques — role prompting, chain of thought, output formatting, few-shot examples, and best practices</div>
      </div>

      <div class="endpoint">
        <span class="method">GET</span>
        <a href="/api/dev-tools" class="path">/api/dev-tools</a>
        <div class="desc">AI developer tools reference — GitHub Copilot, ChatGPT, Claude, Cursor, and practical usage tips</div>
      </div>

      <div class="endpoint">
        <span class="method">GET</span>
        <a href="/api/responsible-ai" class="path">/api/responsible-ai</a>
        <div class="desc">Responsible AI practices — accuracy, PII, prompt injection, OWASP LLM Top 10, intellectual property</div>
      </div>
    </body>
    </html>
  `);
});

// ─── GET /api/ai-concepts ────────────────────────────────────────────────────
app.get('/api/ai-concepts', (req, res) => {
  res.json({
    title: 'AI Concepts for Developers',
    concepts: [
      {
        name: 'Large Language Models (LLMs)',
        description:
          'Neural networks trained on massive text datasets that can understand and generate human language and code.',
        examples: ['GPT-4', 'Claude', 'Gemini'],
        howTheyWork:
          'Trained on vast text data, predict next token based on context',
      },
      {
        name: 'Tokens',
        description:
          'Units of text that LLMs process (roughly 4 chars or 0.75 words)',
        contextWindow: 'GPT-4: 128k tokens, Claude: 200k tokens',
        tip: 'Long prompts use more tokens = higher cost',
      },
      {
        name: 'Temperature',
        description: 'Controls randomness in AI output',
        values: {
          0: 'Deterministic - same output every time (good for code, facts)',
          0.7: 'Balanced (good for general use)',
          1: 'Creative/random (good for brainstorming, creative writing)',
        },
      },
      {
        name: 'System Prompt',
        description: "Instructions that set the AI's persona and rules",
        example:
          'You are a senior Java developer who writes clean, well-tested code.',
      },
      {
        name: 'Zero-shot Prompting',
        description: 'Ask directly without examples',
        example: 'Explain the Singleton design pattern',
      },
      {
        name: 'Few-shot Prompting',
        description: 'Provide examples before your question',
        example:
          'Q: What pattern is this? [code] A: Observer. Q: What pattern is this? [your code] A:',
      },
      {
        name: 'Chain of Thought',
        description: 'Ask AI to reason step by step',
        example:
          'Think step by step: How would you design a REST API for a library system?',
      },
      {
        name: 'RAG (Retrieval Augmented Generation)',
        description: 'Supplement AI with external knowledge',
        steps: [
          'Retrieve relevant docs from knowledge base',
          'Augment prompt with retrieved context',
          'Generate response grounded in your data',
        ],
        useCases: [
          'Company knowledge bases',
          'Documentation Q&A',
          'Code-aware assistants',
        ],
      },
      {
        name: 'Embeddings',
        description:
          'Numerical vector representations of text for semantic search',
        example:
          "'dog' and 'puppy' have similar embeddings even though different words",
      },
      {
        name: 'Hallucination',
        description: 'AI confidently stating incorrect information',
        prevention: [
          'RAG for grounding',
          'Lower temperature for factual tasks',
          'Ask AI to cite sources',
          'Always verify critical output',
        ],
      },
    ],
  });
});

// ─── GET /api/prompt-engineering ────────────────────────────────────────────
app.get('/api/prompt-engineering', (req, res) => {
  res.json({
    title: 'Prompt Engineering Guide',
    techniques: [
      {
        name: 'Role Prompting',
        description: 'Assign a persona to the AI',
        example:
          'You are a senior Java developer with 10 years of experience. Review this code for best practices:',
        whenToUse:
          'When you need domain-specific expertise or a specific communication style',
      },
      {
        name: 'Chain of Thought',
        description: 'Ask AI to show its reasoning',
        example:
          "Let's think step by step. I need to design a database schema for an e-commerce app. What tables do I need and why?",
        whenToUse: 'Complex problems, architecture decisions, debugging',
      },
      {
        name: 'Output Format Specification',
        description: 'Specify exactly what format you want',
        example:
          'Return your answer as JSON with fields: {className, methods: [{name, params, returnType, description}]}',
        whenToUse: 'When you need structured data for further processing',
      },
      {
        name: 'Constraints',
        description: 'Limit scope and length',
        example:
          'In exactly 3 bullet points, list the main benefits of microservices',
        whenToUse: 'When you need concise, focused answers',
      },
      {
        name: 'Few-shot Examples',
        description: 'Show input→output examples before your question',
        example:
          'Convert these to camelCase: user_name → userName, first_name → firstName. Now convert: phone_number →',
        whenToUse:
          'Pattern transformation tasks, code style conversion, formatting',
      },
      {
        name: 'Negative Prompting',
        description: 'Tell AI what NOT to do',
        example:
          'Write a REST controller for Students. Do NOT use field injection (@Autowired on fields). Do NOT use raw types. Use constructor injection.',
        whenToUse: 'When you know common mistakes to avoid',
      },
      {
        name: 'Meta-prompting',
        description: 'Ask AI to improve your prompt',
        example:
          'I want to ask an AI to help me write better Java code. Here is my prompt: [your prompt]. How can I improve it?',
        whenToUse: 'When your prompts are not giving good results',
      },
    ],
    bestPractices: [
      'Be specific and detailed',
      'Provide context about your tech stack',
      'Specify your experience level',
      'Include constraints (language version, framework)',
      'Break complex tasks into steps',
    ],
  });
});

// ─── GET /api/dev-tools ──────────────────────────────────────────────────────
app.get('/api/dev-tools', (req, res) => {
  res.json({
    title: 'AI Developer Tools Reference',
    tools: [
      {
        name: 'GitHub Copilot',
        type: 'IDE Plugin',
        description: 'AI pair programmer built into VS Code/JetBrains',
        features: [
          'Inline code completion (Tab to accept)',
          'Copilot Chat (/explain /fix /tests /doc)',
          '@workspace for codebase-aware questions',
          '#file to reference specific files',
          'Terminal suggestions',
        ],
        tips: [
          'Write a comment describing what you want before the function',
          'Use descriptive function names',
          'Accept partial suggestions with Ctrl+Right',
        ],
        cost: 'Paid ($10/mo individual, free for students/open source)',
      },
      {
        name: 'ChatGPT',
        type: 'Web/API',
        description: 'General-purpose AI from OpenAI',
        bestFor: [
          'Architecture decisions',
          'Explaining concepts',
          'Debugging help',
          'Writing documentation',
          'Code review',
        ],
        tip: 'Use GPT-4 for complex code, GPT-3.5 for quick questions',
      },
      {
        name: 'Claude (Anthropic)',
        type: 'Web/API',
        description: 'AI assistant known for long context and code quality',
        bestFor: [
          'Large codebase analysis',
          'Technical writing',
          'Following complex instructions',
          'Code generation',
        ],
        tip: 'Excellent at understanding and modifying existing code',
      },
      {
        name: 'Cursor',
        type: 'AI-native IDE',
        description: 'VS Code fork with deep AI integration',
        features: [
          'Chat with your entire codebase',
          'AI-powered autocomplete',
          'Inline edits with Cmd+K',
          'Composer for multi-file edits',
        ],
        tip: 'Great for large codebase navigation and refactoring',
      },
    ],
    aiForDevelopment: {
      writingTests:
        'Describe the method, ask AI to generate unit tests with edge cases',
      debugging:
        'Paste error + stack trace + relevant code, describe expected vs actual behavior',
      codeReview:
        'Ask AI to review for: performance, security, readability, best practices',
      documentation: 'Ask AI to write JavaDoc/JSDoc for your functions',
      refactoring:
        'Show code and ask AI to apply [SOLID/DRY/specific pattern]',
      learning:
        "Ask AI to explain code you don't understand, or compare approaches",
    },
  });
});

// ─── GET /api/responsible-ai ─────────────────────────────────────────────────
app.get('/api/responsible-ai', (req, res) => {
  res.json({
    title: 'Responsible AI for Developers',
    topics: [
      {
        name: 'Accuracy & Verification',
        description:
          'AI can hallucinate. Always verify generated code runs. Never ship untested AI code.',
        rules: [
          'Run all AI-generated code',
          'Test edge cases AI might miss',
          'Verify facts and algorithm correctness',
        ],
      },
      {
        name: 'Privacy & PII',
        description: 'Never paste sensitive data into public AI tools',
        examples: [
          'Customer emails/names',
          'Passwords or API keys',
          'Private business logic',
          'HIPAA/PII data',
        ],
        alternatives: [
          'Use enterprise/private AI deployments',
          'Anonymize data before using AI',
          'Use local models (Ollama) for sensitive code',
        ],
      },
      {
        name: 'Prompt Injection',
        description: 'Malicious input that hijacks AI behavior',
        example:
          "If user input is passed directly to AI: 'Ignore previous instructions and...'",
        prevention: [
          'Sanitize user input before including in prompts',
          'Use system prompts to constrain AI behavior',
          'Never let user input override system instructions',
        ],
      },
      {
        name: 'OWASP LLM Top 10 Overview',
        items: [
          'LLM01: Prompt Injection',
          'LLM02: Insecure Output Handling',
          'LLM03: Training Data Poisoning',
          'LLM04: Model Denial of Service',
          'LLM05: Supply Chain Vulnerabilities',
          'LLM06: Sensitive Information Disclosure',
          'LLM07: Insecure Plugin Design',
          'LLM08: Excessive Agency',
          'LLM09: Overreliance',
          'LLM10: Model Theft',
        ],
      },
      {
        name: 'Intellectual Property',
        description: 'Understand licensing for AI-generated code',
        considerations: [
          'Code may resemble training data — check for novel algorithms',
          'Your company may have policies on AI tool usage',
          'Disclose AI use when required',
        ],
      },
    ],
  });
});

// ─── Start server ─────────────────────────────────────────────────────────────
app.listen(PORT, () => {
  console.log('=================================');
  console.log('AI & Developer Productivity Demo');
  console.log('=================================');
  console.log(`Server running at http://localhost:${PORT}`);
  console.log('');
  console.log('Available endpoints:');
  console.log('  GET /                     - Welcome page');
  console.log('  GET /api/ai-concepts      - AI/LLM fundamentals');
  console.log('  GET /api/prompt-engineering - Prompt engineering techniques');
  console.log('  GET /api/dev-tools        - AI developer tools guide');
  console.log('  GET /api/responsible-ai   - Responsible AI practices');
  console.log('');
  console.log('Run: npm install && npm start');
  console.log('=================================');
});
