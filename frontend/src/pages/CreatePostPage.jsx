import { useState } from 'react'
import { useNavigate } from 'react-router'
import { CameraIcon, ImagesIcon } from '@phosphor-icons/react'
import CameraCapture from '../components/CameraCapture.jsx'
import LocationPicker from '../components/LocationPicker.jsx'
import PhotoPicker from '../components/PhotoPicker.jsx'
import { ErrorNote, Segmented, Spinner } from '../components/ui.jsx'
import { api } from '../lib/api.js'
import { PHOTO_LIMITS, PHOTO_RULES, validateFiles } from '../lib/fileValidation.js'
import './create.css'

const CAPTION_MAX = 2000
const ADDRESS_MAX = 500
const SOURCES = [
  { value: 'CAMERA', label: 'Scatta foto', icon: <CameraIcon size={18} aria-hidden="true" /> },
  { value: 'UPLOAD', label: 'Carica foto', icon: <ImagesIcon size={18} aria-hidden="true" /> },
]

export default function CreatePostPage() {
  const navigate = useNavigate()
  // Si parte dall'upload: la fotocamera chiede il permesso solo quando l'utente la sceglie
  const [source, setSource] = useState('UPLOAD')
  const [photos, setPhotos] = useState({ CAMERA: [], UPLOAD: [] })
  const [fileErrors, setFileErrors] = useState([])
  const [caption, setCaption] = useState('')
  const [location, setLocation] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)
  const selected = photos[source]
  const limits = PHOTO_LIMITS[source]

  function changeSource(next) {
    setSource(next)
    setFileErrors([])
    setError(null)
  }

  async function addPhotos(files) {
    const { valid, errors } = await validateFiles(files, PHOTO_RULES)
    if (source === 'CAMERA') {
      if (valid.length) setPhotos((p) => ({ ...p, CAMERA: [valid[0]] }))
    } else {
      const room = limits.max - photos.UPLOAD.length
      const extra = valid.length - room
      if (extra > 0) {
        errors.push(`Puoi allegare al massimo ${limits.max} foto: ${extra === 1 ? '1 foto non è stata aggiunta' : `${extra} foto non sono state aggiunte`}.`)
      }
      setPhotos((p) => ({ ...p, UPLOAD: [...p.UPLOAD, ...valid.slice(0, Math.max(room, 0))] }))
    }
    setFileErrors(errors)
    setError(null)
  }

  async function submit(e) {
    e.preventDefault()
    const problem =
      selected.length < limits.min
        ? source === 'CAMERA'
          ? 'Scatta una foto prima di pubblicare.'
          : 'Aggiungi almeno una foto.'
        : selected.length > limits.max
          ? `Puoi allegare al massimo ${limits.max} foto.`
          : caption.length > CAPTION_MAX
            ? `La didascalia supera i ${CAPTION_MAX} caratteri.`
            : null
    if (problem) return setError(new Error(problem))

    // Ultimo controllo dei file prima dell'invio; il backend li verifica di nuovo
    const { errors } = await validateFiles(selected, PHOTO_RULES)
    if (errors.length) return setError(errors)

    const form = new FormData()
    form.append('source', source)
    selected.forEach((file) => form.append('photos', file))
    if (caption.trim()) form.append('caption', caption.trim())
    if (location) {
      form.append('latitude', location.latitude)
      form.append('longitude', location.longitude)
      if (location.address) form.append('address', location.address.slice(0, ADDRESS_MAX))
    }

    setSubmitting(true)
    setError(null)
    try {
      await api('/api/posts', { method: 'POST', body: form })
      navigate('/', { state: { published: true } })
    } catch (err) {
      setError(err)
      setSubmitting(false)
    }
  }

  return (
    <form className="create" onSubmit={submit} noValidate>
      <header className="page-head">
        <h1>Nuovo post</h1>
        <p className="page-lead">
          Scatta una foto con la fotocamera oppure carica da 1 a 10 immagini, poi aggiungi didascalia e luogo.
        </p>
      </header>

      <div className="create-grid">
        <section className="create-media" aria-label="Foto del post">
          <Segmented label="Origine delle foto" options={SOURCES} value={source} onChange={changeSource} />
          {source === 'CAMERA' ? (
            <CameraCapture
              photo={photos.CAMERA[0] ?? null}
              onCapture={(file) => addPhotos([file])}
              onRetake={() => setPhotos((p) => ({ ...p, CAMERA: [] }))}
            />
          ) : (
            <PhotoPicker
              files={photos.UPLOAD}
              onAdd={addPhotos}
              onRemove={(index) => setPhotos((p) => ({ ...p, UPLOAD: p.UPLOAD.filter((_, i) => i !== index) }))}
            />
          )}
          <ErrorNote error={fileErrors} />
        </section>

        <section className="create-details" aria-label="Dettagli del post">
          <div className="field">
            <label htmlFor="caption">
              Didascalia <span className="optional">facoltativa</span>
            </label>
            <textarea
              id="caption"
              rows={4}
              value={caption}
              maxLength={CAPTION_MAX}
              onChange={(e) => setCaption(e.target.value)}
              placeholder="Racconta qualcosa di questo scatto"
            />
            <p className="field-hint counter" aria-live="polite">
              {caption.length} / {CAPTION_MAX}
            </p>
          </div>

          <fieldset className="field">
            <legend>
              Posizione <span className="optional">facoltativa</span>
            </legend>
            <LocationPicker value={location} onChange={setLocation} />
          </fieldset>

          <ErrorNote error={error} />
          <button type="submit" className="btn btn-primary btn-large" disabled={submitting}>
            {submitting && <Spinner />}
            {submitting ? 'Pubblicazione…' : 'Pubblica'}
          </button>
        </section>
      </div>
    </form>
  )
}
