#!/bin/bash
# =============================================================================
# WEEK 1 - DAY 1 | PART 1 | FILE 1 OF 3
# Topic: Linux OS Fundamentals and Basic Commands
# =============================================================================
# PURPOSE: Demonstrates core Linux commands you'll use every day as a developer.
# Run each section individually — don't run the whole file at once.
# =============================================================================

# -----------------------------------------------------------------------------
# SECTION 1: Understanding the Linux File System
# -----------------------------------------------------------------------------
# Linux organizes everything in a single tree starting from root (/)
# Key directories every developer should know:

#   /           → root of the entire file system
#   /home       → home directories for users (e.g. /home/scott)
#   /etc        → system-wide config files
#   /var        → logs, temporary runtime files
#   /usr        → installed programs and libraries
#   /tmp        → temporary files (cleared on reboot)
#   /bin        → essential system binaries (ls, cp, mv, etc.)
#   /opt        → optional/third-party software

# Show where you are right now
pwd

# Show the root level of the entire file system
ls /

# -----------------------------------------------------------------------------
# SECTION 2: Navigating the File System
# -----------------------------------------------------------------------------

# Print current working directory
pwd

# List files in current directory
ls

# List with details: permissions, owner, size, date
ls -l

# List ALL files including hidden ones (files that start with .)
ls -la

# Change directory to home
cd ~

# Go up one level
cd ..

# Go back to the previous directory
cd -

# Create a directory
mkdir my-project

# Create nested directories in one command (-p = create parents too)
mkdir -p my-project/src/main/java

# -----------------------------------------------------------------------------
# SECTION 3: Working with Files
# -----------------------------------------------------------------------------

# Create an empty file
touch README.md

# Write text into a file (overwrites if exists)
echo "# My First Project" > README.md

# Append text to a file (does NOT overwrite)
echo "This is a fullstack training project." >> README.md

# Display contents of a file
cat README.md

# Display file contents one screen at a time (press q to quit)
# less README.md

# Show just the first 10 lines
head README.md

# Show just the last 10 lines
tail README.md

# Copy a file
cp README.md README-backup.md

# Move (or rename) a file
mv README-backup.md docs/README-backup.md

# Remove a file
rm docs/README-backup.md

# Remove a directory and everything inside it (-r = recursive, -f = force)
rm -rf my-project

# -----------------------------------------------------------------------------
# SECTION 4: Finding Things
# -----------------------------------------------------------------------------

# Find files by name (search from current directory)
find . -name "*.md"

# Find files modified in the last 7 days
find . -mtime -7

# Search for text INSIDE files (grep)
# -r = recursive, -n = show line numbers, -i = case insensitive
grep -rni "project" .

# Search for a word in a specific file
grep "training" README.md

# -----------------------------------------------------------------------------
# SECTION 5: File Permissions
# -----------------------------------------------------------------------------
# Every file has 3 permission sets: owner | group | everyone
# Each set has: r (read=4), w (write=2), x (execute=1)
#
# Example: -rwxr-xr-- means:
#   Owner:  rwx = 7 (read + write + execute)
#   Group:  r-x = 5 (read + execute)
#   Others: r-- = 4 (read only)

# View permissions
ls -l README.md

# Change permissions — make a script executable
chmod +x 02-shell-scripting.sh

# Numeric permission: owner=7, group=5, others=5
chmod 755 02-shell-scripting.sh

# Change owner of a file (requires sudo)
# sudo chown scott:staff README.md

# -----------------------------------------------------------------------------
# SECTION 6: Useful Everyday Commands
# -----------------------------------------------------------------------------

# Who is the current logged-in user?
whoami

# Show system information
uname -a

# Show disk usage (human-readable)
df -h

# Show memory usage
free -h       # Linux only (not available on macOS by default)

# Show running processes
ps aux

# Kill a process by ID
# kill 1234

# Show the top processes using most CPU/memory (press q to quit)
# top

# Pipe: send output of one command into another
# Example: list all files and search for .md files in that list
ls -la | grep ".md"

# Count lines in a file
wc -l README.md

# Show command history
history

# Clear the terminal screen
clear

# Show manual for any command (press q to quit)
# man ls

# -----------------------------------------------------------------------------
# SECTION 7: Package Management (Linux)
# -----------------------------------------------------------------------------
# On Ubuntu/Debian-based systems:
#   sudo apt update              → update package list
#   sudo apt install git         → install a package
#   sudo apt upgrade             → upgrade all installed packages
#   sudo apt remove git          → remove a package

# On macOS with Homebrew:
#   brew install git
#   brew update
#   brew upgrade

# Check if git is installed and which version
git --version

# Check if Java is installed
java -version
