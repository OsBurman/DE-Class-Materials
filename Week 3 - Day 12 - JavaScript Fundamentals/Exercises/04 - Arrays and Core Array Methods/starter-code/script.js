// Exercise 04: Arrays and Core Array Methods
// Run with: node script.js

// ─────────────────────────────────────────────
// PART 1: Creating Arrays
// ─────────────────────────────────────────────

// TODO: Create an array `fruits` containing "apple", "banana", "cherry"
//       Log it with its .length property: `fruits: [...] length: 3`


// TODO: Create a `mixed` array with at least: one number, one string,
//       one boolean, and one nested array (e.g., [1, 2])
//       Log it with its .length


// ─────────────────────────────────────────────
// PART 2: Accessing and Updating Elements
// ─────────────────────────────────────────────

// TODO: Log fruits[0]  and  fruits[fruits.length - 1]
// TODO: Update fruits[1] to "blueberry" and log the updated array


// ─────────────────────────────────────────────
// PART 3: Adding and Removing (push/pop/unshift/shift)
// ─────────────────────────────────────────────

// TODO: push "date" to fruits — log the new length returned AND the current fruits
// TODO: pop from fruits — log the removed element AND the current fruits
// TODO: unshift "avocado" to fruits — log the new length AND fruits
// TODO: shift from fruits — log the removed element AND fruits


// ─────────────────────────────────────────────
// PART 4: splice — Insert, Remove, Replace
// ─────────────────────────────────────────────

// TODO: Use splice(1, 1) to remove 1 element at index 1
//       Log the removed items array and the updated fruits

// TODO: Use splice to INSERT "elderberry" and "fig" at index 1 (delete 0 elements)
//       Log fruits after insertion

// TODO: Use splice to REPLACE the element at index 2 with "grape"
//       Log fruits after replacement


// ─────────────────────────────────────────────
// PART 5: Non-Mutating Methods
// ─────────────────────────────────────────────

// TODO: slice(1, 3) — log the sub-array, then log fruits to prove it is unchanged
// TODO: concat(["honeydew", "kiwi"]) — log the new combined array (fruits unchanged)
// TODO: indexOf("cherry") — log the index
// TODO: includes("fig") — log true or false


// ─────────────────────────────────────────────
// PART 6: reverse and sort
// ─────────────────────────────────────────────

// TODO: reverse() fruits — log the reversed array
//       Then reverse it back to restore order

// TODO: Create const nums = [10, 1, 21, 2]
//       Sort with default .sort() — log result, add a comment about why it's unexpected
//       Sort with numeric comparator (a, b) => a - b — log the correct sorted result


// ─────────────────────────────────────────────
// PART 7: Spread and Destructuring
// ─────────────────────────────────────────────

// TODO: Use spread [...fruits] to copy fruits into fruitsCopy
//       Push "ADDED" to fruitsCopy
//       Log fruitsCopy (has "ADDED") and fruits (unchanged) to prove they are separate

// TODO: Destructure fruits: const [first, second, ...rest] = fruits
//       Log first, second, and rest
