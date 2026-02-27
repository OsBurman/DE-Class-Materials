import { useState } from 'react'
import ApplicationForm from './components/ApplicationForm'
import ConfirmationSummary from './components/ConfirmationSummary'
import './App.css'

function App() {
  const [submittedData, setSubmittedData] = useState(null)

  function handleSubmit(formData) {
    setSubmittedData(formData)
  }

  function handleReset() {
    setSubmittedData(null)
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>💼 Job Application Portal</h1>
        <p>Fill out the form below to apply for an open position.</p>
      </header>
      <main className="app-main">
        {submittedData === null
          ? <ApplicationForm onSubmit={handleSubmit} />
          : <ConfirmationSummary data={submittedData} onReset={handleReset} />
        }
      </main>
    </div>
  )
}

export default App
