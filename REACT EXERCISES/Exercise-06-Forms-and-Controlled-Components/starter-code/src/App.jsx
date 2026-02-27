import { useState } from 'react'
import ApplicationForm from './components/ApplicationForm'
import ConfirmationSummary from './components/ConfirmationSummary'
import './App.css'

// TODO 1: This App component manages which "screen" is shown — the form or the confirmation.
//   Add a piece of state called `submittedData` initialised to null.
//   When submittedData is null, show <ApplicationForm onSubmit={handleSubmit} />.
//   When submittedData is not null, show <ConfirmationSummary data={submittedData} onReset={handleReset} />.

// TODO 2: Implement `handleSubmit(formData)` — it receives the validated form object from
//   ApplicationForm and stores it in state (which switches the view to the confirmation screen).

// TODO 3: Implement `handleReset()` — sets submittedData back to null so the user can
//   fill out another application.

function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>💼 Job Application Portal</h1>
        <p>Fill out the form below to apply for an open position.</p>
      </header>
      <main className="app-main">
        {/* TODO 4: Conditionally render ApplicationForm or ConfirmationSummary
              based on whether submittedData is null. */}
        <ApplicationForm onSubmit={() => {}} />
      </main>
    </div>
  )
}

export default App
