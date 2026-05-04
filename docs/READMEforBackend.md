# Hotel Management System — Project Instructions

> **Ground rules:** This document tells you *what* to build and *what concepts to apply*. It does NOT give you the code. Read each task carefully, plan your class design on paper first, then implement.

---

## WEEK 1 — DB Foundation + Models (10 files)

### Overview

Your job this week is to set up the database layer and write all the plain Java model classes (POJOs). No business logic yet — just structure, fields, and OOP fundamentals.

---

### File 1 — `schema.sql`

**What it is:** A raw SQL file that defines the structure of your database.

**Your tasks:**
- Create tables for: `users`, `rooms`, `bookings`, `payments`, `staff`, `customers`
- Each table must have a primary key (`id`, auto-increment)
- Define appropriate column types (`VARCHAR`, `INT`, `DOUBLE`, `DATE`, `BOOLEAN`, etc.)
- Add foreign key relationships where needed (e.g., a booking belongs to a customer and a room)
- Think about what data each entity needs to store

**Questions to answer before writing:**
- What columns does a `Room` have? (number, type, price, availability status…)
- What links a `Booking` to a `Customer` and a `Room`?
- What columns does a `Payment` have? (amount, date, method, booking reference…)

---

### File 2 — `DBConnection.java`

**What it is:** A utility class that manages the JDBC connection to your SQLite (or MySQL) database.

**OOP concept: Encapsulation**

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `getConnection()` | `Connection` | Returns an active database connection |
| `closeConnection()` | `void` | Closes the connection safely |

**Variables:**
- `private static Connection connection` — the single shared connection instance
- `private static final String DB_URL` — the database URL string (constant)

**Think about:**
- Why should `connection` be `private`? What happens if other classes can freely set it to `null`?
- Why use `static`? Should there be only one connection instance or many?
- Handle `SQLException` — what should happen if the connection fails?

---

### File 3 — `DBInitializer.java`

**What it is:** A utility class that runs your `schema.sql` file to create the tables on first launch.

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `initialize()` | `void` | Reads schema.sql and executes each statement |
| `tableExists(String tableName)` | `boolean` | Checks if a table already exists before creating it |

**Think about:**
- How do you read a `.sql` file in Java? (Hint: `FileReader`, `Scanner`, or `Files.readString()`)
- How do you split multiple SQL statements and execute them one by one?
- Why check if a table already exists before running `CREATE TABLE`?

---

### File 4 — `PasswordHasher.java`

**What it is:** A utility class for hashing and verifying passwords.

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `hash(String plainPassword)` | `String` | Returns a hashed version of the password |
| `verify(String plainPassword, String hashedPassword)` | `boolean` | Checks if a plain password matches its hash |

**Think about:**
- Why should you never store plain-text passwords?
- Look up `MessageDigest` (SHA-256) in Java — how do you convert bytes to a hex string?
- What is the difference between hashing and encryption?

---

### Files 5–10 — The 6 Model Classes

These are the core data-holder classes. They represent rows in your database tables.

**OOP concept applied to ALL models: Encapsulation**
- All fields must be `private`
- Provide `public` getters and setters for each field
- Provide at least **two constructors**: a no-arg constructor AND a full constructor that takes all fields as parameters ← this is **constructor overloading** (a form of polymorphism)

---

#### `User.java`

**Fields:**

| Field | Type | Description |
|---|---|---|
| `id` | `int` | Primary key |
| `username` | `String` | Login name |
| `passwordHash` | `String` | Hashed password (never plain text) |
| `role` | `String` | e.g., "admin", "staff" |
| `createdAt` | `String` | Timestamp string |

**Constructors:**
- `User()` — no-arg
- `User(int id, String username, String passwordHash, String role, String createdAt)` — full

**Methods:**
- Getters and setters for every field
- `toString()` — **override** this from `Object` to return a readable description ← this is **method overriding**

---

#### `Room.java`

**Fields:**

