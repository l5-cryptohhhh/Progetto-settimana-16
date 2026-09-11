---
name: Scatto
description: "Photo social network hung like an exhibition: every post is a work on a matte dark wall, labelled underneath."
colors:
  violet: "#7c3aed"
  violet-hover: "#6d28d9"
  violet-press: "#5b21b6"
  violet-text: "#a78bfa"
  violet-soft: "rgb(124 58 237 / 0.16)"
  violet-line: "rgb(167 139 250 / 0.42)"
  ink: "#0f0f0f"
  plane: "#17171a"
  plane-2: "#1f1f23"
  plane-3: "#2a2a2f"
  line: "rgb(255 255 255 / 0.08)"
  line-strong: "rgb(255 255 255 / 0.14)"
  mist: "#f5f5f7"
  text-2: "#a1a1aa"
  text-3: "#8a8a93"
  white: "#ffffff"
  danger: "#f87171"
  danger-fill: "#dc2626"
  danger-soft: "rgb(248 113 113 / 0.12)"
  success: "#4ade80"
typography:
  display:
    fontFamily: "Poppins, Inter Variable, system-ui, sans-serif"
    fontSize: "clamp(3.5rem, 1.6rem + 3.6vw, 5.5rem)"
    fontWeight: 700
    lineHeight: 1
    letterSpacing: "-0.035em"
  headline:
    fontFamily: "Poppins, Inter Variable, system-ui, sans-serif"
    fontSize: "1.75rem"
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "-0.02em"
  title:
    fontFamily: "Poppins, Inter Variable, system-ui, sans-serif"
    fontSize: "1.25rem"
    fontWeight: 600
    lineHeight: 1.2
    letterSpacing: "-0.02em"
  logo:
    fontFamily: "Poppins, Inter Variable, system-ui, sans-serif"
    fontSize: "1.375rem"
    fontWeight: 700
    lineHeight: 1
    letterSpacing: "-0.03em"
  caption:
    fontFamily: "Poppins, Inter Variable, system-ui, sans-serif"
    fontSize: "1rem"
    fontWeight: 500
    lineHeight: 1.5
    letterSpacing: "-0.005em"
  name:
    fontFamily: "Poppins, Inter Variable, system-ui, sans-serif"
    fontSize: "0.9375rem"
    fontWeight: 600
    letterSpacing: "-0.01em"
  body:
    fontFamily: "Inter Variable, system-ui, -apple-system, Segoe UI, sans-serif"
    fontSize: "1rem"
    fontWeight: 400
    lineHeight: 1.5
  button:
    fontFamily: "Inter Variable, system-ui, -apple-system, Segoe UI, sans-serif"
    fontSize: "0.9375rem"
    fontWeight: 600
    letterSpacing: "-0.005em"
  label:
    fontFamily: "Inter Variable, system-ui, -apple-system, Segoe UI, sans-serif"
    fontSize: "0.875rem"
    fontWeight: 600
  meta:
    fontFamily: "Inter Variable, system-ui, -apple-system, Segoe UI, sans-serif"
    fontSize: "0.8125rem"
    fontWeight: 400
    fontFeature: "\"tnum\""
rounded:
  xs: "8px"
  inner: "10px"
  sm: "12px"
  plate: "14px"
  md: "16px"
  lg: "22px"
  pill: "999px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "16px"
  grid-gap: "24px"
  lg: "32px"
  page: "40px"
  work-gap: "48px"
  rail-w: "248px"
  content-w: "1120px"
