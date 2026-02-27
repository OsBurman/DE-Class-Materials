Git Fundamentals: A Complete Hour-Long Lesson Plan
I'll create a comprehensive script for your one-hour Git class with slide recommendations.

SLIDE 1: Title Slide
Content: "Introduction to Git: Version Control for Developers"
Script (2 minutes):
"Good morning everyone! Today we're going to learn about Git, which is one of the most essential tools you'll use as a developer. By the end of this hour, you'll understand what Git is, why it's critical for modern development, and how to use the most important commands. Don't worry if you've never used command-line tools before—we'll start from the very beginning and build up your skills step by step.
Let me ask you a quick question: Has anyone ever worked on a document, made changes, and then wanted to go back to an earlier version but couldn't remember what you changed? Or maybe you've saved files like 'essay_final.doc', 'essay_final_v2.doc', 'essay_final_ACTUAL.doc'? That's the problem Git solves, but for code."

SLIDE 2: What is Version Control?
Content:

Definition of version control
Visual: Timeline showing snapshots of a project
Why we need it: collaboration, history, backup

Script (3 minutes):
"Version control is a system that tracks changes to files over time. Think of it like a time machine for your code. Every time you make a significant change, you can take a 'snapshot' of your entire project. If something breaks, you can travel back in time to when everything worked.
But it's more than just backup. Version control allows multiple people to work on the same project simultaneously without overwriting each other's work. It keeps a complete history of who changed what and when. And it lets you experiment with new features without risking your working code.
Git is the most popular version control system in the world. It was created by Linus Torvalds, the same person who created Linux, back in 2005. Today, it's used by millions of developers and nearly every tech company you can think of."

SLIDE 3: Git vs GitHub
Content:

Git (local tool)
GitHub/GitLab/Bitbucket (remote hosting services)
Diagram showing the relationship

Script (2 minutes):
"Before we dive in, let's clear up a common confusion: Git and GitHub are NOT the same thing.
Git is the version control software that runs on your computer. It's a command-line tool that tracks your changes locally.
GitHub is a website that hosts Git repositories online. It's like Dropbox or Google Drive, but specifically designed for Git projects. There are alternatives like GitLab and Bitbucket, but they all do the same basic thing: provide a remote location to store and share your Git repositories.
You can use Git without ever touching GitHub, but most developers use both together."

SLIDE 4: Key Concepts
Content:

Repository (repo): Your project folder
Commit: A saved snapshot
Branch: Parallel version of code
Remote: Online copy of repository

Script (3 minutes):
"Let's establish some key vocabulary you'll hear constantly:
A repository or 'repo' is just a fancy word for your project folder. When you initialize Git in a folder, it becomes a repository.
A commit is a snapshot of your project at a specific point in time. Think of it like saving your game—you're creating a checkpoint you can return to later. Each commit has a unique ID and a message describing what changed.
A branch is like a parallel universe for your code. The default branch is called 'main' or 'master'. When you want to add a new feature, you create a branch, work on it there, and then merge it back when it's ready. This keeps your main code clean and working.
A remote is the online version of your repository, usually hosted on GitHub. We 'push' our local changes up to the remote and 'pull' other people's changes down."

SLIDE 5: The Three States of Git
Content:

Diagram showing: Working Directory → Staging Area → Repository
Visual flow of git add and git commit

Script (4 minutes):
"This is crucial to understand: Git has three states where your files can live.
First, the Working Directory—this is just your normal project folder where you edit files. When you modify a file, it's in your working directory.
Second, the Staging Area (also called the index)—this is like a preview of your next commit. You select which changes you want to include in your next snapshot and add them to staging. This gives you control over what gets committed.
Third, the Repository—when you commit, the staged changes are permanently saved into your Git history.
The workflow looks like this: You modify files in your working directory. When you're happy with some changes, you 'add' them to the staging area. When you've staged everything for this checkpoint, you 'commit' it to the repository with a descriptive message.
Why have a staging area? Because sometimes you work on multiple things at once, but you want to commit them as separate, logical chunks. The staging area lets you organize your commits thoughtfully."

SLIDE 6: Installation Check
Content:

How to verify Git is installed
Command: git --version

Script (1 minute):
"Before we start practicing, let's make sure everyone has Git installed. Open your terminal or command prompt and type: git --version
You should see something like 'git version 2.39.0' or similar. If you get an error, you'll need to download Git from git-scm.com. I can help you with that after class, but for now, follow along and you can practice later."

