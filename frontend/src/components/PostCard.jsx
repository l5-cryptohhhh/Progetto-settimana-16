import { lazy, Suspense, useEffect, useRef, useState } from 'react'
import { Link } from 'react-router'
import {
  CameraIcon,
  CaretDownIcon,
  CaretLeftIcon,
  CaretRightIcon,
  ImagesIcon,
  MapPinIcon,
  TrashIcon,
} from '@phosphor-icons/react'
import { useAuth } from '../auth/AuthContext.jsx'
import { api, photoUrl } from '../lib/api.js'
import { formatDate, timeAgo } from '../lib/format.js'
import { Avatar, ConfirmDialog, ErrorNote, Photo, Viewfinder } from './ui.jsx'
import './posts.css'

// Leaflet si scarica solo quando si apre una mappa
const MapView = lazy(() => import('./MapView.jsx'))

/** true mentre l'elemento attraversa la fascia centrale dello schermo. */
function useCenterFocus(ref, enabled) {
  const [focused, setFocused] = useState(false)
  useEffect(() => {
    const el = ref.current
    if (!enabled || !el) return
    const observer = new IntersectionObserver(([entry]) => setFocused(entry.isIntersecting), {
      rootMargin: '-45% 0px -45% 0px',
    })
    observer.observe(el)
    return () => observer.disconnect()
  }, [enabled, ref])
  return enabled && focused
}

/**
 * Un post come opera appesa alla parete: la foto sopra, la didascalia sotto
 * con autore, testo, tecnica (fotocamera o upload) e luogo sempre nello stesso punto.
 */
export default function PostCard({ post, onDeleted, focusOnScroll = false }) {
  const { user } = useAuth()
  const media = useRef(null)
  const focused = useCenterFocus(media, focusOnScroll)
  const [mapOpen, setMapOpen] = useState(false)
  const [confirming, setConfirming] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [error, setError] = useState(null)
  const { author, location } = post
  const fromCamera = post.photoSource === 'CAMERA'
  const technique = fromCamera ? 'Fotocamera, scatto singolo' : `Upload, ${post.photos.length} foto`

  async function remove() {
    setDeleting(true)
    try {
      await api(`/api/posts/${post.id}`, { method: 'DELETE' })
      onDeleted?.(post.id)
    } catch (err) {
      setError(err)
      setDeleting(false)
      setConfirming(false)
    }
  }

  return (
    <article className={`work reveal-host ${focused ? 'is-focused' : ''}`}>
      <div className="work-media" ref={media}>
        <Carousel photos={post.photos} author={author.username} />
        <Viewfinder dot reveal />
      </div>

      <div className="label">
        <header className="label-head">
          <Link to={`/users/${author.id}`} className="label-author">
            <Avatar name={author.username} size={36} />
            <span>
              <strong>{author.username}</strong>
              <time dateTime={post.createdAt} title={formatDate(post.createdAt, { dateStyle: 'long', timeStyle: 'short' })}>
                {timeAgo(post.createdAt)}
              </time>
            </span>
          </Link>
          {author.id === user.id && (
            <button type="button" className="icon-btn" aria-label="Elimina post" onClick={() => setConfirming(true)}>
              <TrashIcon size={20} />
            </button>
          )}
        </header>

        {post.caption && <p className="label-caption">{post.caption}</p>}

        <dl className="label-facts">
          <div>
            <dt>Tecnica</dt>
            <dd>
              {fromCamera ? <CameraIcon size={18} aria-hidden="true" /> : <ImagesIcon size={18} aria-hidden="true" />}
              <span>{technique}</span>
            </dd>
          </div>
          {location && (
            <div>
              <dt>Luogo</dt>
              <dd>
                <button
                  type="button"
                  className="place-toggle"
                  aria-expanded={mapOpen}
                  onClick={() => setMapOpen((open) => !open)}
                >
                  <MapPinIcon size={18} weight="fill" aria-hidden="true" />
                  <span className="place-label">
                    {location.address ?? `${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)}`}
                  </span>
                  <span className="sr-only">{mapOpen ? ', nascondi la mappa' : ', mostra sulla mappa'}</span>
                  <CaretDownIcon size={16} className="place-caret" aria-hidden="true" />
                </button>
              </dd>
            </div>
          )}
        </dl>

        {location && mapOpen && (
          <Suspense fallback={<div className="map post-map skeleton" />}>
            <MapView position={[location.latitude, location.longitude]} zoom={14} interactive={false} className="post-map" />
          </Suspense>
        )}
        <ErrorNote error={error} />
      </div>

      <ConfirmDialog
        open={confirming}
        title="Eliminare il post?"
        message="Foto, didascalia e posizione verranno rimosse definitivamente."
        confirmLabel="Elimina"
        busy={deleting}
        onConfirm={remove}
        onCancel={() => setConfirming(false)}
      />
    </article>
  )
}

/** Carosello con scroll-snap nativo: swipe, inerzia e interruzione li gestisce il browser. */
function Carousel({ photos, author }) {
  const track = useRef(null)
  const [index, setIndex] = useState(0)
  const many = photos.length > 1

  function go(next) {
    const el = track.current
    const smooth = !matchMedia('(prefers-reduced-motion: reduce)').matches
    el.scrollTo({ left: next * el.clientWidth, behavior: smooth ? 'smooth' : 'auto' })
  }

  return (
    <div className="carousel">
      <ul
        ref={track}
        className="carousel-track"
        tabIndex={many ? 0 : undefined}
        aria-label={many ? `${photos.length} foto, scorri per vederle` : undefined}
        onScroll={many ? (e) => setIndex(Math.round(e.currentTarget.scrollLeft / e.currentTarget.clientWidth)) : undefined}
      >
        {photos.map((photo, i) => (
          <li key={photo.id}>
            <Photo
              src={photoUrl(photo.url)}
              alt={many ? `Foto ${i + 1} di ${photos.length} di ${author}` : `Foto di ${author}`}
              loading="lazy"
              decoding="async"
              draggable="false"
            />
          </li>
        ))}
      </ul>
      {many && (
        <>
          <button
            type="button"
            className="carousel-nav prev"
            onClick={() => go(index - 1)}
            disabled={index === 0}
            aria-label="Foto precedente"
          >
            <CaretLeftIcon size={18} weight="bold" />
          </button>
          <button
            type="button"
            className="carousel-nav next"
            onClick={() => go(index + 1)}
            disabled={index === photos.length - 1}
            aria-label="Foto successiva"
          >
            <CaretRightIcon size={18} weight="bold" />
          </button>
          <div className="carousel-dots" aria-hidden="true">
            {photos.map((photo, i) => (
              <span key={photo.id} className={i === index ? 'active' : undefined} />
            ))}
          </div>
        </>
      )}
    </div>
  )
}

export function PostSkeleton() {
  return (
    <div className="work work-skeleton" aria-hidden="true">
      <div className="skeleton skeleton-photo" />
      <div className="label">
        <div className="label-head">
          <span className="skeleton skeleton-avatar" />
          <span className="skeleton skeleton-line" />
        </div>
        <span className="skeleton skeleton-line wide" />
      </div>
    </div>
  )
}
