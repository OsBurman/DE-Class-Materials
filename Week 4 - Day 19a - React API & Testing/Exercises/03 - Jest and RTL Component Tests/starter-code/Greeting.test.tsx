import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Greeting } from './Greeting';

// TODO: write a describe('Greeting', () => { ... }) block containing 4 tests:

// Test 1 – "renders the greeting with the given name"
//   render(<Greeting name="Alice" />)
//   TODO: assert that the text 'Hello, Alice!' is in the document
//   Hint: screen.getByText(...) or screen.getByRole('heading', { name: /hello, alice/i })

// Test 2 – "renders the Change Name button"
//   render(<Greeting name="Alice" />)
//   TODO: assert a button with the accessible name 'Change Name' exists
//   Hint: screen.getByRole('button', { name: /change name/i })

// Test 3 – "changes the name to World when button is clicked"
//   render(<Greeting name="Alice" />)
//   TODO: fireEvent.click the button
//   TODO: assert 'Hello, World!' is in the document

// Test 4 – "does not render old name after button click"
//   render(<Greeting name="Alice" />)
//   TODO: fireEvent.click the button
//   TODO: assert 'Hello, Alice!' is NO LONGER in the document
//   Hint: use screen.queryByText — it returns null instead of throwing when not found
