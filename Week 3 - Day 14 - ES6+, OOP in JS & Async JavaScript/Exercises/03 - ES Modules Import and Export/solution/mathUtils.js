// mathUtils.js — solution

// Named exports — exported individually alongside the declaration
export function add(a, b)      { return a + b; }
export function subtract(a, b) { return a - b; }
export const PI = 3.14159;

// Default export — only one per module; imported without curly braces
export default function multiply(a, b) { return a * b; }
