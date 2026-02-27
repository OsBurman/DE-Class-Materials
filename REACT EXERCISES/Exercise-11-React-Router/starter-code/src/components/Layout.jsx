import { Outlet } from 'react-router-dom'
import Navbar from './Navbar'

// Layout wraps all pages. <Outlet /> is where the matched child route renders.
function Layout({ user, cart, onLogout }) {
  return (
    <>
      <Navbar user={user} cart={cart} onLogout={onLogout} />
      <main>
        <Outlet />
      </main>
    </>
  )
}

export default Layout
