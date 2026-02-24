// main.js — solution

// Requirement 5: Named imports from mathUtils — curly braces required
import { add, subtract, PI } from './mathUtils.js';

// Requirement 6: Default import — no curly braces; name can be anything
import multiply from './mathUtils.js';

// Requirement 7: Namespace import — everything under StringUtils.*
import * as StringUtils from './stringUtils.js';

// Requirement 9: Re-export capitalize from stringUtils so other files can import it from main.js
export { capitalize } from './stringUtils.js';
// Import it locally too so we can demonstrate it
import { capitalize } from './stringUtils.js';

// Requirement 8: Use all imports
console.log(`5 + 3 = ${add(5, 3)}`);           // 8
console.log(`10 - 4 = ${subtract(10, 4)}`);     // 6
console.log(`PI = ${PI}`);                       // 3.14159
console.log(`4 × 7 = ${multiply(4, 7)}`);       // 28
console.log(`Capitalize "hello": ${StringUtils.capitalize('hello')}`);   // Hello
console.log(`Reverse "module": ${StringUtils.reverseString('module')}`); // eludom
console.log(`Re-exported capitalize "world": ${capitalize('world')}`);   // World
