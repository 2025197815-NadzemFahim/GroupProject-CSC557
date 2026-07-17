# Manual QA checklist

Run on an emulator or device (API 24+) before recording the demo. Tick each box.

## Automated tests (run first)
- [ ] `./gradlew test` — unit tests pass (password hashing, validators, state seed).
- [ ] `./gradlew connectedAndroidTest` — DAO tests pass (CRUD + report queries).

## Authentication
- [ ] First launch shows "Create account" mode.
- [ ] Registering with a username < 3 or password < 6 chars shows an error.
- [ ] Registering with valid details opens the friends list.
- [ ] Log out returns to login; the mode is now "Log in".
- [ ] Logging in with a wrong password shows "Incorrect password".
- [ ] Logging in with correct details opens the list.
- [ ] Kill and relaunch the app while logged in → it opens straight to the list (session persists).

## Create
- [ ] FAB opens the add form (title "Add friend").
- [ ] Saving with a blank name shows a name error.
- [ ] Saving with no gender selected shows a gender error.
- [ ] Saving with an invalid email shows an email error.
- [ ] Picking a photo shows a preview.
- [ ] Saving a valid friend returns to the list; the friend appears with photo, state, and phone.

## Read
- [ ] Tapping a friend opens the detail screen with all fields populated.
- [ ] Missing optional fields show "Not provided".

## Update
- [ ] Edit changes a field (e.g. name) and saves; the list and detail reflect the change.
- [ ] Editing the photo replaces it.

## Delete
- [ ] Delete shows a confirmation dialog.
- [ ] Cancel keeps the friend; Delete removes it and returns to the list.

## Search
- [ ] Typing part of a name filters the list live.
- [ ] Searching by phone or email also matches.
- [ ] A non-matching query shows the "No matches" empty state.
- [ ] Clearing the search restores the full list.

## Reports
- [ ] Reports (overflow menu) opens the report screen.
- [ ] Pie chart shows the correct male vs female split.
- [ ] Bar chart shows one bar per state that has friends, with correct counts.
- [ ] Adding/deleting a friend then reopening Reports updates the charts.

## Database (for the demo)
- [ ] Android Studio → App Inspection → Database Inspector shows `users`, `states`, `friends`.
- [ ] `friends` rows match what's on screen; `states` has 16 seeded rows.
