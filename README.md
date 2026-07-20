# Hotel Management System

A desktop application for managing hotel operations with role-based access control, room management, booking workflows, payments, customer records, staff tools, and administrative reporting.

## Overview

This project implements a hotel management system with a graphical user interface built using Java Swing. The application uses SQLite for persistent storage and separates the system into three main roles: **Admin**, **Staff**, and **Customer**. Each role has its own dashboard and permissions for managing hotel data and daily operations.

## link Youtube : https://youtu.be/Fwb66Y68M2w
## Features

### User Management
- Role-based authentication for Admin, Staff, and Customer users
- Secure password hashing using SHA-256
- Customer signup from the login screen
- User account creation and role-based dashboard routing
- Staff and admin account support

### Room Management
- View hotel rooms with room number, type, price, and status
- Add, update, and delete rooms
- Track room availability and occupancy status
- Browse available rooms from the customer dashboard
- Staff room-status monitoring

### Booking Management
- Create room bookings with check-in and check-out dates
- View booking history
- Track booking status
- Manage pending bookings
- Approve or reject booking requests
- Check in and check out guests

### Customer Features
- Sign up and log in as a customer
- Browse available rooms
- Create bookings
- View booking history
- Make and review payments
- Access a customer dashboard with booking information

### Staff Dashboard
- View pending bookings
- Manage check-in and check-out operations
- Monitor room status
- Support daily front-desk hotel workflows

### Admin Dashboard
- Manage rooms
- Manage bookings
- Manage customers
- Manage staff records
- Manage payments
- View income and operational reports
- Access centralized hotel administration tools

### Payment & Reporting
- Store payment records linked to bookings
- Track payment amount, method, date, and status
- Generate occupancy, revenue, and booking reports
- Export report data through the report service

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java |
| GUI Framework | Swing |
| Database | SQLite |
| Database Driver | SQLite JDBC |
| Build Tool | Standard Java Compilation |
| IDE Support | IntelliJ IDEA project files |

## Installation

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- SQLite JDBC driver
- DB Browser for SQLite, optional for database inspection

The required JDBC libraries are already included in the `lib/` folder:

- `sqlite-jdbc.jar`
- `slf4j-api.jar`
- `slf4j-simple.jar`

### Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/hotelManagementSystem.git
   cd hotelManagementSystem
   ```

2. Compile the project:

   On Windows PowerShell:

   ```powershell
   $sources = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
   javac -encoding UTF-8 -cp "lib/*" -d out $sources
   ```

   On macOS/Linux:

   ```bash
   find src -name "*.java" | xargs javac -encoding UTF-8 -cp "lib/*" -d out
   ```

3. Initialize seed data:

   ```bash
   java -cp "out;lib/*" hotel.util.SeedData
   ```

   On macOS/Linux, use `:` instead of `;`:

   ```bash
   java -cp "out:lib/*" hotel.util.SeedData
   ```

4. Run the GUI application:

   On Windows:

   ```bash
   java -cp "out;lib/*" hotel.main.MainGUI
   ```

   On macOS/Linux:

   ```bash
   java -cp "out:lib/*" hotel.main.MainGUI
   ```

## How to Run

### GUI Version

```bash
java -cp "out;lib/*" hotel.main.MainGUI
```

The application opens the login screen for Admin, Staff, and Customer access.

### Console Version

```bash
java -cp "out;lib/*" hotel.main.Main
```

The console version provides menu-based access to rooms, bookings, payments, customers, staff, users, and reports.

## Default Test Credentials

After running `hotel.util.SeedData`, you can use:

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Staff | staff1 | admin123 |
| Staff | staff2 | admin123 |

The console application also creates default users if missing:

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Admin | admin2 | admin123 |
| Admin | admin3 | admin123 |
| Staff | staff1 | staff123 |

Customer accounts can be created from the Sign up screen.

## Usage

### For Customers

1. Open the application and create a customer account.
2. Log in with your customer credentials.
3. Browse available rooms.
4. Select a room and create a booking.
5. View booking history.
6. Complete payment from the customer payment screen.

### For Staff

1. Log in with staff credentials.
2. Review pending bookings.
3. Approve or reject booking requests.
4. Manage guest check-in and check-out.
5. Monitor current room status.

### For Admins

1. Log in with admin credentials.
2. Manage rooms, bookings, customers, staff, and payments.
3. Review hotel income and operational reports.
4. Add, update, or remove hotel records as needed.

## Project Structure

```text
hotelManagementSystem/
├── database/
│   └── schema.sql                         # SQLite database schema
├── docs/                                  # Project notes and documentation
├── lib/                                   # External JDBC and logging libraries
├── src/
│   └── hotel/
│       ├── config/
│       │   ├── DBConnection.java          # SQLite database connection
│       │   └── DBInitializer.java         # Runs database schema setup
│       ├── dao/
│       │   ├── BookingDAO.java            # Booking database operations
│       │   ├── CustomerDAO.java           # Customer database operations
│       │   ├── PaymentDAO.java            # Payment database operations
│       │   ├── RoomDAO.java               # Room database operations
│       │   ├── StaffDAO.java              # Staff database operations
│       │   └── UserDAO.java               # User database operations
│       ├── main/
│       │   ├── Main.java                  # Console application entry point
│       │   └── MainGUI.java               # Swing GUI entry point
│       ├── model/
│       │   ├── Booking.java               # Booking entity
│       │   ├── Customer.java              # Customer entity
│       │   ├── Payment.java               # Payment entity
│       │   ├── Room.java                  # Room entity
│       │   ├── Staff.java                 # Staff entity
│       │   └── User.java                  # User entity
│       ├── service/
│       │   ├── AuthService.java           # Authentication logic
│       │   ├── BookingService.java        # Booking business logic
│       │   ├── PaymentService.java        # Payment business logic
│       │   ├── ReportService.java         # Report generation
│       │   └── RoomService.java           # Room business logic
│       ├── ui/
│       │   ├── admin/                     # Admin dashboard and panels
│       │   ├── common/                    # Shared login/signup UI
│       │   ├── customer/                  # Customer dashboard and panels
│       │   └── staff/                     # Staff dashboard and panels
│       └── util/
│           ├── DateUtil.java              # Date helpers
│           ├── PasswordHasher.java        # SHA-256 password hashing
│           ├── ReportExporter.java        # Report export helper
│           └── SeedData.java              # Default data seeding
├── hotel.db                               # SQLite database file
├── hotel.sqbpro                           # DB Browser for SQLite project file
└── README.md
```

## Database Schema

The application uses SQLite with the following main tables:

- `users` - Stores login credentials, roles, and account creation dates
- `rooms` - Stores room number, type, price per night, and room status
- `customers` - Stores customer contact and profile information
- `staff` - Stores staff name, position, salary, and linked user account
- `bookings` - Stores room reservations, dates, total price, and booking status
- `payments` - Stores booking payment details, method, date, and status

## Key Classes

| Class | Purpose |
|-------|---------|
| `MainGUI` | Starts the Java Swing desktop application |
| `Main` | Starts the console-based version of the system |
| `DBConnection` | Manages the SQLite JDBC connection |
| `DBInitializer` | Creates database tables from `schema.sql` |
| `AuthService` | Handles user login and current-user state |
| `PasswordHasher` | Hashes and verifies passwords using SHA-256 |
| `RoomDAO` | Handles room CRUD operations |
| `BookingDAO` | Handles booking CRUD operations |
| `PaymentDAO` | Handles payment CRUD operations |
| `ReportService` | Generates occupancy, revenue, and booking reports |

## Building from Source

### Windows PowerShell

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out
$sources = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -cp "lib/*" -d out $sources
java -cp "out;lib/*" hotel.main.MainGUI
```

### macOS/Linux

```bash
rm -rf out
mkdir -p out
find src -name "*.java" | xargs javac -encoding UTF-8 -cp "lib/*" -d out
java -cp "out:lib/*" hotel.main.MainGUI
```

## Troubleshooting

### SQLite JDBC Driver Not Found

- Make sure `lib/sqlite-jdbc.jar` exists.
- Run the app with `lib/*` included in the classpath.

### Database Tables Missing

- Run the console application once, or call `hotel.config.DBInitializer`.
- Confirm that `database/schema.sql` exists.
- Make sure the project is run from the repository root so the relative schema path works.

### Login Fails

- Run `hotel.util.SeedData` to insert default users.
- Confirm the username and password match the seeded credentials.
- Customer passwords must be at least 8 characters when signing up from the GUI.

### GUI Does Not Start

- Confirm Java is installed with `java -version`.
- Recompile all files into the `out/` directory.
- Ensure the command includes both `out` and `lib/*` in the classpath.

## Contributing

Contributions are welcome. To contribute:

1. Fork the repository.
2. Create a feature branch:

   ```bash
   git checkout -b feature/your-feature-name
   ```

3. Commit your changes:

   ```bash
   git commit -m "Add your feature"
   ```

4. Push the branch:

   ```bash
   git push origin feature/your-feature-name
   ```

5. Open a pull request.

## License

This project is currently provided for academic use. Add a license file if you want to publish it as an open-source project.

## Author

**HAI Monyoudom**

**Pong MengHeang**

**Hen Chhordavattey**

**Chi LayHorng**

Project: Hotel Management System

---

**Last Updated:** July 2026  
**Version:** 1.0
