Lettering
Exhibit titles and major headings
Cormorant Garamond SemiBold
It has the literary, archival feeling of exhibition labels without becoming theatrical. It also supports Spanish accents properly.
Use it for:
Museum of Minor Mysteries
exhibit names
puzzle-stage headings
completion and finale headings
Avoid using it for buttons or long instructions.
Body, controls, and feedback
Source Sans 3
It is clean, highly readable on a phone, and contrasts nicely with the serif headings.
Use it for:
instructions
clues and feedback
dropdowns
buttons
dialogs
attempt counters
accessibility text
A good hierarchy would be:
Museum title: Cormorant Garamond, 34–38sp, SemiBold
Exhibit title: Cormorant Garamond, 28–32sp, SemiBold
Section heading: Cormorant Garamond, 22–24sp, Medium
Body: Source Sans 3, 17–18sp
Buttons: Source Sans 3, 16sp, SemiBold
Secondary text: Source Sans 3, 14–15sp
Do not use all-caps everywhere. A little letter spacing can work for tiny labels such as CURRENT EXHIBIT, but full titles should remain normally capitalized.
Light theme: “Day Gallery”
Avoid pure white. A warm ivory will make the artwork feel framed rather than pasted onto a blank app.
App background       #F3EEE3   warm museum plaster
Main surface         #FBF8F1   gallery card
Raised surface       #E9E1D3   secondary panel
Primary text         #292722   charcoal ink
Secondary text       #625D54   warm gray
Primary accent       #315E5A   muted museum teal
Primary pressed      #244844
Secondary accent     #9A7044   aged brass
Outline              #BEB4A4
Destructive          #843E42   muted burgundy
Success              #48664B   restrained green
Buttons
Primary action — Resume, Continue, Check, Combine:
background: muted teal
text: warm ivory
slightly rounded corners, but not bubbly
modest elevation or border rather than heavy shadow
Secondary action — Return to entrance, Revisit:
transparent or warm surface
charcoal text
visible teal or gray outline
Destructive action — Restart museum:
do not make it visually dominant
use a burgundy outline or text button
reserve solid burgundy for the confirmation dialog action
Dark theme: “Museum After Hours”
Do not use pure black. A blue-charcoal background will keep the paintings rich and preserve the museum atmosphere.
App background       #181C1D   blue-charcoal
Main surface         #232829   dark gallery wall
Raised surface       #2E3535
Primary text         #F2EBDD   warm parchment
Secondary text       #C2B9AA
Primary accent       #78AAA4   softened teal
Primary pressed      #96BDB8
Secondary accent     #C49A64   warm brass
Outline              #555E5D
Destructive          #D58A8B
Success              #91B093
The dark theme should make the artwork feel like it is being viewed in a quiet gallery at night. Keep image containers slightly lighter than the background and use a thin warm-gray frame around them.
Shared visual treatment
I would give most screens:
a subtle warm or charcoal background;
the artwork inside a simple framed surface;
a narrow border rather than a large drop shadow;
puzzle controls grouped on a distinct but quiet panel;
generous spacing around titles;
one consistent accent color for primary progress actions.
For example:
Background
  └── Exhibit title
  └── Framed artwork
  └── Puzzle panel
       ├── instruction
       ├── controls
       ├── feedback
       └── navigation actions
The entrance can feel slightly grander than the puzzle rooms: larger serif title, more breathing room, and perhaps an aged-brass divider.
The Kubernetes City finale can shift subtly toward the teal and brass accents, suggesting that the player has stepped beyond the older museum into another world.
Does the dark theme earn its keep?
Yes, because:
the artwork is atmospheric enough to benefit from it;
the finale image likely works especially well against a dark surround;
it gives the museum an appealing “after-hours visit” identity;
Compose already supports themed color tokens cleanly.
But build it as a real theme using semantic colors—not screen-by-screen hardcoded colors. Codex should define roles such as museumBackground, exhibitSurface, frameBorder, primaryAction, and feedbackSurface, then provide light and dark values.
My strongest recommendation is:
Cormorant Garamond + Source Sans 3, warm ivory daytime museum, blue-charcoal nighttime museum, muted teal primary actions, aged-brass details.

That will feel atmospheric without sacrificing readability or turning the interface into a Victorian séance invitation.