components:
  button-primary:
    backgroundColor: "{colors.violet}"
    textColor: "{colors.white}"
    typography: "{typography.button}"
    rounded: "{rounded.sm}"
    padding: "0 20px"
    height: "44px"
  button-primary-hover:
    backgroundColor: "{colors.violet-hover}"
  button-primary-active:
    backgroundColor: "{colors.violet-press}"
  button-primary-large:
    backgroundColor: "{colors.violet}"
    textColor: "{colors.white}"
    rounded: "{rounded.plate}"
    height: "52px"
    width: "100%"
  button-secondary:
    backgroundColor: "{colors.plane-2}"
    textColor: "{colors.mist}"
    typography: "{typography.button}"
    rounded: "{rounded.sm}"
    padding: "0 20px"
    height: "44px"
  button-secondary-hover:
    backgroundColor: "{colors.plane-3}"
  button-ghost:
    backgroundColor: "transparent"
    textColor: "{colors.text-2}"
    rounded: "{rounded.sm}"
    padding: "0 20px"
    height: "44px"
  button-ghost-hover:
    backgroundColor: "{colors.plane-2}"
    textColor: "{colors.mist}"
  button-danger:
    backgroundColor: "{colors.danger-fill}"
    textColor: "{colors.white}"
    rounded: "{rounded.sm}"
    padding: "0 20px"
    height: "44px"
  input:
    backgroundColor: "{colors.plane}"
    textColor: "{colors.mist}"
    typography: "{typography.body}"
    rounded: "{rounded.sm}"
    padding: "12px 16px"
    height: "48px"
  work-media:
    backgroundColor: "{colors.plane}"
    rounded: "{rounded.md}"
  wall-label:
    backgroundColor: "{colors.plane}"
    textColor: "{colors.mist}"
    rounded: "{rounded.plate}"
    padding: "12px 16px 10px"
  rail-link:
    textColor: "{colors.text-2}"
    rounded: "{rounded.sm}"
    padding: "0 14px"
    height: "48px"
  rail-link-active:
    backgroundColor: "{colors.plane}"
    textColor: "{colors.mist}"
  tab-new:
    backgroundColor: "{colors.violet}"
    textColor: "{colors.white}"
    rounded: "{rounded.pill}"
    size: "56px"
  avatar:
    backgroundColor: "{colors.plane-3}"
    textColor: "{colors.mist}"
    rounded: "{rounded.pill}"
  segmented:
    backgroundColor: "{colors.plane}"
    rounded: "{rounded.plate}"
    padding: "4px"
  segmented-thumb:
    backgroundColor: "{colors.plane-3}"
    textColor: "{colors.mist}"
    rounded: "{rounded.inner}"
    height: "42px"
  dropzone:
    backgroundColor: "{colors.plane}"
    textColor: "{colors.text-2}"
    rounded: "{rounded.lg}"
    padding: "48px 32px"
  doc-row:
    backgroundColor: "{colors.plane}"
    textColor: "{colors.mist}"
    rounded: "{rounded.md}"
  badge-pending:
    backgroundColor: "{colors.plane-3}"
    textColor: "{colors.mist}"
    rounded: "{rounded.pill}"
    padding: "0 11px"
    height: "28px"
  badge-processing:
    textColor: "#c4b5fd"
    rounded: "{rounded.pill}"
    padding: "0 11px"
    height: "28px"
  badge-completed:
    backgroundColor: "{colors.plane-3}"
    textColor: "{colors.mist}"
    rounded: "{rounded.pill}"
    padding: "0 11px"
    height: "28px"
  badge-failed:
    backgroundColor: "{colors.danger-soft}"
    textColor: "{colors.danger}"
    rounded: "{rounded.pill}"
    padding: "0 11px"
    height: "28px"
  note-error:
    backgroundColor: "{colors.danger-soft}"
    textColor: "{colors.danger}"
    rounded: "{rounded.md}"
    padding: "12px 16px"
  dialog:
    backgroundColor: "{colors.plane}"
    textColor: "{colors.mist}"
    rounded: "{rounded.lg}"
    width: "440px"
  auth-splash:
    backgroundColor: "{colors.violet}"
    textColor: "{colors.white}"
    padding: "72px 40px"
---

# Design System: Scatto

## Overview

**Creative North Star: "The Exhibition Wall (Parete di mostra)"**

Scatto hangs photographs the way a gallery does. The app is a matte near-black wall. Each post is a work hung on it, and a separate wall label always sits in the same place underneath: author, caption set as the title, technique (camera or upload, with the photo count) and place. The interface stays dark, quiet and flat so the photos can be the colour. Depth comes from stacked planes, never from boxes inside boxes.

The brand gives the wall its instruments. Rounded viewfinder corners from the logo mark frame whatever is in focus and replace dashed borders. The logo's violet dot marks the active item. Diagonal brand stripes mean work in progress. Violet itself is kept for actions and active state. The one place that breaks this restraint is the sign-in screen, a drenched violet brand splash with the blob, the stripes and the framed wordmark. Every other screen goes back to the wall.

Density is calm and generous: one feed column, 48px between works, fields set straight on the wall without a panel behind them. Motion only responds to state, lasts 160 to 320ms, and never choreographs a page load.