SLIDE 7: Basic Configuration
Content:
git config --global user.name "Your Name"
git config --global user.email "your@email.com"
Script (2 minutes):
"The first time you use Git, you need to tell it who you are. Every commit you make will be tagged with this information.
Run these two commands, replacing with your actual name and email:

git config --global user.name "Your Name"
git config --global user.email "your@email.com"

Use the email associated with your GitHub account if you have one. The --global flag means this configuration applies to all your projects. You only need to do this once per computer."

SLIDE 8: Creating Your First Repository
Content:
mkdir my-project
cd my-project
git init
Script (3 minutes):
"Let's create your first Git repository! Everyone follow along:
First, create a new folder and navigate into it:

mkdir my-project
cd my-project

Now, initialize Git:

git init

You should see a message like 'Initialized empty Git repository'. Git has created a hidden folder called '.git' where it stores all the version history. Don't delete this folder or you'll lose your history!
Your folder is now a Git repository. Right now it's empty, but we're about to change that."

SLIDE 9: Checking Status
Content:
git status

Shows current state
Untracked files
Modified files
Staged files

Script (3 minutes):
"git status is the command you'll use most often. It shows you what's happening in your repository.
Let's create a file and see what happens. In your project folder, create a file called 'README.md' and add some text to it. Now run:

git status

You'll see 'README.md' listed under 'Untracked files'. Git sees the file but isn't tracking its changes yet. The output even tells you what to do next: use git add to start tracking it.
Get in the habit of running git status constantly. It's like asking 'Where am I? What's changed?' It'll save you from many mistakes."

SLIDE 10: Staging Files
Content:
git add <filename>
git add .
git add -A
Script (3 minutes):
"To stage files for commit, we use git add. There are several ways to use it:

git add README.md stages a specific file
git add . stages all changes in the current directory
git add -A stages all changes in the entire repository

Let's stage our README file:

git add README.md

Now run git status again. See how the file moved to 'Changes to be committed'? It's now in the staging area, ready to be committed.
Pro tip: In the beginning, use git add with specific filenames. As you get comfortable, git add . is faster for staging everything."

SLIDE 11: Making Your First Commit
Content:
git commit -m "Your message here"

Commit messages should be descriptive
Present tense, imperative mood
Examples: "Add README file", "Fix login bug"

Script (4 minutes):
"Now for the magic moment—making your first commit!
Run:

git commit -m "Add README file"

The -m flag lets you add a message right in the command. Every commit MUST have a message describing what changed.
Good commit messages are short but descriptive. Write them in present tense as if you're giving a command: 'Add feature', 'Fix bug', 'Update documentation'. Bad messages like 'stuff' or 'changes' will confuse you later when you're looking through history.
Think of your commit message as a note to your future self explaining why this change was made.
Now run git status again. See 'nothing to commit, working tree clean'? That means all your changes are committed and your working directory matches your last snapshot. Congratulations—you just made your first commit!"

SLIDE 12: Viewing History
Content:
git log
git log --oneline
git log --graph
Script (3 minutes):
"To see your commit history, use:

git log

This shows all commits with their full details: unique ID, author, date, and message. Press 'q' to quit the log view.
For a condensed view, try:

git log --oneline

This shows just the first 7 characters of each commit ID and the message.
Make a few more commits now. Create another file, add it, and commit it. Change your README, add it, and commit that too. Then look at your log. You're building a timeline of your project!
You can even add --graph to see branches visually, though we only have one branch right now."

SLIDE 13: The Working Flow
Content:

Visual workflow diagram


Make changes
git add (stage)
git commit (save)
Repeat

Script (2 minutes):
"Let's pause and solidify the basic workflow, because you'll repeat this pattern thousands of times:
One: Make changes to your files—write code, edit documents, whatever.
Two: Stage the changes you want to commit with git add.
Three: Commit those staged changes with git commit -m "message".
Four: Repeat! Keep working, staging, and committing as you make progress.
Each commit should represent one logical change. Don't commit after every single line of code, but also don't wait until you've rewritten your entire project. Find the middle ground—whenever you complete a feature, fix a bug, or reach a good stopping point."

SLIDE 14: Branching Basics
Content:
git branch
git branch <branch-name>
git checkout <branch-name>
git checkout -b <branch-name>
Script (5 minutes):
"Now let's talk about branches. Branches let you work on new features without affecting your main code.
To see what branches exist:

git branch

You'll see one branch, probably called 'main', with an asterisk showing it's your current branch.
To create a new branch:

