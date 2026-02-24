# Exercise 04: Creating a Custom Pipe

## Objective
Build two custom Angular pipes by implementing the `PipeTransform` interface, and apply them in a template alongside arguments.

## Background
Custom pipes let you package reusable transformation logic for use directly in templates. You will create two pipes: `TruncatePipe` (cuts a string to a max length and appends "…") and `PhoneFormatPipe` (formats a 10-digit number string into `(XXX) XXX-XXXX`).

## Requirements

### Pipe 1 — `TruncatePipe`
1. Create `truncate.pipe.ts` with a class `TruncatePipe` decorated with `@Pipe({ name: 'truncate' })`.
2. Implement `PipeTransform` — the `transform` method signature should be:
   ```ts
   transform(value: string, maxLength: number = 30): string
   ```
3. If `value.length <= maxLength`, return the value unchanged.
4. Otherwise, return `value.slice(0, maxLength) + '…'`.

### Pipe 2 — `PhoneFormatPipe`
1. Create `phone-format.pipe.ts` with `@Pipe({ name: 'phoneFormat' })`.
2. The `transform(value: string): string` method should:
   - Strip all non-digit characters from `value`.
   - If the result is not exactly 10 digits, return the original `value` unchanged.
   - Otherwise format as `(XXX) XXX-XXXX`.

### Demo Component — `PipeDemoComponent`
1. Declare a `descriptions` array of 4 strings of varying lengths.
2. Declare a `phoneNumbers` array of 4 strings (mix of valid 10-digit and invalid formats).
3. In the template:
   - Render each description with `| truncate` (default max) and `| truncate:15` (max 15).
   - Render each phone number with `| phoneFormat`.

4. Declare both pipes and the demo component in `AppModule`.

## Hints
- A pipe class must implement `PipeTransform` and have its `transform` method — Angular calls this automatically.
- `@Pipe({ name: '...' })` sets the name used in templates: `{{ value | truncate }}`.
- Pipe arguments are passed with a colon: `{{ value | truncate:15 }}`.
- Declare pipes in `declarations: []` in `AppModule` exactly like components.

## Expected Output
```
Truncate Pipe Demo (default 30 chars / max 15)
-----------------------------------------------
"The quick brown fox..."        → default: "The quick brown fox jumped ove…"  | max-15: "The quick brown…"
"Short"                         → default: "Short"                             | max-15: "Short"

Phone Format Pipe Demo
-----------------------------------------------
"1234567890"    → (123) 456-7890
"(800) 555-0199" → (800) 555-0199   (already formatted, stripped = 10 digits)
"123-456"       → 123-456           (invalid, returned unchanged)
```
