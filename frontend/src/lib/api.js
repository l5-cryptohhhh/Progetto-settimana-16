const BASE_URL = (import.meta.env.VITE_API_URL ?? 'http://localhost:8080').replace(/\/$/, '')
const TOKEN_KEY = 'scatto.token'
const USER_KEY = 'scatto.user'

export const session = {
  get token() {
    return localStorage.getItem(TOKEN_KEY)
  },
  get user() {
    try {
      return JSON.parse(localStorage.getItem(USER_KEY))
    } catch {
      return null
    }
  },
  save(token, user) {
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(user))
  },
  clear() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  },
}

export class ApiError extends Error {
  constructor(status, message, details = []) {
    super(message)
    this.status = status
    this.details = details
  }
}

// Usati solo se la risposta non contiene il JSON d'errore del backend
const FALLBACK_MESSAGES = {
  401: 'Sessione scaduta: accedi di nuovo.',
  403: 'Non hai i permessi per questa operazione.',
  404: 'Contenuto non trovato.',
  413: 'File troppo grande.',
  415: 'Formato del file non supportato.',
  502: 'Servizio di geocoding non disponibile, riprova tra poco.',
}

let unauthorizedHandler = () => {}
export const onUnauthorized = (handler) => {
  unauthorizedHandler = handler
}

/** Gli URL delle foto arrivano relativi (/api/files/photos/...). */
export const photoUrl = (path) => BASE_URL + path

/**
 * fetch verso il backend. Aggiunge il token, trasforma le risposte d'errore in ApiError
 * e, se una chiamata con token riceve 401, chiude la sessione.
 * Con FormData il Content-Type multipart lo imposta il browser.
 */
export async function api(path, { method = 'GET', json, body, signal, raw = false } = {}) {
  const token = session.token
  const headers = {}
  if (token) headers.Authorization = `Bearer ${token}`
  if (json !== undefined) {
    headers['Content-Type'] = 'application/json'
    body = JSON.stringify(json)
  }

  let response
  try {
    response = await fetch(BASE_URL + path, { method, headers, body, signal })
  } catch (err) {
    if (err.name === 'AbortError') throw err
    throw new ApiError(0, 'Impossibile raggiungere il server. Verifica che il backend sia avviato.')
  }

  if (!response.ok) {
    const data = await response.json().catch(() => ({}))
    if (response.status === 401 && token) {
      session.clear()
      unauthorizedHandler()
    }
    const message = data.message || FALLBACK_MESSAGES[response.status] || `Errore ${response.status}`
    throw new ApiError(response.status, message, data.details ?? [])
  }
  if (raw) return response
  return response.status === 204 ? null : response.json()
}

/** Download di un file protetto: un semplice <a href> non invierebbe il token. */
export async function downloadFile(path, filename) {
  const blob = await (await api(path, { raw: true })).blob()
  const url = URL.createObjectURL(blob)
  const link = Object.assign(document.createElement('a'), { href: url, download: filename })
  document.body.append(link)
  link.click()
  link.remove()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
