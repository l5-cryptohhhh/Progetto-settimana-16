import { useEffect, useState } from 'react'
import {
  CaretDownIcon,
  CheckCircleIcon,
  CheckIcon,
  CircleNotchIcon,
  ClockIcon,
  CopyIcon,
  DownloadSimpleIcon,
  FileImageIcon,
  FilePdfIcon,
  TrashIcon,
  UploadSimpleIcon,
  WarningCircleIcon,
} from '@phosphor-icons/react'
import { api, downloadFile } from '../lib/api.js'
import { acceptFor, DOCUMENT_RULES, validateFiles } from '../lib/fileValidation.js'
import { formatBytes, formatDate } from '../lib/format.js'
import { ConfirmDialog, ErrorNote, Spinner, Viewfinder } from './ui.jsx'

const POLL_MS = 2000
const STATUS = {
  PENDING: { label: 'In attesa', Icon: ClockIcon },
  PROCESSING: { label: 'In elaborazione', Icon: CircleNotchIcon },
  COMPLETED: { label: 'Completato', Icon: CheckCircleIcon },
  FAILED: { label: 'Errore', Icon: WarningCircleIcon },
}
const isWorking = (doc) => doc.ocrStatus === 'PENDING' || doc.ocrStatus === 'PROCESSING'

export default function Documents() {
  const [docs, setDocs] = useState(null)
  const [error, setError] = useState(null)
  const [uploadError, setUploadError] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [dragging, setDragging] = useState(false)
  const [openId, setOpenId] = useState(null)
  const [toDelete, setToDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    api('/api/users/me/documents').then(setDocs).catch(setError)
  }, [])

  // Polling: finché un documento è PENDING o PROCESSING ne rilegge lo stato ogni 2 secondi
  const workingIds = docs?.filter(isWorking).map((doc) => doc.id).join(',') ?? ''
  useEffect(() => {
    if (!workingIds) return
    const timer = setTimeout(async () => {
      const fresh = await Promise.all(
        workingIds.split(',').map((id) =>
          api(`/api/users/me/documents/${id}`).catch((err) => (err.status === 404 ? { id: Number(id), gone: true } : null)),
        ),
      )
      setDocs((list) =>
        list
          .map((doc) => fresh.find((f) => f?.id === doc.id) ?? doc)
          .filter((doc) => !doc.gone),
      )
    }, POLL_MS)
    return () => clearTimeout(timer)
  }, [workingIds, docs])

  async function upload(fileList) {
    const files = [...fileList]
    if (files.length === 0) return
    if (files.length > 1) return setUploadError(['Carica un documento alla volta.'])
    const { valid, errors } = await validateFiles(files, DOCUMENT_RULES)
    setUploadError(errors)
    if (valid.length === 0) return

    const form = new FormData()
    form.append('file', valid[0])
    setUploading(true)
    try {
      const doc = await api('/api/users/me/documents', { method: 'POST', body: form })
      setDocs((list) => [doc, ...(list ?? [])])
      setOpenId(doc.id)
    } catch (err) {
      setUploadError(err)
    } finally {
      setUploading(false)
    }
  }

  async function confirmDelete() {
    setDeleting(true)
    try {
      await api(`/api/users/me/documents/${toDelete.id}`, { method: 'DELETE' })
      setDocs((list) => list.filter((doc) => doc.id !== toDelete.id))
    } catch (err) {
      setError(err)
    } finally {
      setDeleting(false)
      setToDelete(null)
    }
  }

  return (
    <section className="docs" aria-labelledby="docs-title">
      <div>
        <h2 id="docs-title">Documenti</h2>
        <p className="docs-lead">Carica un PDF o un’immagine: il testo viene estratto automaticamente con l’OCR.</p>
      </div>

      <label
        className={`dropzone dropzone-compact ${dragging ? 'is-dragging' : ''}`}
        onDragOver={(e) => {
          e.preventDefault()
          setDragging(true)
        }}
        onDragLeave={() => setDragging(false)}
        onDrop={(e) => {
          e.preventDefault()
          setDragging(false)
          upload(e.dataTransfer.files)
        }}
      >
        <input
          type="file"
          accept={acceptFor(DOCUMENT_RULES)}
          className="sr-only"
          disabled={uploading}
          onChange={(e) => {
            upload(e.target.files)
            e.target.value = ''
          }}
        />
        <Viewfinder />
        <span className="dropzone-icon">{uploading ? <Spinner /> : <UploadSimpleIcon size={24} aria-hidden="true" />}</span>
        <span className="dropzone-text">
          <strong>{uploading ? 'Caricamento in corso…' : 'Carica un documento'}</strong>
          <span>PDF, JPEG, PNG o TIFF fino a 20 MB</span>
        </span>
      </label>
      <ErrorNote error={uploadError} />
      <ErrorNote error={error} />

      {docs === null && !error && (
        <div className="doc-list" aria-hidden="true">
          <span className="skeleton skeleton-doc" />
          <span className="skeleton skeleton-doc" />
        </div>
      )}
      {docs?.length === 0 && <p className="docs-empty">Non hai ancora caricato documenti.</p>}
      {docs?.length > 0 && (
        <ul className="doc-list">
          {docs.map((doc) => (
            <DocumentRow
              key={doc.id}
              doc={doc}
              open={openId === doc.id}
              onToggle={() => setOpenId(openId === doc.id ? null : doc.id)}
              onDelete={() => setToDelete(doc)}
            />
          ))}
        </ul>
      )}

      <ConfirmDialog
        open={Boolean(toDelete)}
        title="Eliminare il documento?"
        message={`${toDelete?.originalFilename} e il testo estratto verranno eliminati definitivamente.`}
        confirmLabel="Elimina"
        busy={deleting}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </section>
  )
}

