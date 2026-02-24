# Exercise 02: Shell Scripting with Variables, Conditionals, and Loops

## Objective
Write a Bash shell script that uses variables, user input, conditional statements, and loops to automate a practical task.

## Background
Shell scripts let you automate repetitive tasks on a Linux system. In software development, scripts are used for everything from setting up environments to automating deployments. In this exercise you'll build a script that processes a list of project files, categorizes them by type, and reports a summary — skills directly applicable to real-world automation.

## Requirements

1. **Variables and user input:**
   - At the top of the script, declare a variable `PROJECT_NAME` and assign it the value `"WebApp"`.
   - Declare a variable `VERSION` and assign it `"1.0.0"`.
   - Print a greeting line using both variables, e.g.: `"Project: WebApp | Version: 1.0.0"`.

2. **Arrays and loops:**
   - Declare an array called `FILES` containing these 8 filenames:
     `"index.html"`, `"style.css"`, `"app.js"`, `"server.java"`, `"README.md"`, `"config.yml"`, `"tests.java"`, `"utils.js"`
   - Use a `for` loop to iterate over the array and print each filename on its own line, prefixed with `"  File:"`.

3. **Conditional logic:**
   - Inside the same loop (or a new loop over the same array), use `if/elif/else` to categorize each file by its extension:
     - Files ending in `.html` or `.css` → print `"  [FRONTEND] filename"`
     - Files ending in `.js` → print `"  [JAVASCRIPT] filename"`
     - Files ending in `.java` → print `"  [JAVA] filename"`
     - Files ending in `.md` or `.yml` → print `"  [CONFIG/DOCS] filename"`
     - Anything else → print `"  [OTHER] filename"`

4. **Counters:**
   - Use counter variables to track how many files fall into each category.
   - After the loop, print a summary section that looks like:
     ```
     ===== File Summary =====
     Frontend files:       2
     JavaScript files:     2
     Java files:           2
     Config/Docs files:    2
     Other files:          0
     Total files:          8
     ```

5. **While loop:**
   - After the summary, write a `while` loop that counts down from 3 to 1, printing `"Launching in X..."` for each number, then prints `"Done!"` when the countdown ends.

## Hints
- To check a file extension in Bash, use a `case` statement or the `==` operator with a pattern like `[[ "$file" == *.java ]]`.
- Increment a counter with `((counter++))` or `counter=$((counter + 1))`.
- A `while` loop in Bash: `while [ $count -gt 0 ]; do ... done`
- Make your script executable with `chmod +x your-script.sh`, then run it with `./your-script.sh`.

## Expected Output

```
Project: WebApp | Version: 1.0.0

===== All Files =====
  File: index.html
  File: style.css
  File: app.js
  File: server.java
  File: README.md
  File: config.yml
  File: tests.java
  File: utils.js

===== File Categories =====
  [FRONTEND] index.html
  [FRONTEND] style.css
  [JAVASCRIPT] app.js
  [JAVA] server.java
  [CONFIG/DOCS] README.md
  [CONFIG/DOCS] config.yml
  [JAVA] tests.java
  [JAVASCRIPT] utils.js

===== File Summary =====
Frontend files:       2
JavaScript files:     2
Java files:           2
Config/Docs files:    2
Other files:          0
Total files:          8

Launching in 3...
Launching in 2...
Launching in 1...
Done!
```
