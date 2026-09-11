import { formatBytes } from './format.js'

/*
 * Verifica dei file lato frontend, con le stesse regole del backend (FileTypeDetector).
 * Tutta la logica sta qui: foto dei post e documenti usano solo le costanti e le funzioni esportate.
 */

const MB = 1024 * 1024

const startsWith = (bytes, signature, offset = 0) => signature.every((value, i) => bytes[offset + i] === value)
const ascii = (text) => [...text].map((char) => char.charCodeAt(0))

/** Formati riconosciuti: tipi MIME, estensioni e firma binaria (magic bytes). */
export const FORMATS = {
  JPEG: { mime: ['image/jpeg'], ext: ['jpg', 'jpeg'], matches: (b) => startsWith(b, [0xff, 0xd8, 0xff]) },
  PNG: { mime: ['image/png'], ext: ['png'], matches: (b) => startsWith(b, [0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]) },
  WEBP: { mime: ['image/webp'], ext: ['webp'], matches: (b) => startsWith(b, ascii('RIFF')) && startsWith(b, ascii('WEBP'), 8) },
  PDF: { mime: ['application/pdf'], ext: ['pdf'], matches: (b) => startsWith(b, ascii('%PDF-')) },
  TIFF: {
    mime: ['image/tiff'],
    ext: ['tif', 'tiff'],
    matches: (b) => startsWith(b, [0x49, 0x49, 0x2a, 0x00]) || startsWith(b, [0x4d, 0x4d, 0x00, 0x2a]),
  },
}

export const PHOTO_RULES = { formats: ['JPEG', 'PNG', 'WEBP'], maxSize: 10 * MB }
export const DOCUMENT_RULES = { formats: ['PDF', 'JPEG', 'PNG', 'TIFF'], maxSize: 20 * MB }

/** Numero di foto per origine, come PhotoSource.validatePhotoCount. */
export const PHOTO_LIMITS = { CAMERA: { min: 1, max: 1 }, UPLOAD: { min: 1, max: 10 } }

/** Valore dell'attributo accept: filtra la scelta nel selettore, ma si può aggirare. */
export const acceptFor = (rules) => rules.formats.flatMap((name) => FORMATS[name].mime).join(',')

/** Formato reale letto dai primi 12 byte, oppure null. */
export async function detectFormat(file) {
  const bytes = new Uint8Array(await file.slice(0, 12).arrayBuffer())
  return Object.keys(FORMATS).find((name) => FORMATS[name].matches(bytes)) ?? null
}

/** null se il file è ammesso, altrimenti il messaggio da mostrare all'utente. */
export async function checkFile(file, rules) {
  const allowed = rules.formats.join(', ')
  const formats = rules.formats.map((name) => FORMATS[name])
  const ext = file.name.includes('.') ? file.name.split('.').pop().toLowerCase() : ''

  if (!formats.some((f) => f.ext.includes(ext))) return `${file.name}: formato non supportato (ammessi ${allowed})`
  if (file.type && !formats.some((f) => f.mime.includes(file.type))) {
    return `${file.name}: formato non supportato (ammessi ${allowed})`
  }
  if (file.size === 0) return `${file.name}: il file è vuoto`
  if (file.size > rules.maxSize) {
    return `${file.name}: troppo grande (${formatBytes(file.size)}, massimo ${formatBytes(rules.maxSize)})`
  }
  // Estensione e tipo si falsificano rinominando il file: decide il contenuto
  if (!rules.formats.includes(await detectFormat(file))) {
    return `${file.name}: il contenuto non corrisponde a un formato ammesso (${allowed})`
  }
  return null
}

/** Separa i file ammessi da quelli rifiutati, con un messaggio per ogni rifiuto. */
export async function validateFiles(files, rules) {
  const list = [...files]
  const results = await Promise.all(list.map((file) => checkFile(file, rules)))
  return { valid: list.filter((_, i) => !results[i]), errors: results.filter(Boolean) }
}
