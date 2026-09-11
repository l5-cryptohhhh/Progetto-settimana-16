import assert from 'node:assert/strict'
import { test } from 'node:test'
import { checkFile, detectFormat, DOCUMENT_RULES, PHOTO_RULES, validateFiles } from './fileValidation.js'

const JPEG = [0xff, 0xd8, 0xff, 0xe0]
const PNG = [0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]
const WEBP = [...Buffer.from('RIFF'), 0, 0, 0, 0, ...Buffer.from('WEBP')]
const PDF = [...Buffer.from('%PDF-1.7')]
const TIFF = [0x4d, 0x4d, 0x00, 0x2a]
const TEXT = [...Buffer.from('solo testo')]

const file = (bytes, name, type = '') => new File([new Uint8Array(bytes)], name, { type })

test('riconosce il formato dai magic bytes', async () => {
  assert.equal(await detectFormat(file(JPEG, 'a')), 'JPEG')
  assert.equal(await detectFormat(file(PNG, 'a')), 'PNG')
  assert.equal(await detectFormat(file(WEBP, 'a')), 'WEBP')
  assert.equal(await detectFormat(file(PDF, 'a')), 'PDF')
  assert.equal(await detectFormat(file(TIFF, 'a')), 'TIFF')
  assert.equal(await detectFormat(file(TEXT, 'a')), null)
})

test('accetta foto valide, anche se il contenuto è un altro formato ammesso', async () => {
  assert.equal(await checkFile(file(JPEG, 'mare.jpg', 'image/jpeg'), PHOTO_RULES), null)
  assert.equal(await checkFile(file(WEBP, 'sole.webp', 'image/webp'), PHOTO_RULES), null)
  assert.equal(await checkFile(file(PNG, 'rinominato.jpg', 'image/jpeg'), PHOTO_RULES), null)
})

test('rifiuta estensione, contenuto e dimensione non ammessi', async () => {
  assert.equal(
    await checkFile(file(JPEG, 'foto.heic', 'image/heic'), PHOTO_RULES),
    'foto.heic: formato non supportato (ammessi JPEG, PNG, WEBP)',
  )
  assert.match(await checkFile(file(TEXT, 'finta.jpg', 'image/jpeg'), PHOTO_RULES), /il contenuto non corrisponde/)
  assert.match(await checkFile(file(PDF, 'doc.pdf', 'application/pdf'), PHOTO_RULES), /formato non supportato/)
  assert.match(await checkFile(file([], 'vuota.png', 'image/png'), PHOTO_RULES), /vuoto/)

  const big = new File([new Uint8Array(JPEG), new Uint8Array(10 * 1024 * 1024)], 'grande.jpg', { type: 'image/jpeg' })
  assert.match(await checkFile(big, PHOTO_RULES), /grande\.jpg: troppo grande \(10 MB, massimo 10 MB\)/)
})

test('documenti: PDF e TIFF ammessi, WEBP no', async () => {
  assert.equal(await checkFile(file(PDF, 'contratto.pdf', 'application/pdf'), DOCUMENT_RULES), null)
  assert.equal(await checkFile(file(TIFF, 'scansione.tiff'), DOCUMENT_RULES), null)
  assert.match(await checkFile(file(WEBP, 'x.webp', 'image/webp'), DOCUMENT_RULES), /ammessi PDF, JPEG, PNG, TIFF/)
})

test('validateFiles separa file validi e messaggi di rifiuto', async () => {
  const ok = file(PNG, 'ok.png', 'image/png')
  const { valid, errors } = await validateFiles([ok, file(TEXT, 'no.png', 'image/png')], PHOTO_RULES)
  assert.deepEqual(valid, [ok])
  assert.equal(errors.length, 1)
})