| Field | Type | Description |
|---|---|---|
| `id` | `int` | Primary key |
| `roomNumber` | `String` | e.g., "101", "202A" |
| `type` | `String` | e.g., "single", "double", "suite" |
| `pricePerNight` | `double` | Nightly rate |
| `isAvailable` | `boolean` | Current availability |

**Constructors:**
- `Room()` — no-arg
- `Room(int id, String roomNumber, String type, double pricePerNight, boolean isAvailable)` — full

**Methods:**
- Getters and setters for every field
- `toString()` — override from `Object`
- Think: should `isAvailable` have a getter named `isAvailable()` or `getIsAvailable()`? (Look up Java Bean convention for booleans)

---

#### `Customer.java`

**Fields:**

| Field | Type | Description |
|---|---|---|
| `id` | `int` | Primary key |
| `name` | `String` | Full name |
| `email` | `String` | Contact email |
| `phone` | `String` | Phone number |
| `address` | `String` | Home address |

**Constructors:**
- `Customer()` — no-arg
- `Customer(int id, String name, String email, String phone, String address)` — full

**Methods:**
- Getters and setters for every field
- `toString()` — override

---

#### `Booking.java`

**Fields:**

| Field | Type | Description |
|---|---|---|
| `id` | `int` | Primary key |
| `customerId` | `int` | Foreign key → Customer |
| `roomId` | `int` | Foreign key → Room |
| `checkInDate` | `String` | Format: YYYY-MM-DD |
| `checkOutDate` | `String` | Format: YYYY-MM-DD |
| `totalPrice` | `double` | Calculated total |
| `status` | `String` | e.g., "pending", "confirmed", "cancelled" |

**Constructors:**
- `Booking()` — no-arg
- `Booking(int id, int customerId, int roomId, String checkInDate, String checkOutDate, double totalPrice, String status)` — full

**Methods:**
- Getters and setters for every field
- `toString()` — override
- Think: where is `totalPrice` calculated? In this model class, or in the service later?

---

#### `Payment.java`

**Fields:**

| Field | Type | Description |
|---|---|---|
| `id` | `int` | Primary key |
| `bookingId` | `int` | Foreign key → Booking |
| `amount` | `double` | Amount paid |
| `paymentDate` | `String` | Format: YYYY-MM-DD |
| `method` | `String` | e.g., "cash", "card", "online" |
| `status` | `String` | e.g., "paid", "refunded" |

**Constructors:**
- `Payment()` — no-arg
- `Payment(int id, int bookingId, double amount, String paymentDate, String method, String status)` — full

**Methods:**
- Getters and setters for every field
- `toString()` — override

---

#### `Staff.java`

**Fields:**

| Field | Type | Description |
|---|---|---|
| `id` | `int` | Primary key |
| `name` | `String` | Full name |
| `position` | `String` | e.g., "receptionist", "manager" |
| `salary` | `double` | Monthly salary |
| `userId` | `int` | Foreign key → User (their login account) |

**Constructors:**
- `Staff()` — no-arg
- `Staff(int id, String name, String position, double salary, int userId)` — full

**Methods:**
- Getters and setters for every field
- `toString()` — override

---

### Week 1 OOP Checklist

Before you finish Week 1, make sure you can answer **yes** to every item:

- [ ] All model fields are `private` (encapsulation)
- [ ] Every model has a no-arg constructor AND a full constructor (constructor overloading)
- [ ] Every model overrides `toString()` from `Object` (method overriding)
- [ ] `DBConnection` uses `private static` for the connection field
- [ ] `PasswordHasher` methods are `static` — why does that make sense?
- [ ] You can explain what "encapsulation" means using your own `Room.java` as the example

---

---

## WEEK 2 — DAOs (6 files)

### Overview

DAO = Data Access Object. Each DAO class is responsible for all database operations (Create, Read, Update, Delete — CRUD) for one model. No UI, no business rules — only SQL queries wrapped in Java methods.

**OOP concept: Interface + Polymorphism**

You must create a **generic DAO interface** that all DAO classes implement.

