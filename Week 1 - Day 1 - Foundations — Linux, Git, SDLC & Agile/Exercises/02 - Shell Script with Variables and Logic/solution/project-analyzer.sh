#!/bin/bash
# Exercise 02 SOLUTION: Shell Scripting with Variables, Conditionals, and Loops

# ─────────────────────────────────────────────
# PART 1: Variables
# ─────────────────────────────────────────────

# Assign string values to variables — no spaces around = in Bash
PROJECT_NAME="WebApp"
VERSION="1.0.0"

# Double-quoted echo expands variables; $VARIABLE or ${VARIABLE} both work
echo "Project: $PROJECT_NAME | Version: $VERSION"

echo ""

# ─────────────────────────────────────────────
# PART 2: Arrays and For Loop
# ─────────────────────────────────────────────

# Bash arrays use parentheses; elements separated by spaces
FILES=("index.html" "style.css" "app.js" "server.java" "README.md" "config.yml" "tests.java" "utils.js")

echo "===== All Files ====="

# "${FILES[@]}" expands all array elements; quotes preserve filenames with spaces
for file in "${FILES[@]}"; do
    echo "  File: $file"
done

echo ""

# ─────────────────────────────────────────────
# PART 3: Conditionals and Counters
# ─────────────────────────────────────────────

# Initialize all counters to 0
frontend_count=0
js_count=0
java_count=0
config_count=0
other_count=0

echo "===== File Categories ====="

for file in "${FILES[@]}"; do
    # [[ ]] is the modern Bash conditional; == with * is a glob pattern match
    if [[ "$file" == *.html || "$file" == *.css ]]; then
        echo "  [FRONTEND] $file"
        ((frontend_count++))
    elif [[ "$file" == *.js ]]; then
        echo "  [JAVASCRIPT] $file"
        ((js_count++))
    elif [[ "$file" == *.java ]]; then
        echo "  [JAVA] $file"
        ((java_count++))
    elif [[ "$file" == *.md || "$file" == *.yml ]]; then
        echo "  [CONFIG/DOCS] $file"
        ((config_count++))
    else
        echo "  [OTHER] $file"
        ((other_count++))
    fi
done

echo ""

# ─────────────────────────────────────────────
# PART 4: Summary
# ─────────────────────────────────────────────

# ${#FILES[@]} gives the number of elements in the array
total=${#FILES[@]}

echo "===== File Summary ====="
# printf "%-22s %d\n" prints a left-aligned string of width 22, then an integer
printf "%-22s %d\n" "Frontend files:" $frontend_count
printf "%-22s %d\n" "JavaScript files:" $js_count
printf "%-22s %d\n" "Java files:" $java_count
printf "%-22s %d\n" "Config/Docs files:" $config_count
printf "%-22s %d\n" "Other files:" $other_count
printf "%-22s %d\n" "Total files:" $total

echo ""

# ─────────────────────────────────────────────
# PART 5: While Loop Countdown
# ─────────────────────────────────────────────

count=3

# -gt means "greater than"; (( )) can also be used: while (( count > 0 ))
while [ $count -gt 0 ]; do
    echo "Launching in $count..."
    ((count--))   # decrement by 1 each iteration
done

echo "Done!"
