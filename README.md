# Metro Reservation System

JavaFX desktop application for metro reservation workflows. The system includes user registration/login, reservation screens, admin views, metro management, and a MySQL database schema.

## Highlights

- JavaFX multi-screen desktop interface.
- User sign-up, login, and reservation flow.
- Admin reservation and metro-management workflows.
- MySQL schema for users, metro records, and reservations.

## Tech Stack

- Java
- JavaFX
- MySQL
- JDBC
- NetBeans/Ant project structure

## Repository Structure

```text
src/Metro/          Java controllers and FXML files
database/schema.sql Database schema
nbproject/          NetBeans project metadata
build.xml           Ant build file
build.sh / run.sh   Helper scripts
```

Build artifacts (`build/`, `out/`, `.class`, `.jar`) and local NetBeans private settings are intentionally excluded from the clean copy.

## How to Run

1. Install a JDK with JavaFX support.
2. Create the MySQL database using `database/schema.sql`.
3. Update local database credentials in the project if needed.
4. Run with NetBeans/Ant or the included helper scripts.

## My Role

I worked on the desktop app structure, JavaFX screens/controllers, reservation logic, and database schema.

## Limitations and Next Steps

- Add real screenshots of each JavaFX screen after running locally.
- Move credentials/configuration into environment variables or a local config file.
- Add setup notes for JavaFX dependencies on Windows/macOS/Linux.
