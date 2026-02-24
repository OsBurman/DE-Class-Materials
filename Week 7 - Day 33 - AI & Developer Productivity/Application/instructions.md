# Day 33 Application — AI & Developer Productivity: AI-Assisted Coding Workshop

## Overview

A hands-on workshop to practice using **AI tools** (GitHub Copilot, ChatGPT, etc.) effectively as a developer. You will practice prompt engineering, AI-assisted debugging, code review, and documentation generation.

---

## Learning Goals

- Write effective prompts (zero-shot, few-shot, chain-of-thought)
- Use AI to generate boilerplate and accelerate coding
- Critically evaluate AI output for bugs, security issues, and best practices
- Generate documentation with AI
- Use AI for debugging and code review
- Understand AI limitations and when NOT to use it

---

## The Challenge

You are building a **Student Grade Calculator** in Python. You will use AI tools to help — but you must evaluate and improve every piece of AI output.

---

## Part 1 — Prompt Engineering Practice

**Task 1 — Zero-Shot Prompting**  
Open your AI tool and ask:
> "Write a Python function that calculates a student's GPA given a list of grades."

Paste the output into `src/gpa_calculator.py`. Add a comment above: `# AI Generated — Zero-Shot`.

**Task 2 — Few-Shot Prompting**  
Write a new prompt with 2–3 examples first, then ask for the same function.
```
Input:  [{"course": "Math", "grade": "A", "credits": 3}, ...]
Output: GPA as a float

Example 1: ...
Example 2: ...

Now write the function.
```
Compare to Task 1. Note differences in `prompt-notes.md`.

**Task 3 — Chain-of-Thought Prompting**  
Ask: "Think step-by-step. What data validation should I add to a GPA calculator? Then write the validated version."  
Add validation to `gpa_calculator.py`.

---

## Part 2 — AI-Assisted Development

**Task 4 — Complete this stub with AI:**  
Open `src/grade_report.py`. Use AI to complete the `TODO` sections. Then review the output for:
- Correctness (does it do what was asked?)
- Edge cases (empty list, None values, invalid grades?)
- Security (any obvious issues?)
Write a 3-line review in `prompt-notes.md`.

**Task 5 — Bug Hunt**  
`src/buggy_calculator.py` is provided with 5 intentional bugs. Ask your AI tool: "Find and explain all bugs in this code." Compare AI findings to `bugs.md` (provided). Did it catch them all?

---

## Part 3 — AI Documentation

**Task 6**  
Ask AI to generate:
1. Docstrings for all functions in `gpa_calculator.py`
2. A `README.md` for this project
3. Inline comments for the most complex function

Evaluate: Is the documentation accurate? Are the docstrings complete?

---

## Part 4 — Reflection

**Task 7 — `reflection.md`**  
Answer these questions (2–3 sentences each):
1. Which prompting technique gave the best results? Why?
2. What mistakes did the AI make that you had to fix?
3. In what situations would you NOT use AI assistance?
4. How did AI change your speed? Your code quality?

---

## Submission Checklist

- [ ] `gpa_calculator.py` — complete with docstrings and validation
- [ ] `grade_report.py` — all TODOs completed (with AI help)
- [ ] `prompt-notes.md` — comparisons documented
- [ ] `reflection.md` — all 4 questions answered
- [ ] Evidence of AI review (comments noting AI output vs your edits)
