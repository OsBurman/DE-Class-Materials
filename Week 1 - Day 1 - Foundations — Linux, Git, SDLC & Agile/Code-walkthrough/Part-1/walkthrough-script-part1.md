# WEEK 1 - DAY 1 | PART 1 | SPEAKING SCRIPT
# Topics: Linux OS Fundamentals, Shell Scripting, SDLC & Fullstack Overview

---

## HOW TO USE THIS SCRIPT

- **[ACTION]** = something you do (open file, run command, type something)
- **[ASK]** = pause and ask the class this question before continuing
- **⚠️ WATCH OUT** = common mistake or confusion to call out
- **→ TRANSITION** = bridge phrase to the next topic

---

---

# FILE 1: `01-linux-commands.sh`

---

## OPENING (Before showing any code)

"Good morning everyone — welcome to Day 1. Today we're laying the foundation for everything else in this course. We're going to talk about Linux, shell scripting, and how the entire software development process works. None of this is glamorous, but I promise — by the end of this week you'll understand why developers use these tools every single day.

Let's start with Linux."

---

## SECTION 1: Understanding the Linux File System

**[ACTION]** Open `01-linux-commands.sh`. Scroll to Section 1.

"Before we run a single command, I want you to understand the world you're operating in. Linux is not Windows. There's no C drive, no D drive. There is ONE tree that starts at the root — which we write as a forward slash `/`.

Everything in Linux is a file. Directories are files. Devices are files. Your running processes? Files. Once you understand that, Linux starts to make a lot more sense."

**[ACTION]** Show the directory list in the comments.

"Let's walk through the directories you'll actually care about as a developer:
- `/home` — this is YOUR space. When you open a terminal, you start here.
- `/etc` — configuration files. If you install something and need to configure it, the config file is probably here.
- `/var` — variable data. Application logs live here. When something breaks in production, you come to `/var/log`.
- `/tmp` — temporary files. Anything here gets wiped on reboot.
- `/usr` — installed programs and their libraries.

**[ASK]** "What do you think happens if you delete something in `/etc` by accident?"

*Wait for answers, then say:* "Exactly — you could break system configuration. That's why we always use `sudo` carefully and never run as root unless necessary."

**[ACTION]** Run: `pwd` then `ls /`

"Let's run these. `pwd` — print working directory. This tells you exactly where you are. `ls /` shows you the root of the file system."

---

## SECTION 2: Navigating the File System

**[ACTION]** Walk through the navigation commands line by line.

"`pwd` — you'll type this constantly. Whenever you're lost, `pwd` tells you where you are.

`ls` — list. The most basic command. But watch what happens when I add flags."

**[ACTION]** Run: `ls` then `ls -l` then `ls -la`

"See the difference? `-l` gives us the long format — permissions, owner, file size, modification date. `-a` shows hidden files — those are files that start with a dot, like `.gitignore`. We'll see those a lot.

**[ACTION]** Run: `cd ~` and explain.

"`cd ~` takes you home. The tilde `~` always means your home directory. Doesn't matter where you are — `cd ~` gets you back.

`cd ..` goes up one level. `cd -` goes back to where you just were. That last one is super useful when you're jumping between two directories."

**[ACTION]** Run: `mkdir my-project` then `mkdir -p my-project/src/main/java`

"Now `mkdir`. Without `-p`, if the parent directories don't exist, it fails. With `-p`, it creates the entire chain. You'll use this constantly when setting up project structures."

⚠️ **WATCH OUT:** "A very common mistake is forgetting the `-p` flag when creating nested directories. You'll get an error saying the parent doesn't exist. Add `-p` and it works."

---

## SECTION 3: Working with Files

**[ACTION]** Walk through file creation and manipulation commands.

"Let's create and work with some files. `touch` creates an empty file — or if the file already exists, it updates the timestamp. 

`echo` prints text. When we redirect with `>` it writes to a file — **replacing** whatever was there. When we use `>>` it **appends** — adds to the end. This distinction matters."

**[ASK]** "If I have a file with 100 lines and I use `echo 'hello' > myfile.txt` — what happens?"

*Answer:* "The entire file gets replaced with just the word 'hello'. Use `>>` if you want to add to it."

**[ACTION]** Run: `echo "# My First Project" > README.md` then `cat README.md` then `echo "This is a fullstack training project." >> README.md` then `cat README.md`

"See how `cat` shows the contents. And notice the second `echo` with `>>` added a new line rather than replacing."

"For copying: `cp source destination`. For moving or renaming: `mv`. To delete: `rm`. To delete a whole directory: `rm -rf`."

⚠️ **WATCH OUT:** "`rm -rf` has no undo. There is no trash can. If you run `rm -rf /` on a Linux server, you will delete the entire operating system. Always double-check your path before running it."

---