---

### Step 0 — Create `IDao.java` (Interface)

Before writing any DAO, define a common interface:

```
interface IDao<T> {
    void add(T obj) throws SQLException;
    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
}
```

**OOP concepts here:**
- **Interface** — defines a contract every DAO must follow
- **Generics (`<T>`)** — the same interface works for any model type
- **Polymorphism** — you can write `IDao<Room> dao = new RoomDAO()` and swap implementations freely

---

### Each DAO class must:

1. Implement `IDao<ModelClass>` (e.g., `UserDAO implements IDao<User>`)
2. Use `DBConnection.getConnection()` — never create a raw connection inside the DAO
3. Use `PreparedStatement` — never concatenate user input directly into SQL strings (SQL injection risk)
4. Map `ResultSet` rows back to model objects

---

### `UserDAO.java`

**Implements:** `IDao<User>`

**Methods to implement:**

| Method | SQL operation | Notes |
|---|---|---|
| `add(User user)` | `INSERT` | Store hashed password, not plain text |
| `getById(int id)` | `SELECT WHERE id=?` | Return `null` if not found |
| `getAll()` | `SELECT *` | Return `List<User>` |
| `update(User user)` | `UPDATE WHERE id=?` | Update all fields |
| `delete(int id)` | `DELETE WHERE id=?` | |
| `getByUsername(String username)` | `SELECT WHERE username=?` | Extra method — used by AuthService later |

**Think about:**
- What does `PreparedStatement` protect against?
- When mapping a `ResultSet` to a `User`, what is `rs.getString("username")` equivalent to?

---

### `RoomDAO.java`

**Implements:** `IDao<Room>`

**Methods to implement:**

| Method | SQL operation | Notes |
|---|---|---|
| `add(Room room)` | `INSERT` | |
| `getById(int id)` | `SELECT WHERE id=?` | |
| `getAll()` | `SELECT *` | |
| `update(Room room)` | `UPDATE WHERE id=?` | |
| `delete(int id)` | `DELETE WHERE id=?` | |
| `getAvailableRooms()` | `SELECT WHERE isAvailable=true` | Extra method |
| `getRoomsByType(String type)` | `SELECT WHERE type=?` | Extra method — **overloaded** concept: two ways to filter rooms |

---

### `BookingDAO.java`

**Implements:** `IDao<Booking>`

**Methods to implement:**

| Method | SQL operation | Notes |
|---|---|---|
| `add(Booking booking)` | `INSERT` | |
| `getById(int id)` | `SELECT WHERE id=?` | |
| `getAll()` | `SELECT *` | |
| `update(Booking booking)` | `UPDATE WHERE id=?` | |
| `delete(int id)` | `DELETE WHERE id=?` | |
| `getByCustomerId(int customerId)` | `SELECT WHERE customerId=?` | All bookings for one customer |
| `getByStatus(String status)` | `SELECT WHERE status=?` | Filter by "confirmed", "pending", etc. |

---

### `PaymentDAO.java`

**Implements:** `IDao<Payment>`

**Methods to implement:**

| Method | SQL operation | Notes |
|---|---|---|
| `add(Payment payment)` | `INSERT` | |
| `getById(int id)` | `SELECT WHERE id=?` | |
| `getAll()` | `SELECT *` | |
| `update(Payment payment)` | `UPDATE WHERE id=?` | |
| `delete(int id)` | `DELETE WHERE id=?` | |
| `getByBookingId(int bookingId)` | `SELECT WHERE bookingId=?` | Extra method |
| `getTotalRevenue()` | `SELECT SUM(amount)` | Extra method — useful for reports |

---

### `StaffDAO.java`

**Implements:** `IDao<Staff>`

**Methods to implement:**

| Method | SQL operation | Notes |
|---|---|---|
| `add(Staff staff)` | `INSERT` | |
| `getById(int id)` | `SELECT WHERE id=?` | |
| `getAll()` | `SELECT *` | |
| `update(Staff staff)` | `UPDATE WHERE id=?` | |
| `delete(int id)` | `DELETE WHERE id=?` | |
| `getByPosition(String position)` | `SELECT WHERE position=?` | Extra method |

