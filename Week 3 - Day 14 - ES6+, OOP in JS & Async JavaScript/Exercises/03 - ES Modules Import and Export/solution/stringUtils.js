// stringUtils.js — solution

// capitalize: uppercase first char, leave rest unchanged
export function capitalize(str) {
  if (!str) return str;
  return str.charAt(0).toUpperCase() + str.slice(1);
}

// reverseString: split into chars, reverse array, rejoin
export function reverseString(str) {
  return str.split('').reverse().join('');
}
