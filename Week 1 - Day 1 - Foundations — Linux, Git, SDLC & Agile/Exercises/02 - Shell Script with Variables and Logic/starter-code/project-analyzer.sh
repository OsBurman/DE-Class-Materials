#!/bin/bash
# Exercise 02: Shell Scripting with Variables, Conditionals, and Loops
# Run with: bash project-analyzer.sh
# Or make executable first: chmod +x project-analyzer.sh && ./project-analyzer.sh

# ─────────────────────────────────────────────
# PART 1: Variables
# ─────────────────────────────────────────────

# TODO: Declare a variable PROJECT_NAME and assign it the value "WebApp"


# TODO: Declare a variable VERSION and assign it "1.0.0"


# TODO: Print a greeting line using both variables in the format:
#       "Project: WebApp | Version: 1.0.0"
#       (hint: use echo with double quotes so variables are expanded)


echo ""

# ─────────────────────────────────────────────
# PART 2: Arrays and For Loop
# ─────────────────────────────────────────────

# TODO: Declare an array called FILES containing these 8 filenames:
#       "index.html" "style.css" "app.js" "server.java"
#       "README.md" "config.yml" "tests.java" "utils.js"
#       (hint: FILES=("item1" "item2" ...))


echo "===== All Files ====="

# TODO: Write a for loop over the FILES array that prints each filename
#       prefixed with "  File:"
#       (hint: for file in "${FILES[@]}"; do ... done)


echo ""

# ─────────────────────────────────────────────
# PART 3: Conditionals and Counters
# ─────────────────────────────────────────────

# TODO: Declare four counter variables, all starting at 0:
#       frontend_count, js_count, java_count, config_count, other_count


echo "===== File Categories ====="

# TODO: Write a for loop over FILES. Inside the loop, use if/elif/else
#       to categorize each file by extension and print the category:
#         .html or .css  → "  [FRONTEND] filename"
#         .js            → "  [JAVASCRIPT] filename"
#         .java          → "  [JAVA] filename"
#         .md or .yml    → "  [CONFIG/DOCS] filename"
#         anything else  → "  [OTHER] filename"
#       Also increment the appropriate counter for each file.
#       (hint: use [[ "$file" == *.java ]] to match extensions)


echo ""

# ─────────────────────────────────────────────
# PART 4: Summary
# ─────────────────────────────────────────────

# TODO: Calculate the total number of files (hint: use ${#FILES[@]} or add all counters)


# TODO: Print the summary section exactly as shown in the Expected Output:
#       ===== File Summary =====
#       Frontend files:       N
#       JavaScript files:     N
#       Java files:           N
#       Config/Docs files:    N
#       Other files:          N
#       Total files:          N
#       (hint: use printf for aligned output, e.g.: printf "%-22s %d\n" "Frontend files:" $frontend_count)


echo ""

# ─────────────────────────────────────────────
# PART 5: While Loop Countdown
# ─────────────────────────────────────────────

# TODO: Declare a variable 'count' and set it to 3


# TODO: Write a while loop that runs as long as count > 0.
#       Each iteration should:
#         - Print "Launching in N..."
#         - Decrement count by 1
#       After the loop ends, print "Done!"

