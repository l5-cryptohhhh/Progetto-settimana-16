import { useEffect } from 'react'
import { Link, Navigate, NavLink, Outlet, useLocation } from 'react-router'
import { HouseIcon, PlusIcon, SignOutIcon, UserCircleIcon } from '@phosphor-icons/react'
import { useAuth } from '../auth/AuthContext.jsx'
import Logo from './Logo.jsx'
import { Avatar } from './ui.jsx'

const LINKS = [
  { to: '/', label: 'Feed', Icon: HouseIcon, end: true },
  { to: '/profile', label: 'Profilo', Icon: UserCircleIcon },
]

export default function Shell() {
  const { user, logout } = useAuth()
  const location = useLocation()

  // Ogni pagina si apre dall'alto (BrowserRouter non ripristina lo scroll)
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [location.pathname])

  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname }} />

  return (
    <div className="shell">
      {/* Desktop: colonna laterale */}
      <aside className="rail">
        <Link to="/" className="rail-logo" aria-label="Scatto, torna al feed">
          <Logo />
        </Link>
        <nav className="rail-nav" aria-label="Navigazione principale">
          {LINKS.map(({ to, label, Icon, end }) => (
            <NavLink key={to} to={to} end={end} className="rail-link">
              {({ isActive }) => (
                <>
                  <Icon size={22} weight={isActive ? 'fill' : 'regular'} aria-hidden="true" />
                  {label}
                </>
              )}
            </NavLink>
          ))}
          <NavLink to="/new" className="btn btn-primary btn-large rail-new">
            <PlusIcon size={20} weight="bold" aria-hidden="true" />
            Nuovo post
          </NavLink>
        </nav>
        <div className="rail-foot">
          <Link to="/profile" className="rail-user">
            <Avatar name={user.username} size={36} />
            <span>
              <strong>{user.username}</strong>
              <small>{user.email}</small>
            </span>
          </Link>
          <button type="button" className="icon-btn" onClick={logout} aria-label="Esci" title="Esci">
            <SignOutIcon size={20} />
          </button>
        </div>
      </aside>

      {/* Mobile: barra in alto e schede in basso, con il + viola al centro */}
      <header className="topbar">
        <Link to="/" className="topbar-logo" aria-label="Scatto, torna al feed">
          <Logo />
        </Link>
        <button type="button" className="icon-btn" onClick={logout} aria-label="Esci">
          <SignOutIcon size={22} />
        </button>
      </header>

      <main className="page">
        <Outlet />
      </main>

      <nav className="tabbar" aria-label="Navigazione principale">
        <NavLink to="/" end className="tab">
          {({ isActive }) => (
            <>
              <HouseIcon size={24} weight={isActive ? 'fill' : 'regular'} aria-hidden="true" />
              <span>Feed</span>
            </>
          )}
        </NavLink>
        <NavLink to="/new" className="tab-new" aria-label="Nuovo post">
          <PlusIcon size={26} weight="bold" aria-hidden="true" />
        </NavLink>
        <NavLink to="/profile" className="tab">
          {({ isActive }) => (
            <>
              <UserCircleIcon size={24} weight={isActive ? 'fill' : 'regular'} aria-hidden="true" />
              <span>Profilo</span>
            </>
          )}
        </NavLink>
      </nav>
    </div>
  )
}