---

### `CustomerDAO.java`

**Implements:** `IDao<Customer>`

**Methods to implement:**

| Method | SQL operation | Notes |
|---|---|---|
| `add(Customer customer)` | `INSERT` | |
| `getById(int id)` | `SELECT WHERE id=?` | |
| `getAll()` | `SELECT *` | |
| `update(Customer customer)` | `UPDATE WHERE id=?` | |
| `delete(int id)` | `DELETE WHERE id=?` | |
| `getByEmail(String email)` | `SELECT WHERE email=?` | Extra method — for duplicate check |
| `searchByName(String keyword)` | `SELECT WHERE name LIKE ?` | Extra method — search functionality |

---

### Week 2 OOP Checklist

- [ ] You created `IDao<T>` interface before writing any DAO class
- [ ] Every DAO implements `IDao<T>` — the compiler enforces the contract
- [ ] You can explain: what does `implements` mean vs `extends`?
- [ ] Every DAO uses `PreparedStatement`, never raw string SQL
- [ ] You tested each DAO with a small `main()` or test method — add/get/update/delete all work
- [ ] You can write `IDao<Room> dao = new RoomDAO()` and call `dao.getAll()` ← polymorphism in action

---

---

## WEEK 3 — Services + Utilities (7 files)

### Overview

Services contain the **business logic**. They sit between the UI and the DAOs. A service does NOT talk to the database directly — it calls DAO methods. Services are where rules like "you can't book a room that's already booked" live.

**OOP concept: Abstraction via Abstract Class + Inheritance**

Create an abstract base class `BaseService.java` that all service classes extend.

---

### Step 0 — Create `BaseService.java` (Abstract Class)

```
abstract class BaseService {
    protected abstract String getServiceName();
    protected void logAction(String action) {
        System.out.println("[" + getServiceName() + "] " + action);
    }
}
```

**OOP concepts here:**
- **Abstract class** — cannot be instantiated directly; it is a blueprint
- **Abstract method** (`getServiceName()`) — every subclass MUST implement it (override it)
- **Concrete method** (`logAction()`) — shared behavior inherited by all services
- **Inheritance** — `AuthService extends BaseService`

---

### `AuthService.java`

**Extends:** `BaseService`

**Purpose:** Handle login, logout, and session tracking.

**Fields:**
- `private UserDAO userDAO` — injected dependency
- `private User loggedInUser` — tracks who is currently logged in

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `login(String username, String password)` | `boolean` | Hash the password, compare with stored hash via `UserDAO`, set `loggedInUser` on success |
| `logout()` | `void` | Clear `loggedInUser` |
| `isLoggedIn()` | `boolean` | Return whether someone is logged in |
| `getCurrentUser()` | `User` | Return the currently logged-in user |
| `getServiceName()` | `String` | Override abstract method — return `"AuthService"` |

**Think about:**
- Who creates the `UserDAO` — the service, or whoever creates the service? (Hint: look up "dependency injection" briefly)
- Why is `loggedInUser` private? What happens if other classes can set it to any user they want?

---

### `BookingService.java`

**Extends:** `BaseService`

**Purpose:** Handle all booking operations with business rules applied.

**Fields:**
- `private BookingDAO bookingDAO`
- `private RoomDAO roomDAO`

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `createBooking(int customerId, int roomId, String checkIn, String checkOut)` | `Booking` | Validate dates, check room availability, calculate price, create booking, mark room unavailable |
| `cancelBooking(int bookingId)` | `boolean` | Change status to "cancelled", mark room available again |
| `confirmBooking(int bookingId)` | `boolean` | Change status to "confirmed" |
| `getBookingsForCustomer(int customerId)` | `List<Booking>` | Delegate to DAO |
| `getAllBookings()` | `List<Booking>` | Delegate to DAO |
| `getServiceName()` | `String` | Return `"BookingService"` |

