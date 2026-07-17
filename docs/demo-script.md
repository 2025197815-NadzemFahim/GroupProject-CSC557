# Demo video script (8–10 minutes, MP4)

Requirements from the brief: every member must speak/present, and you must show
the SQLite database (tables). Use a screen recorder; keep the file small but
clear. Test the app once before recording (see `docs/qa-checklist.md`).

Suggested running order (assign a member to each segment):

1. **Intro (0:00–1:00)** — team introduces itself; state the project title and
   problem statement in one or two sentences.
2. **Register & login (1:00–2:00)** — first-run "Create account", then log out
   and log back in. Mention passwords are stored hashed + salted, not in plain
   text.
3. **Create (2:00–3:30)** — add a friend: pick a photo, fill name, gender, phone,
   email, the four address lines, and pick a state. Show validation by leaving
   the name blank once.
4. **Read (3:30–4:15)** — show the friend in the list, open its detail screen.
5. **Update (4:15–5:00)** — edit a field (and/or the photo); show the list update.
6. **Delete (5:00–5:30)** — delete a friend with the confirmation dialog.
7. **Search (5:30–6:15)** — search by name, then by phone/email; show a no-match
   case; clear it.
8. **Reports (6:15–7:15)** — open Reports; explain the pie (gender) and bar
   (per state) charts; add or delete a friend and reopen to show they update.
9. **Database (7:15–8:15)** — in Android Studio, open App Inspection → Database
   Inspector; show the `users`, `states`, and `friends` tables and point out the
   hashed password and the friend rows.
10. **Wrap-up (8:15–end)** — brief conclusion and future improvements.

Tips: rehearse once; keep transitions quick; make sure the emulator text is large
enough to read in the recording.
