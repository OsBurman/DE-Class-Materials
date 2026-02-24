# Exercise 01: Linux File System Navigation and Basic Commands

## Objective
Practice navigating the Linux file system, managing files and directories, working with file permissions, and using I/O redirection from the command line.

## Background
As a developer, you'll spend a significant amount of time working in the terminal. This exercise simulates a real project setup scenario where you need to organize a project directory, manage files, check permissions, and redirect output — all using Linux commands.

## Requirements

1. **Navigate the file system:**
   - Print your current working directory using the appropriate command.
   - List the contents of your home directory in long format (showing permissions, owner, size, and date).
   - List all files including hidden files in your current directory.

2. **Create a project structure:**
   - Create a directory called `my-project` in your home directory.
   - Inside `my-project`, create three subdirectories: `src`, `docs`, and `tests`.
   - Verify the structure was created correctly by listing the contents of `my-project`.

3. **Create and manipulate files:**
   - Inside `my-project/docs`, create a file called `README.txt` and write the text `"Project documentation goes here."` into it using a single command (do not use a text editor).
   - Copy `README.txt` to the `src` directory.
   - Rename the copy in `src` to `notes.txt`.
   - Display the contents of `notes.txt` to the terminal.

4. **File permissions:**
   - Check the current permissions of `README.txt`.
   - Make `README.txt` executable for the owner using `chmod`.
   - Verify the permissions changed by listing the file in long format again.

5. **Search and filter:**
   - Use `grep` to search for the word `"documentation"` inside `my-project/docs/README.txt`.
   - List all `.txt` files recursively inside `my-project` using `find`.

6. **I/O redirection:**
   - Redirect the output of `ls -la ~/my-project` to a file called `directory-listing.txt` inside `my-project`.
   - Append the text `"End of listing."` to `directory-listing.txt` without overwriting it.
   - Display the final contents of `directory-listing.txt`.

7. **Clean up:**
   - Remove the `tests` directory.
   - Confirm it is gone by listing `my-project` again.

## Hints
- `echo "some text" > filename` writes text to a file (overwrites); `>>` appends.
- `chmod u+x filename` adds execute permission for the file owner.
- `find . -name "*.txt"` searches recursively from the current directory.
- `ls -la` shows all files (including hidden ones) in long format with permissions.

## Expected Output

Your terminal session should produce output similar to the following (exact paths will differ based on your username):

```
# Step 1 - pwd
/home/student

# Step 1 - ls -la (excerpt showing home directory contents)
total 48
drwxr-xr-x  8 student student 4096 Feb 22 10:00 .
drwxr-xr-x 20 root    root    4096 Feb 22 09:00 ..
drwxr-xr-x  5 student student 4096 Feb 22 10:01 my-project

# Step 2 - ls my-project
docs  src  tests

# Step 3 - cat notes.txt
Project documentation goes here.

# Step 4 - permissions before chmod (excerpt)
-rw-r--r-- 1 student student 33 Feb 22 10:02 README.txt

# Step 4 - permissions after chmod u+x
-rwxr--r-- 1 student student 33 Feb 22 10:02 README.txt

# Step 5 - grep output
Project documentation goes here.

# Step 5 - find output
./docs/README.txt
./src/notes.txt

# Step 6 - cat directory-listing.txt (last line)
End of listing.

# Step 7 - ls my-project (tests directory is gone)
docs  directory-listing.txt  src
```
