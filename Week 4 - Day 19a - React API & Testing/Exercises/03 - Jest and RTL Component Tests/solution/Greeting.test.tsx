import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Greeting } from './Greeting';

describe('Greeting', () => {
  it('renders the greeting with the given name', () => {
    render(<Greeting name="Alice" />);
    // getByText throws if not found, making the test fail with a clear message.
    expect(screen.getByText('Hello, Alice!')).toBeInTheDocument();
  });

  it('renders the Change Name button', () => {
    render(<Greeting name="Alice" />);
    // Querying by role + accessible name is more resilient than querying by text alone.
    expect(
      screen.getByRole('button', { name: /change name/i })
    ).toBeInTheDocument();
  });

  it('changes the name to World when button is clicked', () => {
    render(<Greeting name="Alice" />);
    fireEvent.click(screen.getByRole('button', { name: /change name/i }));
    expect(screen.getByText('Hello, World!')).toBeInTheDocument();
  });

  it('does not render old name after button click', () => {
    render(<Greeting name="Alice" />);
    fireEvent.click(screen.getByRole('button', { name: /change name/i }));
    // queryByText returns null when the element is absent (getByText would throw).
    expect(screen.queryByText('Hello, Alice!')).not.toBeInTheDocument();
  });
});
