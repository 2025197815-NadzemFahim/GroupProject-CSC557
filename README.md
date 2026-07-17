# Buddy App

A single-user Android app for managing a personal list of friends (contacts),
built for CSC557. Owners log in, then create/read/update/delete/search friends
and view summary charts (male vs female, friends per state).

## Tech stack
- Java + Gradle (Groovy DSL)
- Architecture: MVVM + Repository
- Persistence: Room (on top of SQLite)
- UI: Material 3, View Binding, Navigation Component
- Images: Glide · Charts: MPAndroidChart
- minSdk 24 · targetSdk 34 · Java 17

## Project layout
```
app/src/main/java/com/buddyapp/
├── data/            models, DAOs, Room database, repositories
├── ui/              auth, friends, reports (each with its ViewModel)
├── util/            helpers (hashing, session, validators, constants)
└── BuddyApplication.java
```

## Getting started
1. Open the `BuddyApp` folder in Android Studio (Hedgehog or newer).
2. Let Gradle sync — it will download dependencies and generate the Gradle
   wrapper if needed.
3. Run on an emulator or device (API 24+).

## Team workflow (Git)
- `main` is always working. Branch per task: `feature/<short-name>`.
- Open a pull request; the project leader reviews before merging into `main`.

## Documentation
See `docs/` for the system flowchart, ERD, UML, and data dictionary used in the
project report.
