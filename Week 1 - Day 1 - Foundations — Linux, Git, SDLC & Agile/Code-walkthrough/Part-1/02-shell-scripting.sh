#!/bin/bash
# =============================================================================
# WEEK 1 - DAY 1 | PART 1 | FILE 2 OF 3
# Topic: Shell Scripting Fundamentals
# =============================================================================
# PURPOSE: Demonstrates how to write real shell scripts — variables, input,
# conditionals, loops, functions, and practical automation examples.
# Make this file executable first: chmod +x 02-shell-scripting.sh
# Then run it: ./02-shell-scripting.sh
# =============================================================================

# The shebang line (above) tells the OS which interpreter to use.
# #!/bin/bash means "run this script with bash"

# -----------------------------------------------------------------------------
# SECTION 1: Variables
# -----------------------------------------------------------------------------
# Variables store values. NO spaces around the = sign.

PROJECT_NAME="FullStack Training"
STUDENT_COUNT=30
PI=3.14

echo "Project: $PROJECT_NAME"
echo "Students: $STUDENT_COUNT"
echo "Pi: $PI"

# Read-only variable (like a constant)
readonly MAX_RETRIES=3
echo "Max retries: $MAX_RETRIES"

# Command substitution — store the output of a command in a variable
CURRENT_DATE=$(date +"%Y-%m-%d")
CURRENT_USER=$(whoami)
echo "Today is $CURRENT_DATE and you are logged in as $CURRENT_USER"

# -----------------------------------------------------------------------------
# SECTION 2: User Input
# -----------------------------------------------------------------------------

echo ""
echo "--- User Input ---"
read -p "Enter your name: " USER_NAME
echo "Hello, $USER_NAME! Welcome to the course."

# -----------------------------------------------------------------------------
# SECTION 3: Conditional Statements (if / elif / else)
# -----------------------------------------------------------------------------

echo ""
echo "--- Conditionals ---"

SCORE=85

if [ $SCORE -ge 90 ]; then
    echo "Grade: A"
elif [ $SCORE -ge 80 ]; then
    echo "Grade: B"
elif [ $SCORE -ge 70 ]; then
    echo "Grade: C"
else
    echo "Grade: F — needs improvement"
fi

# Check if a file exists
if [ -f "README.md" ]; then
    echo "README.md exists"
else
    echo "README.md does not exist"
fi

# Check if a directory exists
if [ -d "/tmp" ]; then
    echo "/tmp directory exists"
fi

# String comparison
ENVIRONMENT="production"
if [ "$ENVIRONMENT" == "production" ]; then
    echo "WARNING: Running in production mode!"
fi

# -----------------------------------------------------------------------------
# SECTION 4: Loops
# -----------------------------------------------------------------------------

echo ""
echo "--- Loops ---"

# For loop — iterate over a list
for DAY in Monday Tuesday Wednesday Thursday Friday; do
    echo "Class day: $DAY"
done

# For loop with a range (C-style)
echo "Counting to 5:"
for (( i=1; i<=5; i++ )); do
    echo "  Count: $i"
done

# While loop
echo "While loop countdown:"
COUNTER=3
while [ $COUNTER -gt 0 ]; do
    echo "  $COUNTER..."
    COUNTER=$((COUNTER - 1))
done
echo "  Go!"

# Loop through files in a directory
echo "Files in current directory:"
for FILE in ./*; do
    echo "  $FILE"
done

# -----------------------------------------------------------------------------
# SECTION 5: Functions
# -----------------------------------------------------------------------------

echo ""
echo "--- Functions ---"

# Define a function
greet_student() {
    local NAME=$1        # $1 = first argument passed to function
    local COURSE=$2      # $2 = second argument
    echo "Welcome, $NAME! You are enrolled in: $COURSE"
}

# Call the function with arguments
greet_student "Alice" "Full-Stack Engineering"
greet_student "Bob" "Full-Stack Engineering"

# Function with a return value (bash returns exit codes 0-255)
# For actual values, use echo and command substitution
add_numbers() {
    local A=$1
    local B=$2
    echo $((A + B))
}

RESULT=$(add_numbers 10 25)
echo "10 + 25 = $RESULT"

# -----------------------------------------------------------------------------
# SECTION 6: Practical Script — Project Setup Automation
# -----------------------------------------------------------------------------
# This is the kind of script a developer would actually write and use.

echo ""
echo "--- Practical Script: Project Setup ---"

setup_project() {
    local PROJECT=$1

    echo "Setting up project: $PROJECT"

    # Create directory structure
    mkdir -p "$PROJECT/src"
    mkdir -p "$PROJECT/tests"
    mkdir -p "$PROJECT/docs"

    # Create starter files
    touch "$PROJECT/README.md"
    echo "# $PROJECT" > "$PROJECT/README.md"
    echo "Created on: $(date)" >> "$PROJECT/README.md"

    touch "$PROJECT/.gitignore"
    echo "node_modules/" >> "$PROJECT/.gitignore"
    echo "*.class" >> "$PROJECT/.gitignore"
    echo "target/" >> "$PROJECT/.gitignore"

    echo "Project '$PROJECT' created successfully!"
    echo "Structure:"
    find "$PROJECT" -type f
}

# Run the setup function
setup_project "demo-app"

# Clean up the demo (comment this out if you want to keep it)
rm -rf demo-app

# -----------------------------------------------------------------------------
# SECTION 7: Exit Codes and Error Handling
# -----------------------------------------------------------------------------

echo ""
echo "--- Exit Codes ---"
# Every command returns an exit code: 0 = success, non-zero = failure
ls /nonexistent-folder 2>/dev/null   # suppress the error output
echo "Exit code of last command: $?"  # $? holds the last exit code

# Use exit codes to handle errors gracefully
if ! command -v java &>/dev/null; then
    echo "ERROR: Java is not installed. Please install Java first."
    exit 1
else
    echo "Java is installed: $(java -version 2>&1 | head -1)"
fi

echo ""
echo "Script completed successfully."
