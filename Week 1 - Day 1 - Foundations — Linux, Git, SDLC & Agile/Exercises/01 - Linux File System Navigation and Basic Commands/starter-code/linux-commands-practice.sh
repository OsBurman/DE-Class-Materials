#!/bin/bash
# Exercise 01: Linux File System Navigation and Basic Commands
# Run this script to practice each command step by step.
# Each section has a TODO. Replace each TODO line with the correct command.
# You can run this script with: bash linux-commands-practice.sh
# Or you can type the commands one by one directly in your terminal.

echo "===== Step 1: Navigate the File System ====="

# TODO: Print the current working directory


# TODO: List the contents of your home directory in long format (hint: use ~ for home)


# TODO: List ALL files in the current directory including hidden files (hint: -a flag)


echo ""
echo "===== Step 2: Create a Project Structure ====="

# TODO: Create a directory called 'my-project' inside your home directory


# TODO: Create three subdirectories inside my-project: src, docs, and tests
# (hint: you can use mkdir with the -p flag or create them one at a time)


# TODO: List the contents of my-project to verify all three folders exist


echo ""
echo "===== Step 3: Create and Manipulate Files ====="

# TODO: Create a file called README.txt inside ~/my-project/docs
#       and write the text: Project documentation goes here.
#       Use echo and > redirection — do not open a text editor


# TODO: Copy README.txt from docs/ to src/


# TODO: Rename the copied file in src/ from README.txt to notes.txt


# TODO: Display the contents of notes.txt to the terminal


echo ""
echo "===== Step 4: File Permissions ====="

# TODO: Display the permissions of README.txt in long format


# TODO: Make README.txt executable for the owner only (use chmod u+x)


# TODO: Display the permissions again to confirm the change


echo ""
echo "===== Step 5: Search and Filter ====="

# TODO: Use grep to search for the word "documentation" inside ~/my-project/docs/README.txt


# TODO: Use find to list all .txt files recursively inside ~/my-project


echo ""
echo "===== Step 6: I/O Redirection ====="

# TODO: Redirect the output of 'ls -la ~/my-project' into a file called
#       directory-listing.txt inside ~/my-project  (use > to create/overwrite)


# TODO: Append the text "End of listing." to directory-listing.txt
#       without overwriting it (hint: use >>)


# TODO: Display the full contents of directory-listing.txt


echo ""
echo "===== Step 7: Clean Up ====="

# TODO: Remove the 'tests' directory inside ~/my-project
#       (hint: use rmdir if it's empty, or rm -r if it has contents)


# TODO: List ~/my-project again to confirm 'tests' is gone


echo ""
echo "===== All steps complete! ====="
