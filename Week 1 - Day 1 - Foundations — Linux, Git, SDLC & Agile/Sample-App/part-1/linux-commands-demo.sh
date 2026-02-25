#!/bin/bash
# =============================================================
# Day 1 Part 1 — Linux Fundamentals & SDLC Demo
# =============================================================
# Run: bash linux-commands-demo.sh
# =============================================================

# Colors for output
GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

section() { echo -e "\n${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; echo -e "${GREEN}$1${NC}"; echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }
cmd()     { echo -e "${YELLOW}$ $1${NC}"; eval "$1"; echo ""; }

echo -e "${GREEN}"
echo "╔══════════════════════════════════════╗"
echo "║  Day 1 Part 1 — Linux & SDLC Demo   ║"
echo "╚══════════════════════════════════════╝"
echo -e "${NC}"

# ─────────────────────────────────────
# SECTION 1: Navigating the File System
# ─────────────────────────────────────
section "1. Navigating the File System"

cmd "pwd"                         # print working directory
cmd "ls -la /tmp"                 # list files with permissions
cmd "echo 'Home dir: ~'; cd ~ && pwd"

# ─────────────────────────────────────
# SECTION 2: Creating Files & Folders
# ─────────────────────────────────────
section "2. Creating Files & Directories"

DEMO_DIR="/tmp/linux-demo-$$"
mkdir -p "$DEMO_DIR"
echo -e "${YELLOW}$ mkdir -p $DEMO_DIR${NC}\n"

# Create sample files
echo "Alice,90" > "$DEMO_DIR/students.csv"
echo "Bob,75"  >> "$DEMO_DIR/students.csv"
echo "Carol,88" >> "$DEMO_DIR/students.csv"
echo "Dave,62"  >> "$DEMO_DIR/students.csv"
echo "Eve,95"   >> "$DEMO_DIR/students.csv"
echo -e "${YELLOW}$ cat students.csv${NC}"
cat "$DEMO_DIR/students.csv"
echo ""

# ─────────────────────────────────────
# SECTION 3: Text Processing
# ─────────────────────────────────────
section "3. Text Processing Commands"

echo -e "${YELLOW}$ grep 'A' students.csv  (find students scoring 9x)${NC}"
grep "9" "$DEMO_DIR/students.csv"
echo ""

echo -e "${YELLOW}$ sort -t, -k2 -rn students.csv  (sort by score desc)${NC}"
sort -t, -k2 -rn "$DEMO_DIR/students.csv"
echo ""

echo -e "${YELLOW}$ wc -l students.csv  (count lines)${NC}"
wc -l "$DEMO_DIR/students.csv"
echo ""

echo -e "${YELLOW}$ cut -d, -f1 students.csv  (names only)${NC}"
cut -d, -f1 "$DEMO_DIR/students.csv"
echo ""

# ─────────────────────────────────────
# SECTION 4: File Permissions
# ─────────────────────────────────────
section "4. File Permissions"

touch "$DEMO_DIR/secret.txt"
echo "Top secret data" > "$DEMO_DIR/secret.txt"

echo -e "${YELLOW}$ ls -la secret.txt${NC}"
ls -la "$DEMO_DIR/secret.txt"
echo ""

cmd "chmod 600 $DEMO_DIR/secret.txt"
echo -e "${YELLOW}$ ls -la secret.txt  (after chmod 600)${NC}"
ls -la "$DEMO_DIR/secret.txt"
echo ""

echo "Permission bits explained:"
echo "  chmod 600 → owner: rw-, group: ---, other: ---"
echo "  chmod 755 → owner: rwx, group: r-x, other: r-x"
echo "  chmod 644 → owner: rw-, group: r--, other: r--"

# ─────────────────────────────────────
# SECTION 5: Shell Scripting Basics
# ─────────────────────────────────────
section "5. Shell Scripting Basics"

echo -e "${YELLOW}Variables and conditionals:${NC}"
SCORE=88
if [ $SCORE -ge 90 ]; then
    GRADE="A"
elif [ $SCORE -ge 80 ]; then
    GRADE="B"
elif [ $SCORE -ge 70 ]; then
    GRADE="C"
else
    GRADE="F"
fi
echo "Score: $SCORE → Grade: $GRADE"
echo ""

echo -e "${YELLOW}For loop:${NC}"
for STUDENT in Alice Bob Carol Dave Eve; do
    echo "  Processing student: $STUDENT"
done
echo ""

echo -e "${YELLOW}Function:${NC}"
calculate_average() {
    local total=0
    local count=0
    while IFS=',' read -r name score; do
        total=$((total + score))
        count=$((count + 1))
    done < "$DEMO_DIR/students.csv"
    echo "Average score: $((total / count))"
}
calculate_average

# ─────────────────────────────────────
# SECTION 6: SDLC Stages Simulator
# ─────────────────────────────────────
section "6. SDLC Stages — Software Development Lifecycle"

echo "Simulating phases of a real software project..."
echo ""

PHASES=(
    "Planning       → Define scope, estimate cost, timeline, feasibility study"
    "Requirements   → Gather user stories, define functional/non-functional needs"
    "System Design  → Architecture, DB schema, API contracts, tech stack"
    "Implementation → Developers write code, code reviews, version control"
    "Testing        → Unit, integration, UAT, performance, security testing"
    "Deployment     → CI/CD pipeline, blue-green deploy, monitoring setup"
    "Maintenance    → Bug fixes, patches, performance tuning, new features"
)

for i in "${!PHASES[@]}"; do
    echo "  Stage $((i+1)): ${PHASES[$i]}"
    sleep 0.3
done

echo ""
echo -e "${GREEN}✓ SDLC simulation complete.${NC}"

# Cleanup
rm -rf "$DEMO_DIR"
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}  Demo complete! Temp files cleaned up.${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
