#!/usr/bin/env python3
"""
fix_slides.py
─────────────
Finds every SLIDE_DESCRIPTIONS.md in the DE-Class-Materials folder tree and
sends each one to Claude in a fresh, isolated API call. Claude is asked to
detect slides that are truncated or sparse compared to the early slides in the
same file, and complete them to the same depth and format.

Each file is backed up to <filename>.bak before being overwritten.

Usage
─────
1.  Install the Anthropic SDK if you haven't:
        pip install anthropic

2.  Set your API key in the terminal before running:
        export ANTHROPIC_API_KEY="sk-ant-..."

3.  Run (dry-run first — no files are changed, just shows what would be fixed):
        python3 fix_slides.py --dry-run

4.  Run for real:
        python3 fix_slides.py

5.  Run on a single file to test before doing all 88:
        python3 fix_slides.py --file "Week 6 - Day 28 - Testing/Slides/Part 1/SLIDE_DESCRIPTIONS.md"

Options
───────
  --dry-run       Print which files would be processed; do not call the API or write anything.
  --file PATH     Process only this one file (relative to the script's folder).
  --skip-backup   Do not create .bak files (not recommended).
  --delay N       Seconds to wait between API calls (default: 3). Prevents rate-limit errors.
"""

import anthropic
import argparse
import os
import time
from pathlib import Path

# ── Configuration ─────────────────────────────────────────────────────────────

ROOT_DIR   = Path(__file__).parent           # folder this script lives in
MODEL      = "claude-3-5-sonnet-20241022"    # change to a newer model if preferred
MAX_TOKENS = 8192                            # max output tokens per file

SYSTEM_PROMPT = """\
You are an expert technical educator specializing in software engineering curriculum.

You will be given the full contents of a SLIDE_DESCRIPTIONS.md file for a one-hour \
software engineering lecture. Each slide in the file should have:
  - A clear header with the slide number and title
  - A body with bullet points, code blocks, tables, or diagrams as appropriate
  - Enough detail that an instructor can build a real slide from the description alone

The problem: later slides in the file are often truncated or sparse — they have a \
header and maybe one or two lines, while early slides have rich detail.

Your job:
  1. Read the ENTIRE file carefully.
  2. Identify which slides are fully developed (the standard to match).
  3. Rewrite any slides that are sparse, truncated, or significantly thinner than the \
best slides — expanding them to the same depth, format, and instructional quality.
  4. Do NOT change slides that are already well-developed.
  5. Return the COMPLETE file — every slide — as valid markdown. No commentary, \
no preamble, no closing notes. Only the markdown content.\
"""

# ── Core logic ────────────────────────────────────────────────────────────────

def find_slide_files(root: Path) -> list[Path]:
    return sorted(root.rglob("SLIDE_DESCRIPTIONS.md"))


def fix_file(client: anthropic.Anthropic, path: Path, skip_backup: bool, delay: float) -> bool:
    """Send one file to Claude and overwrite it with the improved version.
    Returns True on success, False on error."""

    original = path.read_text(encoding="utf-8")

    print(f"\n→ Processing: {path.relative_to(ROOT_DIR)}")
    print(f"  Lines in: {len(original.splitlines())}")

    try:
        response = client.messages.create(
            model=MODEL,
            max_tokens=MAX_TOKENS,
            system=SYSTEM_PROMPT,
            messages=[
                {
                    "role": "user",
                    "content": (
                        "Here is the SLIDE_DESCRIPTIONS.md file. "
                        "Expand any truncated or sparse slides so they match the quality "
                        "of the best slides in the file, then return the complete file.\n\n"
                        f"{original}"
                    ),
                }
            ],
        )
    except anthropic.RateLimitError:
        print("  ⚠ Rate limited — waiting 60 s then retrying once...")
        time.sleep(60)
        return fix_file(client, path, skip_backup, delay)
    except Exception as e:
        print(f"  ✗ API error: {e}")
        return False

    improved = response.content[0].text.strip()

    if len(improved) < len(original) * 0.5:
        print("  ⚠ Response suspiciously short — skipping to avoid data loss.")
        return False

    if not skip_backup:
        bak = path.with_suffix(".md.bak")
        bak.write_text(original, encoding="utf-8")
        print(f"  Backup: {bak.name}")

    path.write_text(improved, encoding="utf-8")
    print(f"  Lines out: {len(improved.splitlines())}  ✓ saved")

    time.sleep(delay)
    return True


# ── Entry point ───────────────────────────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(description="Fix truncated SLIDE_DESCRIPTIONS.md files using Claude.")
    parser.add_argument("--dry-run",     action="store_true", help="List files only; do not call API.")
    parser.add_argument("--file",        type=str, default=None, help="Process a single relative file path.")
    parser.add_argument("--skip-backup", action="store_true", help="Skip creating .bak files.")
    parser.add_argument("--delay",       type=float, default=3.0, help="Seconds between API calls (default: 3).")
    args = parser.parse_args()

    api_key = os.environ.get("ANTHROPIC_API_KEY")
    if not api_key and not args.dry_run:
        print("ERROR: ANTHROPIC_API_KEY environment variable is not set.")
        print("Run:  export ANTHROPIC_API_KEY='sk-ant-...'")
        return

    if args.file:
        files = [ROOT_DIR / args.file]
        missing = [f for f in files if not f.exists()]
        if missing:
            print(f"ERROR: File not found: {missing[0]}")
            return
    else:
        files = find_slide_files(ROOT_DIR)

    print(f"Found {len(files)} SLIDE_DESCRIPTIONS.md file(s) under:\n  {ROOT_DIR}\n")

    if args.dry_run:
        for f in files:
            line_count = len(f.read_text(encoding="utf-8").splitlines())
            print(f"  {f.relative_to(ROOT_DIR)}  ({line_count} lines)")
        print("\n-- Dry run complete. No files changed. --")
        return

    client = anthropic.Anthropic(api_key=api_key)

    succeeded, failed = 0, 0
    for path in files:
        ok = fix_file(client, path, skip_backup=args.skip_backup, delay=args.delay)
        if ok:
            succeeded += 1
        else:
            failed += 1

    print(f"\n{'─'*50}")
    print(f"Done. {succeeded} succeeded, {failed} failed.")
    if failed:
        print("Failed files were not modified. Check errors above.")


if __name__ == "__main__":
    main()
