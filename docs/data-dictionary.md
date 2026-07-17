# Data dictionary

Database: `buddy.db` (SQLite via Room). Three tables.

## users
Owner account for authentication. Normally holds exactly one row.

| Field | Type | Key / Constraint | Description |
|---|---|---|---|
| user_id | INTEGER | PK, AUTOINCREMENT | Unique account id |
| username | TEXT | NOT NULL, UNIQUE | Login name |
| password_hash | TEXT | NOT NULL | Salted, iterated SHA-256 hash of the password |
| salt | TEXT | NOT NULL | Random per-account salt (hex) |
| created_at | INTEGER | NOT NULL | Creation time (epoch millis) |

## states
Seeded lookup table of Malaysian states and federal territories (16 rows).

| Field | Type | Key / Constraint | Description |
|---|---|---|---|
| state_id | INTEGER | PK, AUTOINCREMENT | Unique state id |
| name | TEXT | NOT NULL, UNIQUE | State name |

## friends
The main entity — one row per friend/contact.

| Field | Type | Key / Constraint | Description |
|---|---|---|---|
| friend_id | INTEGER | PK, AUTOINCREMENT | Unique friend id |
| name | TEXT | NOT NULL | Friend's full name |
| gender | TEXT | NOT NULL | "Male" or "Female" (enforced in the app) |
| phone | TEXT | nullable | Contact number |
| email | TEXT | nullable | Email address |
| photo_uri | TEXT | nullable | File path to the photo in internal storage |
| address_line1 | TEXT | nullable | House / lot / floor & building |
| address_line2 | TEXT | nullable | Number, street / district |
| postcode | TEXT | nullable | Postcode |
| city | TEXT | nullable | City / locality |
| state_id | INTEGER | FK → states(state_id), ON DELETE RESTRICT | Friend's state |
| created_at | INTEGER | NOT NULL | Creation time (epoch millis) |
| updated_at | INTEGER | NOT NULL | Last-updated time (epoch millis) |

## Relationships
- `states (1) ──< friends (many)` via `friends.state_id`.
- `users` is independent (authentication only).
