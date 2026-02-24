"""
buggy_calculator.py — Intentionally Buggy Calculator
Day 33 — AI & Developer Productivity

TASK: Use AI tools (Copilot Chat, ChatGPT, etc.) to identify and fix the 5 bugs.
Document each bug in bugs.md BEFORE fixing it.
"""


def add(a, b):
    return a - b  # Bug 1


def multiply(a, b):
    result = 0
    for _ in range(b):  # Bug 2 — what happens when b is 0 or negative?
        result += a
    return result


def calculate_average(numbers):
    total = 0
    for n in numbers:
        total += n
    return total / len(numbers)  # Bug 3


def celsius_to_fahrenheit(celsius):
    return celsius * 9 / 5 + 32  # this one is correct

def fahrenheit_to_celsius(fahrenheit):
    return (fahrenheit - 32) * 5 / 9  # this one is correct


def is_palindrome(text):
    cleaned = text.lower()
    # Bug 4 — spaces and punctuation are not removed
    return cleaned == cleaned[::-1]


def count_vowels(text):
    vowels = "aeiou"
    count = 0
    for char in text:
        if char in vowels:  # Bug 5 — case sensitive
            count += 1
    return count


# ---- Self-test ----
if __name__ == "__main__":
    print("add(3, 4) =", add(3, 4))              # should be 7, but gives 3-4=-1
    print("multiply(3, 4) =", multiply(3, 4))    # should be 12
    print("multiply(3, 0) =", multiply(3, 0))    # bug!
    print("multiply(3, -2) =", multiply(3, -2))  # bug!
    print("average([]) =", end=" ")
    try:
        print(calculate_average([]))              # should handle gracefully
    except Exception as e:
        print(f"Error: {e}")
    print("is_palindrome('A man a plan a canal Panama') =",
          is_palindrome("A man a plan a canal Panama"))  # should be True
    print("count_vowels('Hello World') =",
          count_vowels("Hello World"))  # should be 3 (e, o, o)
