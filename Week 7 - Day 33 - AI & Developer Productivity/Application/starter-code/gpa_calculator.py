"""
gpa_calculator.py — GPA Calculator Starter Code
Day 33 — AI & Developer Productivity

TODO: Use GitHub Copilot / AI assistance to implement the functions below.
Pay attention to how the AI suggests completions — discuss with the class
when suggestions are helpful vs. when they need corrections.
"""


def letter_to_points(letter_grade: str) -> float:
    """
    Convert a letter grade to grade points.

    Scale:
        A  = 4.0, A- = 3.7
        B+ = 3.3, B  = 3.0, B- = 2.7
        C+ = 2.3, C  = 2.0, C- = 1.7
        D+ = 1.3, D  = 1.0, D- = 0.7
        F  = 0.0

    TODO: Implement this function.
    Raise ValueError for unrecognized grade strings.
    """
    # TODO
    pass


def calculate_semester_gpa(courses: list[dict]) -> float:
    """
    Calculate GPA for one semester.

    Each course dict has:
        { "name": str, "credits": int, "grade": str }

    GPA = sum(grade_points * credits) / sum(credits)

    TODO: Implement this function.
    Raise ValueError if courses list is empty.
    """
    # TODO
    pass


def calculate_cumulative_gpa(semesters: list[list[dict]]) -> float:
    """
    Calculate cumulative GPA across multiple semesters.

    semesters: a list of semester course lists (same format as above).

    TODO: Implement this function.
    """
    # TODO
    pass


def gpa_to_letter(gpa: float) -> str:
    """
    Convert a numeric GPA to an approximate letter grade category.

    >= 3.7  → "A"
    >= 3.3  → "B+"
    >= 3.0  → "B"
    >= 2.7  → "B-"
    >= 2.0  → "C"
    >= 1.0  → "D"
    < 1.0   → "F"

    TODO: Implement this function.
    """
    # TODO
    pass


# ---- Manual test (run: python gpa_calculator.py) ----
if __name__ == "__main__":
    semester1 = [
        {"name": "Intro to CS",       "credits": 3, "grade": "A"},
        {"name": "Calculus I",         "credits": 4, "grade": "B+"},
        {"name": "English Comp",       "credits": 3, "grade": "A-"},
        {"name": "Physics Lab",        "credits": 1, "grade": "B"},
    ]

    semester2 = [
        {"name": "Data Structures",    "credits": 3, "grade": "B+"},
        {"name": "Calculus II",        "credits": 4, "grade": "B"},
        {"name": "Technical Writing",  "credits": 3, "grade": "A"},
    ]

    # TODO: Call your functions and print results
    print("Semester 1 GPA:", calculate_semester_gpa(semester1))
    print("Semester 2 GPA:", calculate_semester_gpa(semester2))
    print("Cumulative GPA:", calculate_cumulative_gpa([semester1, semester2]))
