import { useEffect, useRef } from 'react'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const ITALY = [42.5, 12.5]
// Marker in CSS: evita le immagini di default di Leaflet, che con Vite vanno configurate a parte
const pin = L.divIcon({ className: 'map-pin', html: '<span></span>', iconSize: [30, 34], iconAnchor: [15, 33] })
const reducedMotion = () => matchMedia('(prefers-reduced-motion: reduce)').matches

/**
 * Mappa Leaflet con tile OpenStreetMap.
 * position: [lat, lon] oppure null. onPick(lat, lon) viene chiamato al click se interactive.
 */
export default function MapView({ position, onPick, zoom = 15, interactive = true, className = '' }) {
  const container = useRef(null)
  const map = useRef(null)
  const marker = useRef(null)
  const pickRef = useRef(onPick)

  useEffect(() => {
    pickRef.current = onPick
  })

  useEffect(() => {
    const instance = L.map(container.current, {
      center: position ?? ITALY,
      zoom: position ? zoom : 5,
      zoomControl: interactive,
      dragging: interactive,
      touchZoom: interactive,
      doubleClickZoom: interactive,
      keyboard: interactive,
      scrollWheelZoom: false,
      boxZoom: false,
    })
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(instance)
    instance.attributionControl.setPrefix(false)
    if (interactive) {
      instance.on('click', (e) => {
        const { lat, lng } = e.latlng.wrap()
        pickRef.current?.(lat, lng)
      })
    }
    // La mappa può nascere in un contenitore non ancora visibile (dialog, pannello)
    const observer = new ResizeObserver(() => instance.invalidateSize())
    observer.observe(container.current)
    map.current = instance

    return () => {
      observer.disconnect()
      instance.remove()
      map.current = null
      marker.current = null
    }
    // position e zoom servono solo per la vista iniziale
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [interactive])

  const [lat, lon] = position ?? []
  useEffect(() => {
    const instance = map.current
    if (lat == null) {
      marker.current?.remove()
      marker.current = null
      return
    }
    if (marker.current) marker.current.setLatLng([lat, lon])
    else marker.current = L.marker([lat, lon], { icon: pin, keyboard: false, interactive: false }).addTo(instance)
    instance.setView([lat, lon], Math.max(instance.getZoom(), zoom), { animate: !reducedMotion() })
  }, [lat, lon, zoom])

  return <div ref={container} className={`map ${className}`} />
}
