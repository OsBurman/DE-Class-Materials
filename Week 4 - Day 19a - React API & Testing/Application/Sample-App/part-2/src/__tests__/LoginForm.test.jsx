// LoginForm.test.jsx — testing forms, validation, and submit behavior
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { LoginForm } from '../App'

describe('LoginForm', () => {
  it('renders email and password fields', () => {
    render(<LoginForm onLogin={vi.fn()} />)
    expect(screen.getByLabelText(/Email/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/Password/i)).toBeInTheDocument()
  })

  it('renders a submit button', () => {
    render(<LoginForm onLogin={vi.fn()} />)
    expect(screen.getByRole('button', { name: /Login/i })).toBeInTheDocument()
  })

  it('shows an error for invalid email', async () => {
    const user = userEvent.setup()
    render(<LoginForm onLogin={vi.fn()} />)
    await user.type(screen.getByLabelText(/Email/i), 'notanemail')
    await user.type(screen.getByLabelText(/Password/i), 'securepassword')
    await user.click(screen.getByRole('button', { name: /Login/i }))
    expect(screen.getByRole('alert')).toHaveTextContent(/Invalid email/i)
  })

  it('shows an error when password is too short', async () => {
    const user = userEvent.setup()
    render(<LoginForm onLogin={vi.fn()} />)
    await user.type(screen.getByLabelText(/Email/i), 'user@example.com')
    await user.type(screen.getByLabelText(/Password/i), 'abc')
    await user.click(screen.getByRole('button', { name: /Login/i }))
    expect(screen.getByRole('alert')).toHaveTextContent(/Password too short/i)
  })

  it('calls onLogin with credentials on valid submit', async () => {
    const user = userEvent.setup()
    const mockLogin = vi.fn()
    render(<LoginForm onLogin={mockLogin} />)
    await user.type(screen.getByLabelText(/Email/i), 'alice@example.com')
    await user.type(screen.getByLabelText(/Password/i), 'securepass')
    await user.click(screen.getByRole('button', { name: /Login/i }))
    expect(mockLogin).toHaveBeenCalledOnce()
    expect(mockLogin).toHaveBeenCalledWith({ email: 'alice@example.com', password: 'securepass' })
  })

  it('does not call onLogin when validation fails', async () => {
    const user = userEvent.setup()
    const mockLogin = vi.fn()
    render(<LoginForm onLogin={mockLogin} />)
    await user.type(screen.getByLabelText(/Email/i), 'bad-email')
    await user.type(screen.getByLabelText(/Password/i), '123')
    await user.click(screen.getByRole('button', { name: /Login/i }))
    expect(mockLogin).not.toHaveBeenCalled()
  })
})
