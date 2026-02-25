// TodoItem.test.jsx — testing events + callbacks with mocks
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { TodoItem } from '../App'

describe('TodoItem', () => {
  it('renders the todo text', () => {
    render(<TodoItem id={1} text="Buy milk" done={false} onToggle={vi.fn()} onDelete={vi.fn()} />)
    expect(screen.getByText('Buy milk')).toBeInTheDocument()
  })

  it('checkbox is unchecked when done=false', () => {
    render(<TodoItem id={1} text="Task" done={false} onToggle={vi.fn()} onDelete={vi.fn()} />)
    expect(screen.getByRole('checkbox')).not.toBeChecked()
  })

  it('checkbox is checked when done=true', () => {
    render(<TodoItem id={1} text="Task" done={true} onToggle={vi.fn()} onDelete={vi.fn()} />)
    expect(screen.getByRole('checkbox')).toBeChecked()
  })

  it('calls onToggle with the correct id when checkbox is clicked', async () => {
    const user = userEvent.setup()
    const mockToggle = vi.fn()
    render(<TodoItem id={42} text="Task" done={false} onToggle={mockToggle} onDelete={vi.fn()} />)
    await user.click(screen.getByRole('checkbox'))
    expect(mockToggle).toHaveBeenCalledTimes(1)
    expect(mockToggle).toHaveBeenCalledWith(42)
  })

  it('calls onDelete with the correct id when delete button is clicked', async () => {
    const user = userEvent.setup()
    const mockDelete = vi.fn()
    render(<TodoItem id={7} text="Task" done={false} onToggle={vi.fn()} onDelete={mockDelete} />)
    await user.click(screen.getByRole('button', { name: /Delete/i }))
    expect(mockDelete).toHaveBeenCalledWith(7)
  })
})
