export function formatDate(dateStr) {
  if (!dateStr) return '—'
  const date = new Date(dateStr + 'T00:00:00')
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

export function isOverdue(dateStr) {
  if (!dateStr) return false
  return new Date(dateStr + 'T00:00:00') < new Date(new Date().toDateString())
}

export function daysUntil(dateStr) {
  if (!dateStr) return null
  const diff = new Date(dateStr + 'T00:00:00') - new Date(new Date().toDateString())
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
}
