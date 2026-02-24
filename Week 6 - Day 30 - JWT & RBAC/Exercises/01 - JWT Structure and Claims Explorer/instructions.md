# Exercise 01: JWT Structure and Claims Explorer

## Objective
Understand the three-part structure of a JSON Web Token by Base64-decoding a real JWT and extracting its header, payload, and signature.

## Background
A JSON Web Token (JWT) is a compact, URL-safe token used to transmit claims (pieces of information) between parties.
It consists of three Base64URL-encoded parts separated by dots: `header.payload.signature`.
Unlike a server session, a JWT is **self-contained** — the server does not need to store it; it simply verifies the token's signature on every request.

## Requirements

1. **Parse the token** — split the provided sample JWT string on `"."` to extract the three parts.
2. **Decode the header** — Base64URL-decode the first part and pretty-print the resulting JSON.
3. **Decode the payload** — Base64URL-decode the second part and pretty-print the resulting JSON.
4. **Print the signature** — print the raw (still encoded) signature string. Explain in a comment why it cannot be decoded to readable text.
5. **Extract specific claims** — from the decoded payload, print the following named values (parse the JSON manually or with `org.json` / plain string search):
   - `sub` (subject / username)
   - `role`
   - `iat` (issued-at as a readable timestamp)
   - `exp` (expiry as a readable timestamp)
6. **Compare token-based vs session-based** — add a `compareMethods()` method that prints a short comparison table (at least 3 differences) to stdout.

## Hints
- `java.util.Base64.getUrlDecoder()` handles Base64URL encoding without padding issues.
- Convert the decoded bytes to a `String` with `new String(bytes, StandardCharsets.UTF_8)`.
- `Instant.ofEpochSecond(long)` converts a Unix timestamp to a human-readable `Instant`.
- JWT `iat` and `exp` are stored as seconds since the Unix epoch (not milliseconds).

## Expected Output

```
=== JWT Parts ===
Header (raw):    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
Payload (raw):   eyJzdWIiOiJhbGljZSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMzYwMDB9
Signature (raw): SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

=== Decoded Header ===
{
  "alg": "HS256",
  "typ": "JWT"
}

=== Decoded Payload ===
{
  "sub": "alice",
  "role": "USER",
  "iat": 1700000000,
  "exp": 1700036000
}

=== Claims ===
Subject  : alice
Role     : USER
Issued At: 2023-11-14T22:13:20Z
Expires  : 2023-11-15T08:13:20Z

=== Token-Based vs Session-Based ===
Aspect               | Token-Based (JWT)          | Session-Based
---------------------|----------------------------|-----------------------------
Storage              | Client (localStorage/cookie)| Server (session store)
Scalability          | Stateless – scales easily  | Requires shared session store
Revocation           | Hard – must wait for expiry| Easy – delete server session
Server memory        | None                       | Session object per user
Cross-domain (CORS)  | Straightforward            | Requires cookie config
```
