# SQLite Database Guide (hotel.db)

This guide helps you use SQLite in Ubuntu for your **hotelManagementSystem** project.

---

## 1. Open the Database

Go to your project folder:

```bash
cd /media/monyoudom/school/code/year2/sem2/hotelManagementSystem
```

Open database:

```bash
sqlite3 hotel.db
```

---

## 2. Basic SQLite Commands

### Show all tables

```
.tables
```

### Count number of tables

```sql
SELECT count(*) FROM sqlite_master WHERE type='table';
```

### Show database info

```
.databases
```

---

## 3. View Table Structure (Fields)

```sql
.schema table_name
```

Example:

```sql
.schema bookings
```

OR (more detailed):

```sql
PRAGMA table_info(table_name);
```

---

## 4. View Data

### Show all data

```sql
SELECT * FROM table_name;
```

Example:

```sql
SELECT * FROM bookings;
```

---

## 5. Search Data (IMPORTANT)

### Find specific data

```sql
SELECT * FROM table_name WHERE column = value;
```

Example:

```sql
SELECT * FROM bookings WHERE id = 1;
```

### Search text

```sql
SELECT * FROM customers WHERE name LIKE '%John%';
```

### Multiple conditions

```sql
SELECT * FROM bookings WHERE status = 'PENDING' AND room_id = 1;
```

---

## 6. Make Output Readable

Run this once:

```
.headers on
.mode column
```

---

## 7. Insert Data

```sql
INSERT INTO bookings (customer_id, room_id, check_in, check_out, status, deposit_percent, created_at)
VALUES (1, 1, '2026-04-23 14:00:00', '2026-04-25 12:00:00', 'PENDING', 20, '2026-04-23 13:00:00');
```

---

## 8. Update Data

```sql
UPDATE bookings
SET status = 'APPROVED'
WHERE id = 1;
```

---

## 9. Delete Data

```sql
DELETE FROM bookings WHERE id = 1;
```

---

## 10. Important Rules

* SQL commands **must end with ;**
* Commands like `.tables` **do NOT use ;**
* If you see `...>` → press **Ctrl + C**

---

## 11. Exit SQLite

```
.exit
```

---

## 12. Optional GUI (Easier Way)

Install:

```bash
sudo apt install sqlitebrowser
```

Run:

```bash
sqlitebrowser hotel.db
```

---

## 13. Quick Workflow

```
sqlite3 hotel.db
.tables
.schema bookings
SELECT * FROM bookings;
```

---

Now you can:

* Open database ✅
* See tables ✅
* View fields ✅
* Search data ✅
* Modify data ✅

---

If something doesn’t work, check:

* Are you in the correct folder?
* Did you forget `;`?
* Is the table empty?