**Key Characteristics:**
- Dark theme everywhere; the #F5F5F7 light gray is text, never a surface.
- Four tonal planes and two white hairlines build all hierarchy.
- Violet marks actions and active state only; photos supply every other colour.
- Viewfinder corners, the violet dot and diagonal stripes are the recurring brand instruments.
- Poppins for the voice of the work (titles, wordmark, captions, post authors); Inter for everything read or operated.
- A single 1120px, four-column grid shared by feed, profile and create.

## Colors

A near-black gallery wall with a single violet accent and one red that means error.

### Primary
- **Scatto Violet** (`violet`): the brand board's primary colour. Fills the primary button, the raised mobile "+", the shutter disc, the "Copertina" cover pill, the map pin, the viewfinder dot and the active-item dot. Hover deepens it to `violet-hover`, press to `violet-press`. It also forms the 2px rings around recent authors in the stories row, a commitment pinned by the user.
- **Violet Ink** (`violet-text`): violet for text and icons on dark, where full violet would fail contrast. Used for links, the focus outline, the input caret, the location pin icon in the wall label and the hovered viewfinder corners on dropzones.
- **Violet Veil** (`violet-soft`, `violet-line`): the tinted circle behind dropzone icons, the input focus halo, and the chosen-place strip with its violet hairline.

### Neutral
- **Gallery Black** (`ink`): the wall, the page background, and the colour of the sheet that rises over the splash on mobile. It is also the `theme-color`, and index.html paints it inline before the CSS loads.
- **Label Plane** (`plane`): the first plane above the wall. Wall labels, inputs, dropzones, document rows, dialogs, the active rail link and the empty photo frame all sit on it.
- **Raised Plane** (`plane-2`): secondary buttons, hover on ghost buttons and rows, document type icons, search results, map controls.
- **Top Plane** (`plane-3`): avatars, the segmented thumb, the "In attesa" and "Completato" badges, the scrollbar thumb.
- **Hairline** (`line`, 8% white) and **Strong Hairline** (`line-strong`, 14% white): every border and divider. The strong hairline marks hover, an open document, a muted ring and resting viewfinder corners.
- **Mist** (`mist`): primary text. **Muted** (`text-2`): leads, secondary labels. **Faint** (`text-3`): hints, timestamps, fact names, placeholders.
- **White** (`white`): text and icons on violet or over photos, the camera flash, the dot on the violet "Nuovo post" button.

### Semantic
- **Error Red** (`danger` for text on dark, `danger-fill` for the destructive button, `danger-soft` as a 12% tint): error notes, field errors, invalid input borders, the "Errore" badge, the confirm-delete button and the hover on a thumbnail's remove button.
- **Published Green** (`success`): used once, on the transient "Post pubblicato." confirmation note in the feed (12% tint behind it). It is not a status colour anywhere else.

### Named Rules
**The Violet Means Act Rule.** Violet belongs to actions, the active state, and the brand instruments that signal focus (viewfinder dot, author rings). Never use it as decoration on app screens, and never tint avatars with it. Avatars are neutral `plane-3` monograms.

**The Photos Are the Colour Rule.** Apart from violet and red, the only colour on the wall comes from the photos. Map tiles are inverted and desaturated so the map sits on the dark wall too.

**The One Red Rule.** Red always means an error or a destructive action, and nothing else means that. Success is neutral: "Completato" is a filled `plane-3` badge with a check. It is never green.

## Typography

**Display Font:** Poppins 500/600/700 (fallback Inter Variable, system-ui)
**Body Font:** Inter Variable (fallback system-ui, -apple-system, Segoe UI)

**Character:** Poppins has the round geometry of the wordmark. It speaks for the work: titles, the logo, the caption as a title, post authors. Inter carries everything the user reads closely or operates.

