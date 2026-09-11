const KB = 1024
const MB = KB * 1024

export function formatBytes(bytes) {
  if (bytes < KB) return `${bytes} B`
  const [value, unit] = bytes < MB ? [bytes / KB, 'KB'] : [bytes / MB, 'MB']
  return `${value.toLocaleString('it-IT', { maximumFractionDigits: 1 })} ${unit}`
}

export const formatDate = (iso, options = { day: 'numeric', month: 'short', year: 'numeric' }) =>
  new Intl.DateTimeFormat('it-IT', options).format(new Date(iso))

const relative = new Intl.RelativeTimeFormat('it-IT', { numeric: 'auto' })
const STEPS = [
  [60, 'second'],
  [60, 'minute'],
  [24, 'hour'],
  [7, 'day'],
]

/** "adesso", "5 minuti fa", "ieri"; oltre una settimana la data. */
export function timeAgo(iso) {
  let value = (new Date(iso) - Date.now()) / 1000
  for (const [size, unit] of STEPS) {
    if (Math.abs(value) < size) return unit === 'second' ? 'adesso' : relative.format(Math.round(value), unit)
    value /= size
  }
  return formatDate(iso)
}
