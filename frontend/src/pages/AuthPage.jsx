import { useState } from 'react'
import { Link, Navigate, useLocation } from 'react-router'
import { HeartIcon, SunIcon, UsersThreeIcon } from '@phosphor-icons/react'
import { useAuth } from '../auth/AuthContext.jsx'
import Logo from '../components/Logo.jsx'
import { ErrorNote, Spinner, Viewfinder } from '../components/ui.jsx'
import './auth.css'

// Stesse regole della validazione del backend
const RULES = {
  username: (v) => (/^[A-Za-z0-9._]{3,30}$/.test(v) ? null : 'Da 3 a 30 caratteri: lettere, numeri, punto e trattino basso.'),
  email: (v) => (/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim()) ? null : 'Inserisci un indirizzo email valido.'),
  password: (v, isLogin) =>
    isLogin ? (v ? null : 'Inserisci la password.') : v.length >= 8 && v.length <= 72 ? null : 'La password deve avere da 8 a 72 caratteri.',
}

const COPY = {
  login: {
    title: 'Accedi',
    lead: 'Rivedi le tue foto e i luoghi in cui le hai scattate.',
    submit: 'Accedi',
    busy: 'Accesso…',
    switchText: 'Non hai un account?',
    switchLink: 'Registrati',
    switchTo: '/register',
  },
  register: {
    title: 'Crea un account',
    lead: 'Condividi le tue foto e i luoghi in cui le hai scattate.',
    submit: 'Crea account',
    busy: 'Creazione…',
    switchText: 'Hai già un account?',
    switchLink: 'Accedi',
    switchTo: '/login',
  },
}

// "La nostra essenza" dalla brand board
const VALUES = [
  { label: 'Persone vere', Icon: UsersThreeIcon },
  { label: 'Contenuti autentici', Icon: SunIcon },
  { label: 'Connessioni reali', Icon: HeartIcon },
]

export default function AuthPage({ mode }) {
  const isLogin = mode === 'login'
  const copy = COPY[mode]
  const fields = isLogin ? ['email', 'password'] : ['username', 'email', 'password']
  const { user, login, register } = useAuth()
  const location = useLocation()
  const [values, setValues] = useState({ username: '', email: '', password: '' })
  const [touched, setTouched] = useState({})
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState(null)

  if (user) return <Navigate to={location.state?.from ?? '/'} replace />

  const errors = Object.fromEntries(fields.map((name) => [name, RULES[name](values[name], isLogin)]))

  async function submit(e) {
    e.preventDefault()
    setTouched(Object.fromEntries(fields.map((name) => [name, true])))
    if (fields.some((name) => errors[name])) return
    setBusy(true)
    setError(null)
    try {
      const payload = Object.fromEntries(fields.map((name) => [name, name === 'email' ? values.email.trim() : values[name]]))
      await (isLogin ? login(payload) : register(payload))
    } catch (err) {
      setError(err)
      setBusy(false)
    }
  }

  const field = (name, label, inputProps, hint) => {
    const message = touched[name] && errors[name]
    return (
      <div className="field">
        <label htmlFor={name}>{label}</label>
        <input
          id={name}
          name={name}
          value={values[name]}
          aria-invalid={Boolean(message)}
          aria-describedby={message || hint ? `${name}-message` : undefined}
          onChange={(e) => setValues((v) => ({ ...v, [name]: e.target.value }))}
          onBlur={() => setTouched((t) => ({ ...t, [name]: true }))}
          {...inputProps}
        />
        {(message || hint) && (
          <p id={`${name}-message`} className={message ? 'field-error' : 'field-hint'}>
            {message || hint}
          </p>
        )}
      </div>
    )
  }

  return (
    <main className="auth">
      {/* Splash di brand: logo, slogan ed elementi grafici della brand board */}
      <div className="auth-brand">
        <svg className="auth-blob" viewBox="0 0 400 360" aria-hidden="true" focusable="false">
          <path
            fill="#5b21b6"
            d="M92 40c52-30 128-26 170 16 36 36 30 88 60 122 34 38 42 104-4 140-50 40-124 30-176 6C86 292 26 256 14 190 2 128 40 70 92 40Z"
          />
          <circle cx="268" cy="150" r="104" fill="#8b5cf6" opacity="0.55" />
        </svg>
        <span className="auth-stripes" aria-hidden="true" />
        <div className="auth-mark">
          <Viewfinder />
          <Logo dot="currentColor" className="auth-logo" />
          <p className="auth-tagline">Condividi ciò che ti ispira.</p>
        </div>
        <ul className="auth-values">
          {VALUES.map(({ label, Icon }) => (
            <li key={label}>
              <Icon size={26} aria-hidden="true" />
              {label}
            </li>
          ))}
        </ul>
      </div>

      <div className="auth-side">
        <div className="auth-panel">
          <h1>{copy.title}</h1>
          <p className="auth-lead">{copy.lead}</p>

          {location.state?.expired && (
            <p className="note note-info" role="status">
              La sessione è scaduta. Accedi di nuovo per continuare.
            </p>
          )}

          <form className="auth-form" onSubmit={submit} noValidate>
            {!isLogin &&
              field(
                'username',
                'Username',
                { autoComplete: 'username', autoCapitalize: 'none', spellCheck: false },
                '3-30 caratteri: lettere, numeri, punto e trattino basso',
              )}
            {field('email', 'Email', { type: 'email', autoComplete: 'email', inputMode: 'email', autoCapitalize: 'none' })}
            {field(
              'password',
              'Password',
              { type: 'password', autoComplete: isLogin ? 'current-password' : 'new-password' },
              isLogin ? null : 'Almeno 8 caratteri',
            )}
            <ErrorNote error={error} />
            <button type="submit" className="btn btn-primary btn-large" disabled={busy}>
              {busy && <Spinner />}
              {busy ? copy.busy : copy.submit}
            </button>
          </form>

          <p className="auth-switch">
            {copy.switchText} <Link to={copy.switchTo}>{copy.switchLink}</Link>
          </p>
        </div>
      </div>
    </main>
  )
}