git branch feature-login

This creates the branch but doesn't switch to it. To switch branches:

git checkout feature-login

Or do both at once:

git checkout -b feature-login

Now you're on the new branch. Any commits you make here won't affect the main branch. This is incredibly powerful! You can experiment, and if it doesn't work out, just switch back to main and delete the branch. If it does work, you merge it in.
Let's practice: Create a branch called 'experiment', switch to it, make a change to a file, and commit it. Then switch back to main with git checkout main. Look at your file—the change is gone! It only exists on the experiment branch. Switch back to see it again. This is branch magic."

SLIDE 15: Merging Branches
Content:
git merge <branch-name>

Visual of branches merging
Fast-forward vs. three-way merge

Script (4 minutes):
"When your feature is ready, you merge it back into main.
First, switch to the branch you want to merge INTO (usually main):

git checkout main

Then merge the feature branch:

git merge feature-login

Git will combine the changes. If there are no conflicts, it creates a merge commit and you're done.
Sometimes you'll get merge conflicts—this happens when the same line was changed differently in both branches. Git will mark the conflicts in your files, and you'll need to manually decide which version to keep. We won't dive deep into conflicts today, but know they're normal and fixable.
After merging, you can delete the feature branch if you're done with it:

git branch -d feature-login

The changes are now part of main, so you don't need the branch anymore."

SLIDE 16: Introduction to Remote Repositories
Content:

Visual: Local repo ↔ Remote repo
GitHub/GitLab as remotes
Why use remotes: backup, collaboration, sharing

Script (3 minutes):
"So far, everything we've done is local—only on your computer. But the real power of Git comes from remote repositories.
A remote repository is typically hosted on GitHub, GitLab, or Bitbucket. It serves three purposes:
One, it's a backup. If your computer crashes, your code is safe in the cloud.
Two, it's for collaboration. Your teammates can push their changes to the remote, and you can pull them down.
Three, it's for sharing. You can make your code public or share it with specific people.
The workflow is: you work locally, making commits as we practiced. Periodically, you 'push' your commits to the remote. Other team members push their work too. You 'pull' their changes down to stay in sync."

SLIDE 17: Connecting to a Remote
Content:
git remote add origin <url>
git remote -v
Script (3 minutes):
"To connect your local repository to a remote, you need the remote's URL. If you create a repository on GitHub, it gives you this URL.
Add the remote with:

git remote add origin https://github.com/username/repo.git

'origin' is just a name—it's the conventional name for your primary remote, but you could call it anything.
To see your remotes:

git remote -v

This shows both fetch and push URLs. They're usually the same.
Now your local repo knows about the remote, but they're not synced yet. That's where push and pull come in."

SLIDE 18: Pushing Changes
Content:
git push origin main
git push -u origin main
Script (3 minutes):
"To send your local commits to the remote:

git push origin main

This pushes your 'main' branch to the 'origin' remote.
The first time you push a branch, add the -u flag:

git push -u origin main

This sets up tracking, so in the future you can just type git push without specifying the remote and branch.
Before you can push, the remote needs to exist. On GitHub, you create an empty repository first, then connect it as your remote. The GitHub interface walks you through this.
Important: You can only push if you have permission. For your own repositories, this is automatic. For other people's repositories, you need to be added as a collaborator or submit a pull request."

SLIDE 19: Pulling Changes
Content:
git pull origin main
git pull
Script (2 minutes):
"To get the latest changes from the remote:

git pull origin main

If you set up tracking with -u, just:

git pull

This downloads new commits from the remote and merges them into your current branch. Always pull before starting new work to make sure you have the latest code.
If you and a teammate both changed the same file, you might get merge conflicts when you pull. Like we discussed earlier, these are normal—Git will help you resolve them."

SLIDE 20: Cloning Repositories
Content:
git clone <url>
Script (2 minutes):
"If you want to download an existing repository from GitHub, use clone:

git clone https://github.com/username/repo.git

This creates a folder with the repository name, downloads all the code and history, and automatically sets up the remote.
Cloning is what you do when you start working on an existing project. The repository owner doesn't need to add you as a collaborator for you to clone a public repo—anyone can clone public repositories.
Try this: Find any public repository on GitHub and clone it. You now have the entire project and its history on your machine!"

SLIDE 21: Essential Commands Summary
Content:
Table or list of commands:

git init - Create repository
git status - Check status
git add - Stage files
git commit -m - Save snapshot
git log - View history
git branch - List/create branches
git checkout - Switch branches
git merge - Merge branches
git push - Upload to remote
git pull - Download from remote
git clone - Copy remote repository

