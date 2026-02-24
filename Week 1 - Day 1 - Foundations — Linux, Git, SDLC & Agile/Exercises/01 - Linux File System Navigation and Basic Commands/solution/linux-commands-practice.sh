#!/bin/bash
# Exercise 01 SOLUTION: Linux File System Navigation and Basic Commands

echo "===== Step 1: Navigate the File System ====="

# Print the current working directory
pwd

# List the home directory in long format (-l = long format)
ls -l ~

# List all files including hidden ones (-a = all, includes dotfiles)
ls -la

echo ""
echo "===== Step 2: Create a Project Structure ====="

# Create the top-level project directory
mkdir ~/my-project

# Create three subdirectories inside my-project
# Using -p means "create parent directories as needed" and won't error if they exist
mkdir -p ~/my-project/src
mkdir -p ~/my-project/docs
mkdir -p ~/my-project/tests

# Verify the structure
ls ~/my-project

echo ""
echo "===== Step 3: Create and Manipulate Files ====="

# echo with > redirection creates the file and writes the text in one command
echo "Project documentation goes here." > ~/my-project/docs/README.txt

# cp copies a file — source first, then destination
cp ~/my-project/docs/README.txt ~/my-project/src/README.txt

# mv renames (or moves) a file — old name first, then new name
mv ~/my-project/src/README.txt ~/my-project/src/notes.txt

# cat prints file contents to the terminal
cat ~/my-project/src/notes.txt

echo ""
echo "===== Step 4: File Permissions ====="

# ls -la shows permissions in the leftmost column (e.g., -rw-r--r--)
ls -la ~/my-project/docs/README.txt

# chmod u+x: u = user/owner, + = add, x = execute permission
chmod u+x ~/my-project/docs/README.txt

# Check again — the owner column should now show 'x' (e.g., -rwxr--r--)
ls -la ~/my-project/docs/README.txt

echo ""
echo "===== Step 5: Search and Filter ====="

# grep searches for a pattern inside a file and prints matching lines
grep "documentation" ~/my-project/docs/README.txt

# find searches recursively: . means start from current dir, -name filters by filename pattern
find ~/my-project -name "*.txt"

echo ""
echo "===== Step 6: I/O Redirection ====="

# > creates or overwrites the file with the command's output
ls -la ~/my-project > ~/my-project/directory-listing.txt

# >> appends to the file without overwriting existing content
echo "End of listing." >> ~/my-project/directory-listing.txt

# Show the final contents of the file
cat ~/my-project/directory-listing.txt

echo ""
echo "===== Step 7: Clean Up ====="

# rmdir removes an empty directory; use rm -r for non-empty directories
rmdir ~/my-project/tests

# Verify tests is gone — should only show: docs  directory-listing.txt  src
ls ~/my-project

echo ""
echo "===== All steps complete! ====="
