import { useEffect, useRef, useState } from 'react'
import { ImageBrokenIcon, WarningCircleIcon } from '@phosphor-icons/react'

/** Monogramma neutro: sulla parete gli unici colori, oltre al viola delle azioni, sono le foto. */
export function Avatar({ name = '?', size = 40 }) {
  return (
    <span className="avatar" style={{ '--size': `${size}px` }} aria-hidden="true">
      {name[0]?.toUpperCase()}
    </span>
  )
}

/**
 * Angoli del mirino del logo. dot: punto viola al posto dell'angolo in alto a destra.
 * reveal: nascosti finché il contenitore .reveal-host non è a fuoco.
 */
export function Viewfinder({ dot = false, reveal = false, className = '' }) {
  const classes = ['vf', dot && 'vf-dot', reveal && 'vf-reveal', className].filter(Boolean).join(' ')
  return (
    <span className={classes} aria-hidden="true">
      <i />
      <i />
      <i />
      <i />
    </span>
  )
}

/** <img> che, se il file non si carica, mostra un riquadro "Foto non disponibile" al posto dell'icona rotta. */
export function Photo({ alt, ...props }) {
  const [failed, setFailed] = useState(false)
  if (failed) {
    return (
      <span
        className="photo-missing"
        role={alt ? 'img' : undefined}
        aria-label={alt ? `${alt}, non disponibile` : undefined}
        aria-hidden={alt ? undefined : true}
      >
        <span className="empty-frame">
          <Viewfinder />
          <ImageBrokenIcon size={26} aria-hidden="true" />
        </span>
        <span aria-hidden="true">Foto non disponibile</span>
      </span>
    )
  }
  return <img alt={alt} {...props} onError={() => setFailed(true)} />
}

export function Spinner({ label }) {
  return <span className="spinner" role={label ? 'status' : undefined} aria-label={label} />
}

/** Stato vuoto che spiega cosa comparirà e come arrivarci. */
export function EmptyState({ icon, title, children, action }) {
  return (
    <div className="empty">
      <span className="empty-frame">
        <Viewfinder dot />
        {icon}
      </span>
      <h2>{title}</h2>
      <p>{children}</p>
      {action}
    </div>
  )
}

/** Errore del backend (message + details) oppure elenco di messaggi. */
export function ErrorNote({ error, children }) {
  if (!error || (Array.isArray(error) && error.length === 0)) return null
  const [message, ...details] = Array.isArray(error) ? [null, ...error] : [error.message, ...(error.details ?? [])]
  return (
    <div className="note note-error" role="alert">
      <WarningCircleIcon size={20} weight="fill" aria-hidden="true" />
      <div>
        {message && <p>{message}</p>}
        {!message && details.length === 1 && <p>{details[0]}</p>}
        {(message ? details.length > 0 : details.length > 1) && (
          <ul>
            {details.map((detail, i) => (
              <li key={i}>{detail}</li>
            ))}
          </ul>
        )}
        {children}
      </div>
    </div>
  )
}

/** Controllo segmentato: il cursore scorre con una molla. */
export function Segmented({ label, options, value, onChange }) {
  const index = Math.max(0, options.findIndex((option) => option.value === value))
  return (
    <div className="segmented" role="radiogroup" aria-label={label} style={{ '--count': options.length, '--index': index }}>
      <span className="segmented-thumb" aria-hidden="true" />
      {options.map((option) => (
        <button
          key={option.value}
          type="button"
          role="radio"
          aria-checked={option.value === value}
          onClick={() => onChange(option.value)}
        >
          {option.icon}
          <span>{option.label}</span>
        </button>
      ))}
    </div>
  )
}

/** <dialog> nativo: focus trap, Esc e top layer gestiti dal browser. */
export function Modal({ open, onClose, label, className = '', children }) {
  const ref = useRef(null)
  useEffect(() => {
    const dialog = ref.current
    if (open && !dialog.open) dialog.showModal()
    if (!open && dialog.open) dialog.close()
  }, [open])

  return (
    <dialog
      ref={ref}
      className={`modal ${className}`}
      aria-label={label}
      onCancel={(e) => {
        e.preventDefault()
        onClose()
      }}
      onClick={(e) => e.target === ref.current && onClose()}
    >
      {open && children}
    </dialog>
  )
}

export function ConfirmDialog({ open, title, message, confirmLabel, busy, onConfirm, onCancel }) {
  return (
    <Modal open={open} onClose={busy ? () => {} : onCancel} label={title} className="modal-confirm">
      <div className="modal-body">
        <h2>{title}</h2>
        <p>{message}</p>
        <div className="modal-actions">
          <button type="button" className="btn btn-secondary" onClick={onCancel} disabled={busy}>
            Annulla
          </button>
          <button type="button" className="btn btn-danger" onClick={onConfirm} disabled={busy}>
            {busy && <Spinner />}
            {confirmLabel}
          </button>
        </div>
      </div>
    </Modal>
  )
}

/** URL temporaneo per l'anteprima di un File, liberato quando non serve più. */
export function useObjectUrl(file) {
  const [url, setUrl] = useState(null)
  useEffect(() => {
    if (!file) return setUrl(null)
    const next = URL.createObjectURL(file)
    setUrl(next)
    return () => URL.revokeObjectURL(next)
  }, [file])
  return url
}