### Hierarchy
- **Display** (Poppins 700, fluid 3.5 to 5.5rem, line-height 1, -0.035em): only the wordmark on the auth splash (3rem on mobile).
- **Headline** (Poppins 700, 1.75rem, 1.2, -0.02em): page `h1`. The auth heading scales up to 2.25rem and the profile name to 2rem (1.5rem at 720px and below).
- **Title** (Poppins 600, 1.25rem, 1.2): section `h2`, dialog titles, empty-state titles. Dropzone headings use Poppins 600 at 1.125rem.
- **Logo** (Poppins 700, 1.375rem, -0.03em): the wordmark in the rail and top bar. The mark is 1.18em.
- **Caption** (Poppins 500, 1rem, 1.5, max 65ch): the post caption, set as the title of the work in its wall label.
- **Name** (Poppins 600, 0.9375rem): the post author in the wall label. Avatar monograms and the profile post count also use Poppins 600.
- **Body** (Inter 400, 1rem, 1.5): leads (max 60ch), dialog text, extracted OCR text (0.9375rem, line-height 1.6).
- **Button** (Inter 600, 0.9375rem, -0.005em): buttons, segmented options, rail links (weight 550).
- **Label** (Inter 600, 0.875rem): field labels and legends, the OCR text header.
- **Meta** (Inter 400, 0.8125rem, tabular numerals): timestamps, file sizes, fact names (`Tecnica`, `Luogo`), hints, the photo counter. Tab labels and the names in the stories row use 0.75rem at weight 500 to 550.

### Named Rules
**The Two Voices Rule.** Poppins is for things that are titled: headings, wordmark, captions, post author names. Everything else is Inter, including the user name in the rail foot, the names in the stories row and document filenames.

**The Tabular Facts Rule.** Every number that can change (times, sizes, counts, coordinates) uses `font-variant-numeric: tabular-nums`, so a changing state never shifts the layout.

## Layout

**One grid for every page.** The content container is `content-w` (1120px) inside a page padding of 16px (mobile, 20px on top) or 40px (960px and up). It is divided into four columns with `grid-gap` (24px):
- **Profile posts grid:** 4 square columns at 24px gaps from 960px. Below that, 3 columns at 12px, tightening to 4px gaps and 6px tile radii at 720px and below.
- **Create post:** from 1180px the media and the details are two halves (columns 1–2 and 3–4). The media half stays sticky 40px from the top. Below that they stack.
- **Feed:** from 1280px the column spans exactly the two centre columns, `(min(100%, 1120px) − 24px) / 2`. That is 544px at a 1440 viewport and at most 548px. Below 1280px the feed is centred at a maximum of 600px.

**Shell.** Below 960px there is a sticky 60px translucent top bar (logo left, sign-out right) and a fixed 68px tab bar: Feed, a raised 56px violet "+" and Profilo, with safe-area insets. From 960px a 248px sticky left rail replaces both. It holds the logo, links with dot markers, a full-width violet "Nuovo post" button, and the user plus the Esci button at the foot above a hairline.

**Rhythm.** Works are 48px apart. The feed stack, profile sections and forms use 24–32px, 20px and 28px gaps (create details). Fields use 8px between label and control. Photos in the feed are 4:5, the camera frame is 4:3, grid tiles are 1:1.

**Breakpoints:** 560px (document rows stack their badge under the name), 720px (compact profile, 240px dropzone), 960px (rail, split auth, 4-column profile grid), 1180px (create halves), 1280px (feed locks to the centre columns). The auth values row hides when the viewport is under 720px tall.

### Named Rules
**The One Grid Rule.** New pages align to the 1120px four-column grid with 24px gaps. A new layout occupies whole columns (one, two, four), never a width of its own.

## Elevation & Depth

Scatto is flat and tonal. Hierarchy comes from four planes stacked on the wall (`ink` → `plane` → `plane-2` → `plane-3`) and from 8% or 14% white hairlines, drawn as inset box-shadows or 1px borders. Photos get a 7% white inset edge so dark images still separate from the wall. Real shadows appear only on things that float above the page, plus the violet glow that lifts the primary action.

### Shadow Vocabulary
- **Float** (`box-shadow: 0 2px 6px rgb(0 0 0 / 0.35), 0 16px 40px -12px rgb(0 0 0 / 0.6)`): map zoom controls and the map hint pill.
- **Lift** (`box-shadow: 0 4px 12px rgb(0 0 0 / 0.45), 0 32px 72px -16px rgb(0 0 0 / 0.8)`): dialogs and the address search results, always paired with an inset 8% hairline.
- **Violet Glow** (`box-shadow: inset 0 1px 0 rgb(255 255 255 / 0.2), 0 8px 22px -8px rgb(124 58 237 / 0.75)`): primary buttons and the mobile "+".
- **Viewfinder lift** (`filter: drop-shadow(0 1px 2px rgb(0 0 0 / 0.45))`): white viewfinder corners over photos only.

### Named Rules
**The Planes Not Boxes Rule.** Never nest a card inside a card. A child region steps to a different plane instead. Extracted OCR text drops down to the wall at the bottom of its open document. In a dialog, the wall label merges with the dialog's plane.