Script (2 minutes):
"Here's your cheat sheet. These eleven commands cover 90% of what you'll do with Git:
git init, status, add, commit, log for basic version control.
git branch, checkout, and merge for working with branches.
git push, pull, and clone for working with remotes.
Take a screenshot of this slide or write these down. In your first weeks using Git, you'll reference this list constantly. Eventually, these commands will become muscle memory."

SLIDE 22: Best Practices
Content:

Commit often with clear messages
Pull before you push
Use branches for features
Don't commit sensitive data
Keep commits focused

Script (3 minutes):
"Before we wrap up, some best practices:
Commit often. Don't wait days between commits. Commit whenever you complete a logical unit of work. Small, frequent commits are better than giant ones.
Write meaningful commit messages. Your future self will thank you. 'Fix bug' tells you nothing. 'Fix login button not responding on mobile' tells you everything.
Always pull before you push. This prevents conflicts and ensures you're working with the latest code.
Use branches for new features. Keep main stable. Experiment on branches.
Never commit passwords, API keys, or sensitive data. Once it's in Git history, it's very hard to remove. Use environment variables or config files that are excluded from Git.
Keep commits focused. One commit should do one thing. Don't fix three bugs and add two features in a single commit."

SLIDE 23: Common Mistakes to Avoid
Content:

Forgetting to commit
Committing to wrong branch
Not pulling before editing
Committing large binary files
Panic deleting .git folder

Script (2 minutes):
"Some mistakes everyone makes when learning Git:
Forgetting to commit your work at the end of the day. Make it a habit—always commit before closing your laptop.
Committing to the wrong branch. Always check git status or git branch before committing.
Editing files without pulling first, then getting conflicts when you try to push.
Committing huge files or binary files that don't need version control. Git isn't great with large media files.
And the classic: panicking and deleting the .git folder. If Git confuses you, ask for help instead of nuking your entire history!"

SLIDE 24: Getting Help
Content:
git help <command>
git <command> --help

Documentation resources
Git official docs
GitHub Learning Lab

Script (1 minute):
"Git has built-in help. Type git help for an overview, or git help commit for detailed information about a specific command.
The official Git documentation at git-scm.com is comprehensive. GitHub also has excellent learning resources and interactive tutorials.
Don't be afraid to Google your questions—every Git problem you encounter, thousands of developers have encountered before. Stack Overflow is your friend."

SLIDE 25: Practice Exercise
Content:
Exercise checklist:

Create a new repository
Make 3 commits
Create a branch
Make changes on the branch
Merge back to main
(Optional) Push to GitHub

Script (3 minutes):
"Let's do a quick practice exercise to reinforce everything. I want you to:
One, create a new repository in a fresh folder.
Two, create at least three files and make three separate commits, one for each file.
Three, create a new branch called 'development'.
Four, switch to that branch and make some changes. Commit them.
Five, switch back to main and merge the development branch.
If you have a GitHub account, try pushing this to a new repository.
Take five minutes to try this. I'll walk around and help if you get stuck. This exercise uses all the core skills we learned today."

SLIDE 26: Next Steps
Content:

Practice daily
Explore GitHub
Learn about: .gitignore, git diff, git stash, rebase
Contribute to open source

Script (2 minutes):
"We covered a lot today, but this is just the beginning. To really learn Git, you need to use it regularly.
Start using Git for all your projects, even small ones. The practice is invaluable.
Explore GitHub. Follow developers, star interesting projects, read other people's code.
As you get comfortable, learn about .gitignore files for excluding files from Git, git diff for seeing exactly what changed, git stash for temporarily saving work, and rebasing for a different way to integrate changes.
Eventually, consider contributing to open source projects. It's a great way to practice Git collaboration and give back to the community."

SLIDE 27: Q&A
Content:

"Questions?"
Your contact information
Additional resources

Script (2 minutes):
"That brings us to the end of our session. We covered what Git is, why it's essential, the three states of Git, basic commands, branching, and working with remotes.
What questions do you have? Don't worry if Git feels overwhelming—it's normal. The concepts are simple, but it takes practice to internalize them.
Remember, Git is a tool to help you, not stress you out. Start simple: commit your changes regularly, use branches for experiments, and push to a remote for backup. As you get comfortable, you'll discover Git's more powerful features.
My contact information is on this slide if you need help after class. Now, who has questions?"
