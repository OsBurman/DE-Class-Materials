import NotesWidget from './components/NotesWidget'
import UserProfileWidget from './components/UserProfileWidget'
import SearchWidget from './components/SearchWidget'
import ToggleWidget from './components/ToggleWidget'
import './App.css'

function App() {
  return (
    <div className="app">
      <h1>🪝 Custom Hooks Dashboard</h1>
      <p className="subtitle">Four widgets, each powered by a custom React hook.</p>
      <div className="dashboard">
        <NotesWidget />
        <UserProfileWidget />
        <SearchWidget />
        <ToggleWidget />
      </div>
    </div>
  )
}

export default App
