# Entity Relationship Diagram

Paste the block below into any Mermaid renderer (or the Mermaid tab in
Android Studio / VS Code) to regenerate the ERD image for the report.

```mermaid
erDiagram
    STATES ||--o{ FRIENDS : "has many"
    USERS {
        int user_id PK
        string username "NOT NULL, UNIQUE"
        string password_hash "NOT NULL"
        string salt "NOT NULL"
        long created_at "NOT NULL"
    }
    STATES {
        int state_id PK
        string name "NOT NULL, UNIQUE"
    }
    FRIENDS {
        int friend_id PK
        string name "NOT NULL"
        string gender "NOT NULL"
        string phone
        string email
        string photo_uri
        string address_line1
        string address_line2
        string postcode
        string city
        int state_id FK
        long created_at "NOT NULL"
        long updated_at "NOT NULL"
    }
```

`USERS` stands alone (authentication). `STATES` is a seeded lookup table so the
"friends per state" report is normalized and consistent. One state has many
friends (`state_id` foreign key on `FRIENDS`).
