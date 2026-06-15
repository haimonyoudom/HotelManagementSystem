# Hotel Management System Deployment Workflow

## Overview

The goal is to:

1. Finish the Hotel Management System.
2. Migrate the database from SQLite to MySQL.
3. Test the application thoroughly.
4. Export the project as a JAR file.
5. Convert the JAR into an EXE installer.
6. Deploy the application to multiple computers.

---

# Phase 1: Complete the Application

Before changing the database, ensure all features are working correctly.

### Checklist

* Login System
* Room Management
* Booking Management
* Customer Management
* Payment Management
* Reports and Statistics
* User Interface
* Error Handling

### Workflow

```text
Complete Features
    ↓
Fix Bugs
    ↓
Test Everything
```

---

# Phase 2: Migrate SQLite to MySQL

## Current Database

```text
SQLite
    ↓
hotel.db
```

## Target Database

```text
MySQL Server
    ↓
hotel_db
```

## Tasks

### 1. Analyze Current Database Layer

Review:

* Database connection class
* DAO classes
* SQL queries
* Table creation scripts

### 2. Install MySQL

Install:

* MySQL Server
* MySQL Workbench (Optional)

### 3. Create Database

Example:

```sql
CREATE DATABASE hotel_db;
```

### 4. Create Tables

Recreate all SQLite tables in MySQL.

### 5. Update JDBC Driver

Remove SQLite dependency.

Add MySQL Connector/J.

### 6. Change Connection URL

Before:

```java
jdbc:sqlite:hotel.db
```

After:

```java
jdbc:mysql://localhost:3306/hotel_db
```

### 7. Check SQLite-Specific Features

Review:

* AUTOINCREMENT
* INTEGER PRIMARY KEY
* PRAGMA statements
* SQLite date/time functions
* last_insert_rowid()

### Workflow

```text
Analyze Database
        ↓
Install MySQL
        ↓
Create Database
        ↓
Create Tables
        ↓
Update JDBC Driver
        ↓
Update Connection URL
        ↓
Test DAOs
```

---

# Phase 3: Multi-Computer Testing

## Architecture

```text
PC 1
   │
   ├──► MySQL Server
   │
PC 2
```

### Verify

* Multiple clients can connect
* Data synchronization works
* Insert, update, and delete operations work correctly

---

# Phase 4: Export JAR File

## NetBeans

```text
Run
    ↓
Clean and Build Project
    ↓
dist/
    ↓
HotelManagement.jar
```

## Test JAR

```bash
java -jar HotelManagement.jar
```

Expected Result:

* Application launches successfully
* Database connection works
* All features function correctly

---

# Phase 5: Create EXE Installer

## Option 1: jpackage (Recommended)

```text
HotelManagement.jar
        ↓
jpackage
        ↓
HotelManagement.exe
```

## Option 2: Launch4j

```text
HotelManagement.jar
        ↓
Launch4j
        ↓
HotelManagement.exe
```

---

# Phase 6: Deployment

## Single Computer Setup

```text
Computer A
 ├─ HotelManagement.exe
 └─ MySQL Server
```

## Multi-Computer Setup

```text
Server PC
 └─ MySQL Server

Reception PC
 └─ HotelManagement.exe

Manager PC
 └─ HotelManagement.exe

Admin PC
 └─ HotelManagement.exe
```

Connection Example:

```java
jdbc:mysql://SERVER_IP:3306/hotel_db
```

---

# Recommended Order

```text
1. Finish Project
2. Backup Project
3. Switch SQLite → MySQL
4. Test Database Thoroughly
5. Build JAR
6. Test JAR
7. Create EXE
8. Test EXE on Another Computer
9. Deploy MySQL Server
10. Distribute EXE to Users
```

---

# Important Note

Do not migrate to MySQL and create the EXE at the same time.

Recommended approach:

```text
Finish Application
        ↓
Migrate to MySQL
        ↓
Test Everything
        ↓
Build JAR
        ↓
Create EXE
        ↓
Deploy
```

This reduces troubleshooting complexity and makes it easier to identify problems during deployment.
