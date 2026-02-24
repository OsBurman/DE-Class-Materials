export default function ErrorMessage({ message }) {
  return (
    <div className="error">
      <strong>Error:</strong> {message}
    </div>
  )
}
