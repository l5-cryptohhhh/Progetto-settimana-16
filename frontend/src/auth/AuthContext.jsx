import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { useNavigate } from 'react-router'
import { api, onUnauthorized, session } from '../lib/api.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const navigate = useNavigate()
  const [user, setUser] = useState(() => (session.token ? session.user : null))

  // 401 da una chiamata protetta: api.js ha già cancellato il token, si torna al login
  useEffect(() => {
    onUnauthorized(() => {
      setUser(null)
      navigate('/login', { replace: true, state: { expired: true } })
    })
  }, [navigate])

  // All'avvio verifica il token salvato e aggiorna i dati dell'utente
  useEffect(() => {
    if (!session.token) return
    api('/api/users/me')
      .then((me) => {
        if (!session.token) return
        session.save(session.token, me)
        setUser(me)
      })
      .catch(() => {})
  }, [])

  const authenticate = useCallback(async (path, payload) => {
    const { token, user } = await api(path, { method: 'POST', json: payload })
    session.save(token, user)
    setUser(user)
  }, [])

  const logout = useCallback(() => {
    session.clear()
    setUser(null)
    navigate('/login', { replace: true })
  }, [navigate])

  const value = {
    user,
    login: (payload) => authenticate('/api/auth/login', payload),
    register: (payload) => authenticate('/api/auth/register', payload),
    logout,
  }
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
