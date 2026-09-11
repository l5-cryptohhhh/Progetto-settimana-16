/**
 * Segno di Scatto ridisegnato dalla brand board: tre angoli del mirino, l'obiettivo pieno
 * e il punto viola al posto del quarto angolo. Colore dal testo (currentColor).
 */
export function LogoMark({ dot = 'var(--violet)', className = '' }) {
  return (
    <svg className={`logo-mark ${className}`} viewBox="0 0 48 48" aria-hidden="true" focusable="false">
      <g fill="none" stroke="currentColor" strokeWidth="5.2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M5.6 18.5v-4.9a8 8 0 0 1 8-8h4.9" />
        <path d="M5.6 29.5v4.9a8 8 0 0 0 8 8h4.9" />
        <path d="M42.4 29.5v4.9a8 8 0 0 1-8 8h-4.9" />
      </g>
      <circle cx="24" cy="24" r="7.4" fill="currentColor" />
      <circle cx="39.2" cy="8.8" r="4.4" style={{ fill: dot }} />
    </svg>
  )
}

/** Logo con scritta: la dimensione segue il font-size del contenitore. */
export default function Logo({ dot, className = '' }) {
  return (
    <span className={`logo ${className}`}>
      <LogoMark dot={dot} />
      <span className="logo-word">Scatto</span>
    </span>
  )
}
