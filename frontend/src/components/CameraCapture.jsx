import { useEffect, useRef, useState } from 'react'
import { ArrowCounterClockwiseIcon, CameraIcon, WarningCircleIcon } from '@phosphor-icons/react'
import { acceptFor, PHOTO_RULES } from '../lib/fileValidation.js'
import { Spinner, useObjectUrl, Viewfinder } from './ui.jsx'

function cameraMessage(err) {
  switch (err?.name) {
    case 'NotAllowedError':
    case 'SecurityError':
      return "Accesso alla fotocamera negato. Consenti l'accesso nelle impostazioni del browser e riprova."
    case 'NotFoundError':
    case 'OverconstrainedError':
      return 'Nessuna fotocamera disponibile su questo dispositivo.'
    case 'NotReadableError':
      return "La fotocamera è già in uso da un'altra applicazione."
    default:
      return 'Impossibile avviare la fotocamera.'
  }
}

/**
 * Una sola foto dalla fotocamera: getUserMedia + <video> + <canvas> -> File JPEG.
 * Se la webcam non è disponibile si usa <input capture>, che su mobile apre la fotocamera di sistema.
 */
export default function CameraCapture({ photo, onCapture, onRetake }) {
  const video = useRef(null)
  const [status, setStatus] = useState('starting') // starting | live | error
  const [message, setMessage] = useState('')
  const [attempt, setAttempt] = useState(0)
  const [flash, setFlash] = useState(0)
  const preview = useObjectUrl(photo)
  const supported = Boolean(navigator.mediaDevices?.getUserMedia)

  useEffect(() => {
    if (photo) return
    if (!supported) {
      setMessage('Questo browser non permette di usare la fotocamera dalla pagina.')
      setStatus('error')
      return
    }
    let stream
    let cancelled = false
    setStatus('starting')
    navigator.mediaDevices
      .getUserMedia({ video: { facingMode: 'environment', width: { ideal: 1920 }, height: { ideal: 1440 } }, audio: false })
      .then((s) => {
        stream = s
        if (cancelled) return s.getTracks().forEach((track) => track.stop())
        if (video.current) video.current.srcObject = s
      })
      .catch((err) => {
        if (cancelled) return
        setMessage(cameraMessage(err))
        setStatus('error')
      })
    return () => {
      cancelled = true
      stream?.getTracks().forEach((track) => track.stop())
    }
  }, [photo, attempt, supported])

  function shoot() {
    const el = video.current
    const canvas = document.createElement('canvas')
    canvas.width = el.videoWidth
    canvas.height = el.videoHeight
    canvas.getContext('2d').drawImage(el, 0, 0)
    setFlash((n) => n + 1)
    canvas.toBlob(
      (blob) => blob && onCapture(new File([blob], 'foto-camera.jpg', { type: 'image/jpeg' })),
      'image/jpeg',
      0.92,
    )
  }

  if (photo) {
    return (
      <div className="camera">
        <div className="camera-frame">
          {preview && <img src={preview} alt="Anteprima della foto scattata" />}
          <Viewfinder dot />
          {flash > 0 && <span key={flash} className="camera-flash" aria-hidden="true" />}
        </div>
        <div className="camera-controls">
          <button type="button" className="btn btn-secondary" onClick={onRetake}>
            <ArrowCounterClockwiseIcon size={18} aria-hidden="true" />
            Rifai la foto
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="camera">
      <div className={`camera-frame ${status === 'live' ? 'is-live' : ''}`}>
        {status !== 'error' && (
          <>
            <video ref={video} autoPlay playsInline muted onLoadedMetadata={() => setStatus('live')} />
            <Viewfinder dot />
          </>
        )}
        {status === 'starting' && (
          <div className="camera-status">
            <Spinner />
            <p>Avvio della fotocamera…</p>
          </div>
        )}
        {status === 'error' && (
          <div className="camera-status" role="alert">
            <WarningCircleIcon size={32} aria-hidden="true" />
            <p>{message}</p>
            <div className="camera-fallback">
              {supported && (
                <button type="button" className="btn btn-glass btn-small" onClick={() => setAttempt((n) => n + 1)}>
                  Riprova
                </button>
              )}
              <label className="btn btn-primary btn-small">
                <CameraIcon size={18} aria-hidden="true" />
                Apri la fotocamera
                <input
                  type="file"
                  accept={acceptFor(PHOTO_RULES)}
                  capture="environment"
                  className="sr-only"
                  onChange={(e) => {
                    const file = e.target.files[0]
                    e.target.value = ''
                    if (file) onCapture(file)
                  }}
                />
              </label>
            </div>
          </div>
        )}
      </div>
      {status !== 'error' && (
        <div className="camera-controls">
          <button type="button" className="shutter" onClick={shoot} disabled={status !== 'live'} aria-label="Scatta la foto" />
        </div>
      )}
    </div>
  )
}
