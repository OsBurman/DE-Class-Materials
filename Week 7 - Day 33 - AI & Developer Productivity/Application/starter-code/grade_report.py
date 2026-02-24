"""
grade_report.py — Grade Report Generator
Day 33 — AI & Developer Productivity

Use AI tools to help complete the TODOs below.
Before accepting any AI suggestion, read it carefully and verify it is correct.
"""

from gpa_calculator import calculate_semester_gpa, calculate_cumulative_gpa, gpa_to_letter


SAMPLE_STUDENT = {
    "name": "Alex Johnson",
    "id": "STU-2024-001",
    "major": "Computer Science",
    "semesters": [
        {
            "name": "Fall 2023",
            "courses": [
                {"name": "Intro to Programming", "credits": 3, "grade": "A"},
                {"name": "Discrete Math",         "credits": 3, "grade": "B+"},
                {"name": "English Composition",   "credits": 3, "grade": "A-"},
                {"name": "Gen Ed Elective",       "credits": 3, "grade": "B"},
            ]
        },
        {
            "name": "Spring 2024",
            "courses": [
                {"name": "Data Structures",  "credits": 3, "grade": "B+"},
                {"name": "Calculus I",       "credits": 4, "grade": "B"},
                {"name": "Systems Programming", "credits": 3, "grade": "A-"},
                {"name": "Tech Writing",     "credits": 3, "grade": "A"},
            ]
        },
        {
            "name": "Fall 2024",
            "courses": [
                {"name": "Algorithms",        "credits": 3, "grade": "B"},
                {"name": "Database Systems",  "credits": 3, "grade": "A"},
                {"name": "Operating Systems", "credits": 3, "grade": "B+"},
                {"name": "Statistics",        "credits": 3, "grade": "C+"},
            ]
        },
    ]
}


def format_course_row(course: dict) -> str:
    """
    Format a single course as a fixed-width row.
    Example: "  Intro to Programming        3 cr    A      4.00"

    TODO: Implement using f-string formatting.
    """
    # TODO
    pass


def format_semester_section(semester: dict) -> str:
    """
    Format a full semester section including header, course rows, and semester GPA.

    TODO: Implement this function.
    """
    # TODO
    pass


def generate_report(student: dict) -> str:
    """
    Generate a complete text-based grade report for the student.

    The report should include:
    - Header with student name, ID, and major
    - Each semester section (use format_semester_section)
    - Summary section with cumulative GPA and standing

    TODO: Implement this function using the helper functions above.
    """
    # TODO
    pass


def get_academic_standing(gpa: float) -> str:
    """
    Return academic standing string based on GPA.

    >= 3.5 → "Dean's List"
    >= 2.0 → "Good Standing"
    >= 1.5 → "Academic Probation"
    < 1.5  → "Academic Suspension"

    TODO: Implement.
    """
    # TODO
    pass


if __name__ == "__main__":
    report = generate_report(SAMPLE_STUDENT)
    if report:
        print(report)
    else:
        print("TODO: generate_report() not yet implemented")