## SECTION 4: Finding Things

**[ACTION]** Show the `find` and `grep` commands.

"`find` searches for files by name, date, size — whatever you need. `grep` searches for text INSIDE files. These two commands together are incredibly powerful.

Watch: `grep -rni 'project' .` — this recursively searches every file in the current directory for the word 'project', case-insensitive, and shows the line numbers."

**[ASK]** "When would you use `grep` in a real development scenario?"

*Guide toward:* "Searching a codebase for where a function is used, finding all usages of a config value, looking for a specific error message in log files."

---

## SECTION 5: File Permissions

**[ACTION]** Run: `ls -l 01-linux-commands.sh`

"This is one of the things that trips up new developers the most. Let me break it down.

The first character: `-` means file, `d` means directory.

Then three groups of three: `rwx` for owner, `rwx` for group, `rwx` for everyone else.
- `r` = read = 4
- `w` = write = 2  
- `x` = execute = 1

So `rwx` = 7, `r-x` = 5, `r--` = 4."

**[ACTION]** Run: `chmod +x 02-shell-scripting.sh` then `ls -l 02-shell-scripting.sh`

"See how the `x` appeared in the permissions? That's what makes a script runnable. Without it, you'd get a 'Permission denied' error even if the file exists."

⚠️ **WATCH OUT:** "When you create a `.sh` file, it's NOT executable by default. You MUST run `chmod +x filename.sh` before you can run it. This is the most common reason a script fails on the first attempt."

---

## SECTION 6: Useful Everyday Commands

**[ACTION]** Walk through the utility commands.

"Quick tour of commands you'll use every day:

`whoami` — tells you who you're logged in as. Critical when you're SSH'd into a remote server.
`ps aux` — shows all running processes. You'll use this when something is running and you need to find its process ID to kill it.
`ls -la | grep '.md'` — and HERE is the pipe operator. The `|` takes the output of the left command and sends it as input to the right command. This is one of the most powerful concepts in Linux."

**[ASK]** "What do you think `history | grep mkdir` would do?"

*Answer:* "It would show every `mkdir` command you've ever run in this terminal."

→ **TRANSITION:** "Alright — now that we know our way around Linux, let's take it a step further. Instead of typing commands one at a time, what if we could save them and run them automatically? That's shell scripting."

---

---

# FILE 2: `02-shell-scripting.sh`

---

## OPENING

**[ACTION]** Open `02-shell-scripting.sh`.

"A shell script is just a text file containing Linux commands. But when you add variables, conditionals, loops, and functions — it becomes a small program. Developers use shell scripts to automate deployments, set up project structures, run tests, and so much more."

**[ACTION]** Point out the shebang line: `#!/bin/bash`

"This first line is called the **shebang**. It tells the OS: 'when you run this file, use bash to interpret it.' Without this line, the system doesn't know how to execute the script."

---

## SECTION 1: Variables

**[ACTION]** Walk through variable declarations.

"Variables in bash are straightforward, but there's one rule you CANNOT break:"

⚠️ **WATCH OUT:** "No spaces around the equals sign. `NAME = 'Alice'` will fail. It MUST be `NAME='Alice'`. This catches everyone at least once."

"To USE a variable, put a `$` in front of it. And notice the `readonly` keyword — that's how we make a constant in bash.

This is command substitution:" 

**[ACTION]** Point to `CURRENT_DATE=$(date +"%Y-%m-%d")`

"The `$()` syntax runs the command inside it and stores the result. So `CURRENT_DATE` now holds today's date as a string."

**[ACTION]** Run the script up through Section 1 and show output.

---

## SECTION 2: User Input

**[ACTION]** Show the `read` command.

"The `read -p` command pauses the script and waits for the user to type something. The `-p` flag lets you show a prompt message. Whatever they type gets stored in the variable."

---

## SECTION 3: Conditionals

**[ACTION]** Walk through the `if/elif/else` block.

"In bash, conditions go inside square brackets with spaces: `[ $SCORE -ge 90 ]`. 

The comparison operators in bash are different from what you'll see in Java or JavaScript:
- `-ge` = greater than or equal (≥)
- `-gt` = greater than (>)
- `-le` = less than or equal (≤)
- `-lt` = less than (<)
- `-eq` = equal (==)
- `-ne` = not equal (!=)"

**[ASK]** "If `SCORE=80`, which branch runs?"

**[ACTION]** Show the file existence check: `[ -f "README.md" ]`

"`-f` checks if a file exists and is a regular file. `-d` checks for a directory. These are incredibly useful for defensive scripting — always check before you act."

---

## SECTION 4: Loops

**[ACTION]** Walk through the `for` loops and `while` loop.

"The `for...in` loop iterates over a list. The C-style `for` loop with `(( ))` works just like in other languages. The `while` loop runs as long as the condition is true.

