// Counter.test.jsx — testing useState + user interactions
import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { Counter } from '../App'

describe('Counter', () => {
  it('renders initial count of 0', () => {
    render(<Counter />)
    expect(screen.getByTestId('count-display')).toHaveTextContent('0')
  })

  it('renders with a custom initialCount', () => {
    render(<Counter initialCount={10} />)
    expect(screen.getByTestId('count-display')).toHaveTextContent('10')
  })

  it('increments count when + button is clicked', async () => {
    const user = userEvent.setup()
    render(<Counter />)
    await user.click(screen.getByRole('button', { name: /Increment/i }))
    expect(screen.getByTestId('count-display')).toHaveTextContent('1')
  })

  it('decrements count when − button is clicked', async () => {
    const user = userEvent.setup()
    render(<Counter initialCount={5} />)
    await user.click(screen.getByRole('button', { name: /Decrement/i }))
    expect(screen.getByTestId('count-display')).toHaveTextContent('4')
  })

  it('resets count to initialCount', async () => {
    const user = userEvent.setup()
    render(<Counter initialCount={3} />)
    await user.click(screen.getByRole('button', { name: /Increment/i }))
    await user.click(screen.getByRole('button', { name: /Increment/i }))
    await user.click(screen.getByRole('button', { name: /Reset/i }))
    expect(screen.getByTestId('count-display')).toHaveTextContent('3')
  })

  it('uses a custom step', async () => {
    const user = userEvent.setup()
    render(<Counter step={5} />)
    await user.click(screen.getByRole('button', { name: /Increment/i }))
    expect(screen.getByTestId('count-display')).toHaveTextContent('5')
  })
})
