import { useState } from 'react'
import { ImagesIcon, PlusIcon, XIcon } from '@phosphor-icons/react'
import { acceptFor, PHOTO_LIMITS, PHOTO_RULES } from '../lib/fileValidation.js'
import { formatBytes } from '../lib/format.js'
import { useObjectUrl, Viewfinder } from './ui.jsx'

const MAX = PHOTO_LIMITS.UPLOAD.max
const ids = new WeakMap()
let nextId = 0
const keyOf = (file) => ids.get(file) ?? (ids.set(file, ++nextId), nextId)

function Thumb({ file, index, onRemove }) {
  const url = useObjectUrl(file)
  const cover = index === 0
  return (
    <li className="thumb">
      {url && <img src={url} alt={`Foto ${index + 1}${cover ? ', copertina' : ''}: ${file.name}`} />}
      <span className="thumb-order" aria-hidden="true">
        {index + 1}
      </span>
      {cover && (
        <span className="thumb-cover" aria-hidden="true">
          Copertina
        </span>
      )}
      <button type="button" className="thumb-remove" onClick={onRemove} aria-label={`Rimuovi ${file.name}`}>
        <XIcon size={14} weight="bold" />
      </button>
    </li>
  )
}

/** Selezione multipla (click o trascinamento) con anteprime numerate nell'ordine di invio. */
export default function PhotoPicker({ files, onAdd, onRemove }) {
  const [dragging, setDragging] = useState(false)
  const dropHandlers = {
    onDragOver: (e) => {
      e.preventDefault()
      setDragging(true)
    },
    onDragLeave: () => setDragging(false),
    onDrop: (e) => {
      e.preventDefault()
      setDragging(false)
      onAdd([...e.dataTransfer.files])
    },
  }
  const input = (
    <input
      type="file"
      multiple
      accept={acceptFor(PHOTO_RULES)}
      className="sr-only"
      onChange={(e) => {
        onAdd([...e.target.files])
        e.target.value = ''
      }}
    />
  )

  if (files.length === 0) {
    return (
      <label className={`dropzone ${dragging ? 'is-dragging' : ''}`} {...dropHandlers}>
        {input}
        <Viewfinder />
        <span className="dropzone-icon">
          <ImagesIcon size={30} aria-hidden="true" />
        </span>
        <strong>Scegli le foto</strong>
        <span className="dropzone-hint">
          oppure trascinale qui. JPEG, PNG o WEBP fino a 10 MB, al massimo {MAX} foto.
        </span>
      </label>
    )
  }

  const totalSize = files.reduce((sum, file) => sum + file.size, 0)
  return (
    <div className={`picker ${dragging ? 'is-dragging' : ''}`} {...dropHandlers}>
      <ol className="thumbs" aria-label="Foto selezionate, nell'ordine di pubblicazione">
        {files.map((file, i) => (
          <Thumb key={keyOf(file)} file={file} index={i} onRemove={() => onRemove(i)} />
        ))}
        {files.length < MAX && (
          <li>
            <label className="thumb-add">
              {input}
              <Viewfinder />
              <PlusIcon size={24} aria-hidden="true" />
              <span>Aggiungi</span>
            </label>
          </li>
        )}
      </ol>
      <p className="picker-count">
        {files.length} di {MAX} foto, {formatBytes(totalSize)}
      </p>
    </div>
  )
}
