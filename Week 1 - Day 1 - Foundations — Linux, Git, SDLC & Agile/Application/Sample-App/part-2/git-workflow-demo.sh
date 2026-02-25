#!/bin/bash
# =============================================================
# Day 1 Part 2 — Git Workflow & Agile Sprint Demo
# =============================================================
# Run: bash git-workflow-demo.sh
# =============================================================

GREEN='\033[0;32m'; CYAN='\033[0;36m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
section() { echo -e "\n${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; echo -e "${GREEN}$1${NC}"; echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }
runcmd()  { echo -e "${YELLOW}$ $1${NC}"; eval "$1"; echo ""; }

echo -e "${GREEN}"
echo "╔═════════════════════════════════════════╗"
echo "║  Day 1 Part 2 — Git Workflow & Agile   ║"
echo "╚═════════════════════════════════════════╝"
echo -e "${NC}"

REPO="/tmp/git-demo-$$"
mkdir -p "$REPO"
cd "$REPO"

# ─────────────────────────────────────
# SECTION 1: Initialize Repository
# ─────────────────────────────────────
section "1. Initialize a Git Repository"

runcmd "git init"
runcmd "git config user.name  'Academy Dev'"
runcmd "git config user.email 'dev@academy.com'"

echo "# Student Grade App" > README.md
echo "public class Main { }" > Main.java
runcmd "git status"

# ─────────────────────────────────────
# SECTION 2: Staging & Committing
# ─────────────────────────────────────
section "2. Staging and Committing Changes"

runcmd "git add README.md"
echo -e "${YELLOW}$ git status  (after staging README.md)${NC}"
git status
echo ""

runcmd "git add Main.java"
runcmd "git commit -m 'Initial commit: add README and Main class'"
runcmd "git log --oneline"

# ─────────────────────────────────────
# SECTION 3: Feature Branching
# ─────────────────────────────────────
section "3. Feature Branching"

echo "Branching strategy: main → feature/add-student-class"
echo ""
runcmd "git checkout -b feature/add-student-class"

cat > Student.java << 'EOF'
public class Student {
    private String name;
    private int grade;

    public Student(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() { return name; }
    public int getGrade()   { return grade; }
}
EOF
runcmd "git add Student.java"
runcmd "git commit -m 'feat: add Student class with name and grade'"

echo "// Calculate GPA" >> Main.java
runcmd "git add Main.java"
runcmd "git commit -m 'feat: add GPA calculation placeholder'"

runcmd "git log --oneline"

# ─────────────────────────────────────
# SECTION 4: Merging Back to Main
# ─────────────────────────────────────
section "4. Merge Feature Branch into Main"

runcmd "git checkout main"
runcmd "git merge feature/add-student-class --no-ff -m 'Merge feature/add-student-class into main'"
runcmd "git log --oneline --graph"

# ─────────────────────────────────────
# SECTION 5: Simulating a Merge Conflict
# ─────────────────────────────────────
section "5. Simulating a Merge Conflict (and Resolving It)"

runcmd "git checkout -b feature/grading-scale"
echo "// Grade: A=90+ B=80+ C=70+" >> Main.java
runcmd "git add Main.java && git commit -m 'feat: add grading scale comments'"

runcmd "git checkout main"
echo "// Grade: A>=90 B>=80 C>=70 D>=60" >> Main.java
runcmd "git add Main.java && git commit -m 'chore: add detailed grading scale'"

echo -e "${RED}Attempting merge — this will conflict:${NC}"
git merge feature/grading-scale 2>&1 || true

echo ""
echo "Resolving conflict by keeping both comments..."
# Simulate resolution
git checkout --theirs Main.java 2>/dev/null || true
echo "// Grade: A=90+ B=80+ C=70+ D=60+ (resolved)" > Main.java.resolved
mv Main.java.resolved Main.java
runcmd "git add Main.java"
runcmd "git commit -m 'merge: resolve grading scale conflict'"

# ─────────────────────────────────────
# SECTION 6: Git Commands Summary
# ─────────────────────────────────────
section "6. Full Git Log"
runcmd "git log --oneline --graph --all"

# ─────────────────────────────────────
# SECTION 7: Agile Sprint Board Simulation
# ─────────────────────────────────────
section "7. Agile Sprint Board — Sprint 1"

echo "Sprint Goal: Build the Student Grade Calculator MVP"
echo "Sprint Duration: 2 weeks | Velocity: 20 story points"
echo ""
printf "%-30s %-10s %-15s %-10s\n" "User Story" "Points" "Status" "Assignee"
printf "%-30s %-10s %-15s %-10s\n" "──────────────────────────────" "──────────" "───────────────" "──────────"
printf "%-30s %-10s %-15s %-10s\n" "As a teacher I can add student" "3"        "✅ Done"          "Alice"
printf "%-30s %-10s %-15s %-10s\n" "As a teacher I can view grades" "2"        "✅ Done"          "Bob"
printf "%-30s %-10s %-15s %-10s\n" "As a teacher I can calc GPA"   "5"        "🔄 In Progress"   "Carol"
printf "%-30s %-10s %-15s %-10s\n" "As a student I can view report" "5"        "📋 To Do"         "Unassigned"
printf "%-30s %-10s %-15s %-10s\n" "Export grades to CSV"          "5"        "📋 To Do"         "Unassigned"
echo ""

DONE=5; IN_PROGRESS=5; TODO=10; TOTAL=20
echo "Sprint Burndown:"
echo "  Completed:   $DONE / $TOTAL points ($(( DONE * 100 / TOTAL ))%)"
echo "  In Progress: $IN_PROGRESS points"
echo "  Remaining:   $TODO points"
echo ""
echo "Ceremonies this sprint:"
echo "  📅 Sprint Planning  — Story pointing, capacity planning"
echo "  📅 Daily Standup    — Yesterday / Today / Blockers"
echo "  📅 Sprint Review    — Demo to stakeholders"
echo "  📅 Retrospective    — What went well / improve / action items"

# Cleanup
cd /tmp && rm -rf "$REPO"
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}  Demo complete! Git repo cleaned up.${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