**Business rules to enforce inside `createBooking()`:**
- Check-in date must be before check-out date
- Room must currently be available (`isAvailable == true`)
- Calculate `totalPrice = pricePerNight × numberOfNights`
- After creating the booking, update the room's `isAvailable` to `false`

**Think about:**
- If `createBooking()` throws an exception halfway through (booking saved but room not updated), what goes wrong? This is a "transaction" problem.
- Why does this service need BOTH `bookingDAO` AND `roomDAO`?

---

### `RoomService.java`

**Extends:** `BaseService`

**Purpose:** Handle room management with validation.

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `addRoom(String number, String type, double price)` | `boolean` | Validate inputs, create and save room |
| `updateRoom(Room room)` | `boolean` | Validate, then update via DAO |
| `deleteRoom(int roomId)` | `boolean` | Check if room has active bookings before deleting |
| `getAllRooms()` | `List<Room>` | Delegate to DAO |
| `getAvailableRooms()` | `List<Room>` | Delegate to DAO |
| `searchRooms(String type)` | `List<Room>` | Filter by room type |
| `getServiceName()` | `String` | Return `"RoomService"` |

**Business rule:** `deleteRoom()` must refuse to delete a room that has a "confirmed" or "pending" booking associated with it.

---

### `PaymentService.java`

**Extends:** `BaseService`

**Purpose:** Process and track payments.

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `processPayment(int bookingId, double amount, String method)` | `Payment` | Create a payment record, update booking status to "confirmed" |
| `refundPayment(int paymentId)` | `boolean` | Change payment status to "refunded" |
| `getPaymentsForBooking(int bookingId)` | `List<Payment>` | Delegate to DAO |
| `getTotalRevenue()` | `double` | Delegate to DAO |
| `getServiceName()` | `String` | Return `"PaymentService"` |

**Business rule:** `processPayment()` — the amount paid should match the booking's `totalPrice`. What should you do if they don't match?

---

### `ReportService.java`

**Extends:** `BaseService`

**Purpose:** Generate summary reports by combining data from multiple DAOs.

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `generateOccupancyReport()` | `String` | How many rooms are occupied vs available |
| `generateRevenueReport()` | `String` | Total revenue and number of paid bookings |
| `generateBookingSummary()` | `String` | Count of bookings by status |
| `exportReport(String content, String filename)` | `void` | Write report string to a `.txt` file (delegates to `ReportExporter`) |
| `getServiceName()` | `String` | Return `"ReportService"` |

---

### `DateUtil.java`

**What it is:** A static utility class for date operations (no instantiation needed).

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `calculateNights(String checkIn, String checkOut)` | `int` | Parse dates, return number of days between them |
| `isValidDate(String date)` | `boolean` | Check if string is a valid YYYY-MM-DD date |
| `isBefore(String date1, String date2)` | `boolean` | Return true if date1 is strictly before date2 |
| `today()` | `String` | Return today's date as YYYY-MM-DD string |

**Think about:**
- Why are all methods `static`? (No state to maintain — just transformations)
- Look up `LocalDate.parse()` in Java — this is the modern way to handle dates

---

### `ReportExporter.java`

**What it is:** A utility for writing report files to disk.

**Methods to implement:**

| Method | Return type | Description |
|---|---|---|
| `exportToTxt(String content, String filename)` | `void` | Write string content to `reports/filename.txt` |
| `ensureReportsDir()` | `void` | Create the `reports/` directory if it doesn't exist |

**Think about:**
- What Java class do you use to write text to a file? (`FileWriter`, `BufferedWriter`, `Files.writeString()`?)
- What happens if the `reports/` directory doesn't exist when you try to write to it?

---

### Week 3 OOP Checklist