Notice `$((COUNTER - 1))` — the double parentheses are how bash does arithmetic. Regular parentheses and `$()` are for command substitution."

**[ASK]** "What would happen if I forgot `COUNTER=$((COUNTER - 1))` in the while loop?"

*Answer:* "Infinite loop. Always make sure your loop has a way to exit."

---

## SECTION 5: Functions

**[ACTION]** Walk through the function definitions.

"Functions in bash are defined with a name and curly braces. Arguments come in as `$1`, `$2`, `$3`, etc. Notice the `local` keyword — this keeps the variable scoped to the function. Without `local`, it's a global variable."

**[ACTION]** Show the `add_numbers` function and the `$()` call.

"Since bash doesn't have a traditional return value for non-integer data, we use `echo` to output the result and command substitution `$()` to capture it. This pattern is widely used."

---

## SECTION 6: Practical Script — Project Setup

**[ACTION]** Walk through the `setup_project` function slowly.

"This is the kind of thing a developer actually writes. Instead of manually creating the same folder structure for every new project, you write it once as a function and call it. This is the power of scripting — automation."

**[ACTION]** Run the script and show the output.

→ **TRANSITION:** "Excellent. You now know Linux commands and how to automate them with scripts. Before we get to Git in Part 2, let's zoom out and understand the big picture — the process that governs ALL software development."

---

---

# FILE 3: `03-sdlc-overview.md`

---

## OPENING

**[ACTION]** Open `03-sdlc-overview.md`.

"This file is different — it's a reference document, not runnable code. SDLC is a concept, so we're going to talk through it together.

**The question I want you to think about is:** before a developer writes a single line of code, what has already happened? And after they write the code, what still needs to happen?"

---

## The 7 Stages

**[ACTION]** Show the ASCII diagram.

"Let's walk through each stage. As I do, I want you to think about where you've seen this before — even if you didn't know the name for it."

**Stage 1 — Planning:** "This is where the business decides: are we doing this? Is it worth it? How long will it take? How much will it cost? You'll experience this as sprint planning in Agile."

**Stage 2 — Requirements:** "Talk to the stakeholders. What does the system NEED to do? This gets written as user stories: 'As a customer, I want to see my order history so that I can track past purchases.' You'll write these in Jira."

**Stage 3 — Design:** "Before anyone codes: what's the database look like? What are the API endpoints? What does the UI look like? This prevents expensive rework later."

**Stage 4 — Implementation:** "This is where you live. Writing code, doing code reviews, making pull requests. But notice — it's just ONE of seven stages."

⚠️ **WATCH OUT:** "Junior developers often think their job is just Stage 4. But understanding all 7 stages makes you a better developer because you understand WHY you're building what you're building."

**Stage 5 — Testing:** "QA, automated tests, performance testing. We have an entire day dedicated to testing (Week 6). For now: it happens BEFORE deployment."

**Stage 6 — Deployment:** "Getting it into production. CI/CD pipelines automate this. Docker, Kubernetes, GitHub Actions — Week 8."

**Stage 7 — Maintenance:** "Software is never 'done'. Bugs are found. Features are requested. The cycle starts again."

---

## SDLC Models Table

**[ACTION]** Show the model comparison table.

"Different organizations use different models. You'll likely work in Agile/Scrum — most modern software companies do. But you'll hear about Waterfall in regulated industries like healthcare and finance.

**[ASK]** "Why do you think a bank might prefer Waterfall over Agile?"

*Guide toward:* "Regulatory compliance, audit trails, fixed requirements — change is expensive and risky in regulated environments."

---

## Fullstack Architecture Diagram

**[ACTION]** Show the fullstack diagram.

"THIS is the map of everything you're going to learn. Point to each layer as you speak:

- Frontend: what runs in the user's browser — React or Angular
- Backend: your Java Spring Boot server — handles the business logic
- Database: where data lives persistently — SQL or MongoDB
- DevOps: Docker, Kubernetes, AWS — how it all gets packaged and deployed

**[ASK]** "When you click 'Add to Cart' on Amazon, which layers are involved?"

*Answer:* "All of them. The frontend captures the click, sends an HTTP request to the backend, the backend validates and processes it, writes to the database, and sends a response back to update the UI."

---

## Where Does Today Fit?

"Look at that curriculum table. Today is Day 1 — foundation. Everything we're doing today — Linux, shell scripting, SDLC, Git — these are the tools you'll use regardless of which layer you're working on.

You'll be SSH'd into Linux servers in Week 8. You'll use shell scripts to automate builds. You'll follow the SDLC on every project. And Git — which we're about to learn — you will use every single day for the rest of your career."

→ **TRANSITION:** "Alright — let's take a break and then come back for Part 2 where we dive into Git and Agile. These are the two things you'll actually use starting tomorrow."