**The Wall Label Rule.** The caption plate is its own `plane` surface hung 10px below the work, never a footer inside the photo's frame.

## Shapes

Corners are soft and graded by size. Small inner controls use 10px. Fields, buttons, rail links and document icons use 12px. Wall labels, grid tiles, the segmented control and large buttons use 14px. Works, notes, document rows and maps use 16px. Dropzones, the camera frame and dialogs use 22px. Circles are for avatars, rings, icon buttons, the shutter and the dots. Badges, counters and the carousel dots are pills. The mobile auth form rises over the splash as a sheet with 28px top corners.

The recurring silhouette is the **viewfinder**: four rounded L brackets (28px arms, 3px stroke, 11px corner radius, 14px inset by default) drawn from the logo mark. The **dot variant** hides the top-right bracket and puts a 12px violet dot there, as in the logo. The brackets scale with their host, from 18px/2px on thumbnails to 52px/5px on the splash. The map pin is a violet teardrop with a white 3px border.

## Components

### Buttons
Confident and tactile: a firm press, a fast colour change.
- **Shape:** gently rounded (12px). Small variant 36px high with 10px corners, large variant 52px high and full width with 14px corners.
- **Primary:** violet fill, white Inter 600 label, 44px minimum height, 20px horizontal padding, Violet Glow. Hover goes to `violet-hover`, press to `violet-press`.
- **Secondary:** `plane-2` fill with an 8% inset hairline; `plane-3` on hover. Used for "Carica altri", "Annulla", "Rifai la foto".
- **Ghost:** transparent with muted text; `plane-2` and mist on hover.
- **Danger:** `danger-fill` with white text (#b91c1c on hover). Only inside delete confirmation dialogs.
- **Glass:** 14% white with a 12px backdrop blur, only over photos or video.
- **States:** every pressable scales to 0.97 on press (icon buttons 0.9) over 160ms. Disabled drops to 50% opacity. Focus shows a 2px `violet-text` outline offset 3px.

### Inputs / Fields
- **Style:** `plane` fill, 1px 8% hairline, 12px corners, 48px minimum height (textarea 112px), `violet-text` caret, faint placeholder. Browser autofill is repainted onto the plane.
- **Hover:** the hairline strengthens to 14%.
- **Focus:** violet border plus a 4px `violet-soft` halo, with no outline.
- **Error:** `danger` border, a `danger-soft` halo on focus, a 0.8125rem field error below. Optional fields carry a faint "facoltativo" marker.

### Navigation
- **Rail link:** 48px, muted Inter 550, 12px corners. Hover and active both move to `plane` with mist text. The active link also shows an 8px violet dot at the right edge, which springs in from 0.3 scale.
- **"Nuovo post" in the rail:** a large primary button. When active its dot is **white**, matching the logo variant on violet.
- **Tab bar:** faint labels; the active tab turns mist and gets a 7px violet dot at the top right of its icon. The raised 56px violet "+" shows a 14px violet dot with a wall-coloured ring at its top-right corner when its page is active.
- **Bars:** top bar and tab bar are 86–90% `ink` with an 18–20px blur. They turn solid when the user asks for reduced transparency.

### Recent authors row
- A horizontal snap-scrolling row of 60px neutral avatars in 2px violet rings, names in 0.75rem Inter below. The first item is "Nuovo post": the user's avatar in a muted 14% ring with a small violet "+" disc.
- Hover scales the ring to 1.05, press to 0.95.

### Work and wall label (signature)
- **Work:** the photo carousel at 4:5 in a 16px `plane` frame with a 7% inset edge. Native scroll-snap. Glass round arrows appear on hover-capable devices only. Pill dots stretch to 16px for the active photo.
- **Wall label:** a separate `plane` plate (14px corners) 10px below the work. Header: 36px avatar, the author in Poppins, a relative time in faint meta. The delete icon button appears only on your own posts. Then the caption in Poppins 500. A hairline introduces a two-column fact list with a 72px faint name column (`Tecnica` with a camera or images icon; `Luogo` as a toggle with a violet-ink pin that opens a 240px map).

### Viewfinder and "messa a fuoco" (signature)
- **Plain viewfinder** (four corners): dropzones, the photo-missing frame, "add photo" tiles, the auth splash. On dropzones it rests at 14% white and turns `violet-text` on hover or drag.
- **Dot viewfinder** (three corners plus the violet dot): feed works, profile grid tiles, the camera frame (where the dot pulses at 1.6s while live) and empty states (at 14% white, dot still violet).
- **Focus reveal:** on feed works and profile tiles the corners start 10px outside and transparent. They close in to their position over 260ms with the exponential ease-out `cubic-bezier(0.22, 1, 0.36, 1)`, with no spring. The dot follows 90ms later, scaling up from 0.3. Triggers: the work crossing the centre 10% band of the viewport (IntersectionObserver `rootMargin: -45% 0px -45% 0px`, feed only), hover on hover-capable devices, and keyboard focus inside the host. Under reduced motion the corners snap in and out with no travel.

### OCR status badges
- **Shape:** 28px pills, 11px padding, Inter 600 at 0.78rem, each with a named label and an icon. Colour is never the only signal.
- **In attesa:** static diagonal stripes (8% white) on `plane-3`, mist text.
- **In elaborazione:** violet stripes (38% and 14%) that scroll at 1s linear, lilac `#c4b5fd` text, a spinning icon.
- **Completato:** a solid neutral `plane-3` pill with a check.
- **Errore:** `danger-soft` pill with `danger` text; the opened row explains the failure in an error note.
- **Document row:** a `plane` row with an 8% inset hairline (14% when open). Fixed columns (44px icon, name, 156px badge, 16px caret), so a status change never moves anything.

### Dropzone
- A 22px `plane` field, 360px tall (240px at 720px and below, a compact 16px-radius row for documents). A plain viewfinder frames it instead of a dashed border. A 64px `violet-soft` circle holds a `violet-text` icon and lifts 3px on hover. While a file is dragged over it, violet brand stripes (14%) fill the plane.

### Segmented control and dialog
- **Segmented:** a `plane` track with a hairline and 14px corners. A `plane-3` thumb with 10px corners slides with the spring over 320ms. The checked option's icon turns `violet-text`.
- **Dialog:** a native `<dialog>` on `plane`, 22px corners, max 440px (560px for an opened post), Lift shadow. It materializes (opacity, 12px rise, 0.97 scale, 6px blur) over 320ms above a 64% black backdrop with a 6px blur.

### Auth splash
- The only drenched violet surface. On desktop it is the left panel (1.1fr against a form column of at least 460px); on mobile it sits on top and the form rises over it as a `ink` sheet. It holds a translucent violet blob at the bottom left, white 24% diagonal stripes cut off by the top-right corner, a large plain viewfinder (45% white) framing the white wordmark with a white dot, the tagline, and the three values with icons along the bottom.

## Do's and Don'ts

### Do:
- **Do** build hierarchy by stepping planes (`ink` → `plane` → `plane-2` → `plane-3`) and 8%/14% white hairlines.
- **Do** frame focusable or droppable areas with viewfinder corners, and use the dot variant for photos and empty states.
- **Do** mark the current item with the violet dot: 7–8px beside nav items, 14px ringed on the round "+", white on a violet fill.
- **Do** show work in progress with the brand's -45° diagonal stripes: static for waiting, moving for processing, violet while dragging files.
- **Do** give every OCR or status phase a name and an icon as well as its colour, and keep status columns fixed-width with tabular numerals.
- **Do** keep state transitions between 160ms and 320ms with `cubic-bezier(0.22, 1, 0.36, 1)`. Use the small spring only for physical changes: the segmented thumb, active-dot scale-in and caret flips.
- **Do** hang the caption on its own `plane` label 10px under the work, with the caption in Poppins 500.
- **Do** align new layouts to the 1120px, four-column, 24px-gap grid.

### Don't:
- **Don't** use violet as decoration on app screens, and don't colour avatars or the "Completato" badge. Neutral means done.
- **Don't** use red for anything except errors and destructive actions, and don't extend green beyond the one-off "Post pubblicato" confirmation.
- **Don't** put a light surface anywhere in the app; `mist` (#F5F5F7) is a text colour.
- **Don't** use dashed borders for upload areas; the viewfinder replaces them.
- **Don't** nest a card inside a card; step down a plane (extracted text drops to the wall).
- **Don't** use blobs, drenched violet fills or the large framed wordmark outside the auth splash.
- **Don't** add a spring or page-load choreography to the "messa a fuoco" reveal; it is a 260ms exponential ease-out tied to focus.
- **Don't** set body copy, form labels or data in Poppins; Poppins is reserved for things that are titled.
