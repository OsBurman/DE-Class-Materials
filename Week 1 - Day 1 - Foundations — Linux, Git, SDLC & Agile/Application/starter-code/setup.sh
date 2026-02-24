#!/bin/bash

# =============================================================
# setup.sh — Team Task Board Project Setup Script
#
# PURPOSE: Automate initial project environment setup.
# USAGE:   chmod +x scripts/setup.sh && ./scripts/setup.sh
# =============================================================

# ----- CONFIGURATION -----------------------------------------
PROJECT_NAME="Team Task Board"   # TODO: Change this to your project name
REQUIRED_DIRS=("src" "tests" "docs" "scripts")

# ----- HELPER FUNCTIONS --------------------------------------

print_header() {
    echo "============================================"
    echo "  $1"
    echo "============================================"
}

print_success() {
    echo "  ✅  $1"
}

print_warning() {
    echo "  ⚠️   $1"
}

print_error() {
    echo "  ❌  $1"
}

# ----- MAIN SCRIPT -------------------------------------------

print_header "Welcome to $PROJECT_NAME"
echo ""

# TODO 1: Print the current date and time using the 'date' command
# Hint: Use command substitution $(date) inside an echo statement
echo "Setup started at: [YOUR CODE HERE]"
echo ""

# --- Step 1: Check required directories ----------------------
print_header "Step 1: Checking project directories"

for dir in "${REQUIRED_DIRS[@]}"; do
    # TODO 2: Check if the directory exists.
    #         If it DOES exist, print a success message.
    #         If it does NOT exist, create it with mkdir and print a warning.
    # Hint: Use: if [ -d "$dir" ]; then ... fi
    echo "[YOUR CODE HERE — check and create $dir if missing]"
done

echo ""

# --- Step 2: Verify Git is initialized -----------------------
print_header "Step 2: Checking Git repository"

# TODO 3: Check whether a .git directory exists in the current folder.
#         If it DOES exist, print: "Git repository found."
#         If it does NOT exist, run 'git init' and print: "Git repository initialized."
# Hint: Use: if [ -d ".git" ]; then ... fi
echo "[YOUR CODE HERE — check for .git directory]"

echo ""

# --- Step 3: Show current Git status -------------------------
print_header "Step 3: Git status"

# TODO 4: Run 'git status' to show the current state of the repo.
echo "[YOUR CODE HERE — run git status]"

echo ""

# --- Step 4: Summary -----------------------------------------
print_header "Setup Complete!"
echo ""
echo "  Project  : $PROJECT_NAME"

# TODO 5: Print the current working directory using the 'pwd' command
echo "  Location : [YOUR CODE HERE]"
echo ""
echo "  Next steps:"
echo "    1. Fill in README.md with your project details"
echo "    2. Complete docs/sprint-backlog.md with your user stories"
echo "    3. Complete docs/definition-of-done.md with your team's criteria"
echo "    4. Make your first commit: git add . && git commit -m 'chore: initial setup'"
echo ""
