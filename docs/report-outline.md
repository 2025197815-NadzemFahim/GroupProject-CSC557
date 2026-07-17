# Report outline (8–10 pages, excluding cover)

Maps each required report section to content already produced in this project.
Font: Times New Roman / Arial 11pt, single spacing, A4, single-sided. Cover page:
course, group, project title, prepared by, prepared for.

1. **Title** — Buddy Application.
2. **Problem statement** — People keep friends' contact details scattered across
   chat apps, paper, and memory; there is no single private place to store a
   friend's photo, contact info, and full address and to see simple summaries.
3. **Objectives** (2–3):
   - Provide a secure, single-user mobile app to store and manage friends' details.
   - Support full CRUD + search over friend records stored locally in SQLite.
   - Provide graphical summaries (friends by gender, friends by state).
4. **Scope / limitations** — single user; one device; local storage only (no cloud
   sync or multi-user); Android only.
5. **Members' details** — id, name, group, programme (fill in).
6. **Roles and responsibilities** — assign per member (e.g. DB/repository, auth,
   friends UI, reports, testing/report). The project leader coordinates.
7. **Flowchart (system design)** — see `docs/user-flow.md` (render the Mermaid).
8. **User interface design** — screenshots of login, list, add/edit, detail, reports.
9. **Database design (ERD)** — see `docs/erd.md` (render the Mermaid).
10. **Data dictionary** — see `docs/data-dictionary.md`.
11. **Special methods / techniques** (not taught in class) — pick and explain:
    - Room ORM over SQLite (compile-time-checked queries, DAOs).
    - MVVM + LiveData reactive UI.
    - Salted, iterated SHA-256 password hashing.
    - Third-party charting with MPAndroidChart.
12. **Conclusion / future improvements** — cloud sync, multi-user, export, backup,
    biometric login, dark theme, more report types.

## Architecture summary (for the report body)
MVVM + Repository. UI (Activity/Fragments) → ViewModel (LiveData) → Repository
(single source of truth) → Room DAO → SQLite. Design patterns used: Repository,
Observer (LiveData), Singleton (AppDatabase), DAO, Factory (ViewModelProvider),
Adapter (RecyclerView).
