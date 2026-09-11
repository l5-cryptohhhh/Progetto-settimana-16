# Product

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Primary audience: the Epicode exam commission evaluating the week-16 project (D5). They open the app on desktop, register, publish posts with the camera and with uploads, attach a location, upload documents and read the OCR text. The interface must also read as a real consumer social app for everyday people sharing photos from their phone or laptop.

## Product Purpose

Scatto is a photo social network: people publish posts made of one camera shot or 1-10 uploaded photos, optionally tied to the place where they were taken, and keep documents on their profile whose text is extracted with OCR. Success means every exam requirement is immediately findable and works, inside a product that looks finished and credible.

## Positioning

Every post can carry the place of the photos (picked on a map or searched by address), and the camera path is a first-class, single-shot flow distinct from multi-photo upload.

## Operating Context

- Desktop browser at http://localhost:5173 during evaluation; mobile browsers for real use (camera via getUserMedia or the native capture input).
- Backend Spring Boot on http://localhost:8080 with JWT auth; photos served publicly, documents only with the token.
- Maps: Leaflet with OpenStreetMap tiles; geocoding through the backend (Nominatim, about one request per second).

## Capabilities and Constraints

- Auth: register (username 3-30 chars, letters/digits/./_; email; password 8-72), login, logout, protected pages, 401 returns to login.
- Feed: paginated posts, newest first; photo carousel; caption, author, date; location with map; camera vs upload origin; delete own posts with confirmation.
- Create post: CAMERA (exactly 1 photo) or UPLOAD (1-10 photos, previews, remove, visible order); caption up to 2000 chars; optional location by map click (reverse geocoding) or address search (600 ms debounce, 3+ chars); remove location.
- File checks on the client identical to the backend: photos JPEG/PNG/WEBP up to 10 MB; documents PDF/JPEG/PNG/TIFF up to 20 MB; extension, MIME type and magic bytes; clear per-file messages; 413/415 still handled.
- Profile: user data, the user's posts grid, documents section with upload, OCR status badges, 2 s polling, extracted text with copy, authenticated download, delete.
- Not available in the backend, never to be shown as working features: stories, notifications, likes, comments, follows, user search, direct messages.
- UI language: Italian. Frontend stack: React 19 + Vite, react-router, Leaflet, Phosphor icons.

## Brand Commitments

Binding brand identity supplied by the user (brand board "Scatto"):

- Name: Scatto. Tagline: "Condividi ciò che ti ispira."
- Logo: viewfinder mark (four rounded corner brackets around a solid circle, with a violet dot at the top right) plus the "Scatto" wordmark; variants on violet, on dark, on light, monochrome.
- Colors: violet #7C3AED (primary), black #0F0F0F, light gray #F5F5F7, white #FFFFFF.
- Typography: Poppins for titles and logo, Inter for text and descriptions.
- Graphic elements: violet circle, viewfinder corners, violet blob shapes, diagonal violet stripe pattern.
- App UI reference: dark screens, violet primary buttons, a "stories" row of round avatars, photo-first feed, bottom navigation with a violet "+" at the center.
- Values: "Persone vere", "Contenuti autentici", "Connessioni reali".

Decisions confirmed by the user (2026-09-11): dark theme across the whole site; the stories row shows real recent authors from the feed, linking to their profiles; the login/register screen is a brand splash plus the form.

## Evidence on Hand

- Brand board image provided in the conversation (logo, palette, type, graphic elements, app mockups). No logo source files: the mark is redrawn as SVG from the board.
- No real user photos, testimonials or metrics are bundled; content comes only from the backend.

## Product Principles

1. Exam requirements are never hidden behind style: camera vs upload, file checks, location and OCR stay explicit and legible.
2. Photos lead; the interface frames them rather than competing with them.
3. Show only what the backend really does; brand patterns borrowed from the mockup must be backed by real data.
4. Every error from the backend or the file checks is shown in plain Italian with the way to recover.

## Accessibility & Inclusion

WCAG 2.2 AA: text contrast 4.5:1 on the dark theme, visible keyboard focus, 44 px touch targets, labels on icon buttons, reduced-motion respected.
