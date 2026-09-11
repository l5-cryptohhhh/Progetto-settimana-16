import { lazy, Suspense, useEffect, useRef, useState } from 'react'
import { MagnifyingGlassIcon, MapPinIcon, XIcon } from '@phosphor-icons/react'
import { api } from '../lib/api.js'
import { ErrorNote, Spinner } from './ui.jsx'

const MapView = lazy(() => import('./MapView.jsx'))

// Nominatim accetta circa una richiesta al secondo: niente chiamate a ogni tasto o click
const DEBOUNCE_MS = 600

/** Posizione del post: punto sulla mappa (reverse geocoding) oppure ricerca di un indirizzo. */
export default function LocationPicker({ value, onChange }) {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState(null)
  const [searching, setSearching] = useState(false)
  const [resolving, setResolving] = useState(false)
  const [error, setError] = useState(null)
  const reverseTimer = useRef(null)
  const reverseId = useRef(0)

  // Indirizzo -> coordinate: debounce e minimo 3 caratteri
  useEffect(() => {
    const q = query.trim()
    if (q.length < 3) {
      setResults(null)
      return
    }
    const controller = new AbortController()
    const timer = setTimeout(async () => {
      setSearching(true)
      setError(null)
      try {
        setResults(await api(`/api/geocoding/search?q=${encodeURIComponent(q)}`, { signal: controller.signal }))
      } catch (err) {
        if (err.name !== 'AbortError') setError(err)
      } finally {
        if (!controller.signal.aborted) setSearching(false)
      }
    }, DEBOUNCE_MS)
    return () => {
      clearTimeout(timer)
      controller.abort()
      setSearching(false)
    }
  }, [query])

  useEffect(() => () => clearTimeout(reverseTimer.current), [])

  function cancelReverse() {
    clearTimeout(reverseTimer.current)
    reverseId.current++
    setResolving(false)
  }

  function choose(place) {
    cancelReverse()
    onChange({ latitude: place.latitude, longitude: place.longitude, address: place.displayName })
    setQuery('')
    setResults(null)
    setError(null)
  }

  // Coordinate -> indirizzo: il marker si sposta subito, l'indirizzo arriva dopo il debounce
  function pick(lat, lon) {
    const point = { latitude: Number(lat.toFixed(6)), longitude: Number(lon.toFixed(6)), address: null }
    cancelReverse()
    onChange(point)
    setError(null)
    setResolving(true)
    const id = reverseId.current
    reverseTimer.current = setTimeout(async () => {
      try {
        const place = await api(`/api/geocoding/reverse?lat=${point.latitude}&lon=${point.longitude}`)
        if (id === reverseId.current) onChange({ ...point, address: place.displayName })
      } catch (err) {
        // 404: nessun indirizzo per quel punto (per esempio in mare), restano le coordinate
        if (id === reverseId.current && err.status !== 404) setError(err)
      } finally {
        if (id === reverseId.current) setResolving(false)
      }
    }, DEBOUNCE_MS)
  }

  function clear() {
    cancelReverse()
    setError(null)
    onChange(null)
  }

  return (
    <div className="location">
      <div className="search">
        <MagnifyingGlassIcon size={18} className="search-icon" aria-hidden="true" />
        <input
          type="search"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              e.preventDefault()
              if (results?.[0]) choose(results[0])
            }
            if (e.key === 'Escape') setQuery('')
          }}
          placeholder="Cerca un indirizzo o un luogo"
          aria-label="Cerca un indirizzo o un luogo"
          autoComplete="off"
          role="combobox"
          aria-expanded={Boolean(results)}
          aria-controls="place-results"
          aria-autocomplete="list"
        />
        {searching && <Spinner label="Ricerca in corso" />}
        {results && (
          <ul id="place-results" className="search-results" role="listbox">
            {results.length === 0 ? (
              <li className="search-empty">Nessun risultato per “{query.trim()}”</li>
            ) : (
              results.map((place) => (
                <li key={`${place.latitude},${place.longitude}`} role="option" aria-selected="false">
                  <button type="button" onClick={() => choose(place)}>
                    <MapPinIcon size={18} aria-hidden="true" />
                    <span>{place.displayName}</span>
                  </button>
                </li>
              ))
            )}
          </ul>
        )}
      </div>

      <div className="location-map">
        <Suspense fallback={<div className="map skeleton" />}>
          <MapView position={value ? [value.latitude, value.longitude] : null} onPick={pick} />
        </Suspense>
        {!value && <p className="map-hint">Seleziona un punto sulla mappa</p>}
      </div>

      {value && (
        <div className="place-chosen">
          <MapPinIcon size={20} weight="fill" aria-hidden="true" />
          <div>
            <strong>{value.address ?? (resolving ? 'Ricerca dell’indirizzo…' : 'Punto selezionato')}</strong>
            <span>
              {value.latitude.toFixed(5)}, {value.longitude.toFixed(5)}
            </span>
          </div>
          <button type="button" className="btn btn-ghost btn-small" onClick={clear}>
            <XIcon size={16} aria-hidden="true" />
            Rimuovi
          </button>
        </div>
      )}
      <ErrorNote error={error} />
    </div>
  )
}