function DocumentRow({ doc, open, onToggle, onDelete }) {
  const [copied, setCopied] = useState(false)
  const [error, setError] = useState(null)
  const { label, Icon } = STATUS[doc.ocrStatus]
  const TypeIcon = doc.contentType === 'application/pdf' ? FilePdfIcon : FileImageIcon
  const text = doc.extractedText?.trim()

  async function copy() {
    try {
      await navigator.clipboard.writeText(doc.extractedText)
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    } catch {
      setError(new Error('Copia non riuscita: seleziona il testo e copialo a mano.'))
    }
  }

  async function download() {
    setError(null)
    try {
      await downloadFile(`/api/users/me/documents/${doc.id}/file`, doc.originalFilename)
    } catch (err) {
      setError(err)
    }
  }

  return (
    <li className={`doc ${open ? 'is-open' : ''}`}>
      <div className="doc-row">
        <button type="button" className="doc-main" aria-expanded={open} aria-controls={`doc-${doc.id}`} onClick={onToggle}>
          <span className="doc-icon">
            <TypeIcon size={24} aria-hidden="true" />
          </span>
          <span className="doc-text">
            <strong>{doc.originalFilename}</strong>
            <span>
              {formatBytes(doc.size)}, {formatDate(doc.uploadedAt)}
            </span>
          </span>
          <span className={`badge badge-${doc.ocrStatus.toLowerCase()}`}>
            <Icon size={14} weight="bold" aria-hidden="true" />
            {label}
          </span>
          <CaretDownIcon size={16} className="doc-caret" aria-hidden="true" />
        </button>
        <button type="button" className="icon-btn" onClick={download} aria-label={`Scarica ${doc.originalFilename}`}>
          <DownloadSimpleIcon size={20} />
        </button>
        <button type="button" className="icon-btn" onClick={onDelete} aria-label={`Elimina ${doc.originalFilename}`}>
          <TrashIcon size={20} />
        </button>
      </div>

      {open && (
        <div className="doc-detail" id={`doc-${doc.id}`}>
          {isWorking(doc) && (
            <div className="ocr-wait" role="status">
              <span className="skeleton" />
              <span className="skeleton" />
              <span className="skeleton" />
              <p>{doc.ocrStatus === 'PENDING' ? 'Il documento è in coda per l’OCR…' : 'Estrazione del testo in corso…'}</p>
            </div>
          )}
          {doc.ocrStatus === 'FAILED' && <ErrorNote error={new Error(doc.ocrError || 'Elaborazione OCR non riuscita.')} />}
          {doc.ocrStatus === 'COMPLETED' &&
            (text ? (
              <>
                <div className="ocr-head">
                  <span>Testo estratto</span>
                  <button type="button" className="btn btn-secondary btn-small" onClick={copy}>
                    {copied ? <CheckIcon size={16} weight="bold" aria-hidden="true" /> : <CopyIcon size={16} aria-hidden="true" />}
                    {copied ? 'Copiato' : 'Copia'}
                  </button>
                </div>
                <pre className="ocr-text" tabIndex={0}>
                  {doc.extractedText}
                </pre>
              </>
            ) : (
              <p className="docs-empty">Nessun testo riconosciuto nel documento.</p>
            ))}
        </div>
      )}
      {error && (
        <div className="doc-detail">
          <ErrorNote error={error} />
        </div>
      )}
    </li>
  )
}