- [ ] `BaseService` is abstract and cannot be instantiated directly
- [ ] Every service **extends** `BaseService` and **overrides** `getServiceName()` ← method overriding
- [ ] `logAction()` in `BaseService` is inherited and callable from every service without rewriting it ← inheritance
- [ ] Services call DAOs — they do NOT write SQL themselves ← separation of concerns
- [ ] `DateUtil` and `ReportExporter` have only `static` methods — you never do `new DateUtil()`
- [ ] You can explain: what is the difference between an `interface` (Week 2) and an `abstract class` (Week 3)?

---

---

## WEEK 4 — Integration + Bug Fixes (0 new files)

### Overview

No new files. This week you support teammates connecting their UI panels to your services, and fix any bugs discovered during integration.

---

### Your tasks:

**1. Expose clean service APIs**

Make sure your service methods have clear, predictable signatures. Other members should be able to call `bookingService.createBooking(...)` without understanding your internal logic.

**2. Fix DAO/Service bugs found during integration**

Common issues to watch for:
- `NullPointerException` — did you forget to initialize a DAO field?
- `SQLException` — are you closing `PreparedStatement` and `ResultSet` in a `finally` block (or using try-with-resources)?
- Room availability not updating after booking
- `totalPrice` calculating as 0 because `DateUtil.calculateNights()` returns 0

**3. Write a basic integration test**

Create a temporary `TestIntegration.java` (not part of the final product) and manually test this full flow:
1. Add a customer
2. Add a room
3. Create a booking for that customer and room
4. Process a payment for that booking
5. Generate a revenue report and verify the number is correct
6. Cancel the booking and verify the room is marked available again

**4. OOP review — can you answer these?**

| Question | Concept tested |
|---|---|
| Why do all your models have `private` fields? | Encapsulation |
| Why does `RoomDAO` implement `IDao<Room>` instead of just writing the methods freely? | Interface / Polymorphism |
| What would break if you wrote SQL inside `BookingService` instead of calling `BookingDAO`? | Separation of concerns |
| Why can't you do `new BaseService()`? | Abstract class |
| You have `toString()` in every model — is that overriding or overloading? | Method overriding |
| `UserDAO` has one `add(User)` method. What if you also needed `add(User, boolean sendEmail)`? What concept is that? | Method overloading |
| `BookingService` and `RoomService` both inherit `logAction()` — what is that called? | Inheritance |

---

### Final project structure

```
src/
├── db/
│   ├── DBConnection.java
│   ├── DBInitializer.java
│   └── PasswordHasher.java
├── model/
│   ├── User.java
│   ├── Room.java
│   ├── Booking.java
│   ├── Payment.java
│   ├── Staff.java
│   └── Customer.java
├── dao/
│   ├── IDao.java          ← interface
│   ├── UserDAO.java
│   ├── RoomDAO.java
│   ├── BookingDAO.java
│   ├── PaymentDAO.java
│   ├── StaffDAO.java
│   └── CustomerDAO.java
├── service/
│   ├── BaseService.java   ← abstract class
│   ├── AuthService.java
│   ├── BookingService.java
│   ├── RoomService.java
│   ├── PaymentService.java
│   └── ReportService.java
└── util/
    ├── DateUtil.java
    └── ReportExporter.java
```

---

## OOP Concept Summary

| Concept | Where you use it | Why |
|---|---|---|
| **Encapsulation** | All model classes (`private` fields + getters/setters) | Protect data from being changed arbitrarily |
| **Inheritance** | All services extend `BaseService` | Share `logAction()` without duplicating code |
| **Abstraction** | `BaseService` (abstract class) | Force every service to define its own `getServiceName()` |
| **Interface** | `IDao<T>` implemented by all DAOs | Guarantee a consistent CRUD contract across all DAOs |
| **Polymorphism** | `IDao<Room> dao = new RoomDAO()` | Swap implementations without changing calling code |
| **Method Overriding** | `toString()` in models, `getServiceName()` in services | Customize inherited behavior for each specific class |
| **Method Overloading** | Multiple constructors in models | Create objects with or without providing all fields |

---

*Good luck, Dom. Design on paper first — always.*