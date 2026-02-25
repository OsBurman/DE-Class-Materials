// GreetingCard.test.jsx — testing props and conditional rendering
import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { GreetingCard } from '../App'

describe('GreetingCard', () => {
  it('renders the name in a heading', () => {
    render(<GreetingCard name="Alice" />)
    expect(screen.getByRole('heading', { name: /Alice/i })).toBeInTheDocument()
  })

  it('renders the default role as Student', () => {
    render(<GreetingCard name="Bob" />)
    expect(screen.getByText(/Role: Student/i)).toBeInTheDocument()
  })

  it('renders a custom role', () => {
    render(<GreetingCard name="Carol" role="Instructor" />)
    expect(screen.getByText(/Role: Instructor/i)).toBeInTheDocument()
  })

  it('shows fallback message when name is empty', () => {
    render(<GreetingCard name="" />)
    expect(screen.getByText(/Please provide a name/i)).toBeInTheDocument()
  })

  it('renders the greeting-card container with a name', () => {
    render(<GreetingCard name="Dave" />)
    expect(screen.getByTestId('greeting-card')).toBeInTheDocument()
  })
})
