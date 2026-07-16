# PowerPoint Presentation Slide Report

Project: Hotel Management System

Suggested format: 14-16 slides

---

## Slide 1: Cover Page

### Hotel Management System

Java Swing + SQLite Desktop Application

Prepared by:

| Team Member | Role | Photo |
|---|---|---|
| Member 1 / Your Name | Backend and Database Developer | Insert photo |
| Member 2 / Your Name | Admin UI Developer | Insert photo |
| Member 3 / Your Name | Staff and Login UI Developer | Insert photo |
| Member 4 / Your Name | Customer UI Developer | Insert photo |

Design idea for cover:

- Use a hotel/lobby background image with a dark transparent overlay.
- Put the project title in large white text.
- Add small icons for rooms, booking, payment, and reports.
- Use gold, navy, and white colors for a professional hotel style.
- Place team member photos in circular frames at the bottom.

---

## Slide 2: Introduction

### Project Overview

The Hotel Management System is a desktop application developed with Java Swing and SQLite. It helps manage daily hotel operations such as user login, room management, booking, customer records, payment processing, and reports.

The system supports three main user roles:

- Admin: manages rooms, staff, customers, bookings, payments, and reports.
- Staff: manages pending bookings, check-in/check-out workflows, and room status.
- Customer: browses available rooms, creates bookings, makes payments, and views booking history.

---

## Slide 3: Purpose and Objectives

### Purpose

The purpose of this project is to replace manual hotel record keeping with a simple computerized system that stores hotel data in a database and gives each user role its own dashboard.

### Objectives

- Provide secure login and role-based access.
- Store hotel data using SQLite.
- Allow admin users to manage rooms, customers, staff, payments, and reports.
- Allow staff users to approve or cancel bookings and manage room status.
- Allow customers to browse rooms, book rooms, pay, and view booking history.
- Apply Object-Oriented Programming concepts in a real project.
- Separate the system into clear layers: UI, service, DAO, model, and database.

---

## Slide 4: System Architecture

### Layered Design

The project is organized into different packages so each part has a clear responsibility.

```text
hotel.ui       -> Swing screens and dashboards
hotel.service  -> Business logic
hotel.dao      -> Database access and SQL queries
hotel.model    -> Data classes / objects
hotel.config   -> Database connection and initialization
hotel.util     -> Helper classes
database       -> SQLite schema
```

### Data Flow

```text
User clicks button in Swing UI
        ↓
Service validates business rule
        ↓
DAO executes SQL query
        ↓
SQLite stores or returns data
        ↓
UI displays result to user
```

---

## Slide 5: Object-Oriented Design

### OOP Concepts Applied

| OOP Concept | How It Is Used in the Project |
|---|---|
| Encapsulation | Model classes keep fields private and expose getters/setters. |
| Abstraction | `BaseService` defines shared service behavior and requires subclasses to provide a service name. |
| Inheritance | Service classes such as `BookingService`, `RoomService`, and `PaymentService` extend `BaseService`. |
| Polymorphism | DAO classes implement the shared generic `IDao<T>` interface. |
| Method Overriding | Services override `getServiceName()`, and model classes override `toString()`. |
| Separation of Concerns | UI does not write SQL directly; services handle rules and DAOs handle database work. |

---

## Slide 6: OOP Example - Encapsulation

### Example File: `src/hotel/model/Room.java`

```java
public class Room {
    private int id;
    private String roomNumber;
    private String type;
    private double pricePerNight;
    private boolean isAvailable;
    private String status;

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setStatus(String status) {
        this.status = status;
        this.isAvailable = "available".equalsIgnoreCase(status);
    }
}
```

### Explanation

The `Room` class uses private fields to protect the room data from direct modification. Other classes must use methods such as `getRoomNumber()`, `setPricePerNight()`, and `setStatus()`.

The `setStatus()` method also updates `isAvailable`, so the object keeps its data consistent when the room status changes.

Screenshot suggestion:

- Take a screenshot of `Room.java`, especially the private fields and getter/setter methods.

---

## Slide 7: OOP Example - Inheritance and Abstraction

### Example Files

- `src/hotel/service/BaseService.java`
- `src/hotel/service/BookingService.java`
- `src/hotel/service/RoomService.java`
- `src/hotel/service/PaymentService.java`

```java
public abstract class BaseService {
    protected abstract String getServiceName();

    protected void logAction(String action) {
        System.out.println("[" + getServiceName() + "] " + action);
    }
}
```

```java
public class BookingService extends BaseService {
    @Override
    protected String getServiceName() {
        return "BookingService";
    }
}
```

### Explanation

`BaseService` is an abstract class because it provides common behavior but is not meant to be used directly. Each service inherits `logAction()` and overrides `getServiceName()` with its own service name.

This reduces duplicated code and gives all service classes a consistent logging style.

---

## Slide 8: OOP Example - Interface and Polymorphism

### Example File: `src/hotel/dao/IDao.java`

```java
public interface IDao<T> {
    void add(T obj) throws SQLException;
    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
}
```

### Example Implementation: `RoomDAO`

```java
public class RoomDAO implements IDao<Room> {
    @Override
    public void add(Room room) throws SQLException {
        // Insert room data into the database
    }
}
```

### Explanation

`IDao<T>` defines a standard CRUD contract. All DAO classes must implement the same basic methods. This is polymorphism because the system can treat different DAO classes through the same interface pattern, while each DAO still works with its own model type.

Screenshot suggestion:

- Take one screenshot of `IDao.java`.
- Take another screenshot showing `RoomDAO implements IDao<Room>`.

---

## Extra Slide: Why We Used Encapsulation

### Concept

Encapsulation means hiding class data by using `private` fields and allowing access through public methods such as getters and setters.

Example from `Room.java`:

```java
private String roomNumber;
private double pricePerNight;
private boolean isAvailable;

public double getPricePerNight() {
    return pricePerNight;
}

public void setPricePerNight(double pricePerNight) {
    this.pricePerNight = pricePerNight;
}
```

### Why We Used It

We used encapsulation because hotel data should not be changed directly from outside the class. For example, room price, room status, customer email, and booking status should be controlled through methods.

### Benefits

- Protects important data from accidental changes.
- Makes the code easier to control and debug.
- Allows validation before updating values.
- Keeps each object responsible for its own data.

Presentation sentence:

> We used encapsulation to protect model data. Instead of changing fields directly, other classes must use getter and setter methods, which makes the system safer and easier to maintain.

---

## Extra Slide: Why We Used Interface

### Concept

An interface defines a contract that other classes must follow. In our project, `IDao<T>` defines the common database operations for all DAO classes.

Example from `IDao.java`:

```java
public interface IDao<T> {
    void add(T obj) throws SQLException;
    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
}
```

### Why We Used It

We used an interface so all DAO classes follow the same CRUD structure. For example, `RoomDAO`, `BookingDAO`, `CustomerDAO`, `PaymentDAO`, `StaffDAO`, and `UserDAO` all follow the same basic method pattern.

### Benefits

- Creates a standard rule for database classes.
- Makes the code more organized and predictable.
- Helps team members understand every DAO faster.
- Supports polymorphism because DAO classes can be handled through the same interface type.

Presentation sentence:

> We used `IDao<T>` as a contract for all DAO classes, so every database class has the same basic operations: add, get by id, get all, update, and delete.

---

## Extra Slide: Why We Used Abstract Class

### Concept

An abstract class is a class that cannot be created directly. It can contain shared methods and abstract methods that child classes must implement.

Example from `BaseService.java`:

```java
public abstract class BaseService {
    protected abstract String getServiceName();

    protected void logAction(String action) {
        System.out.println("[" + getServiceName() + "] " + action);
    }
}
```

### Why We Used It

We used `BaseService` because all service classes need common behavior, especially action logging. But each service also needs its own name, so `getServiceName()` is abstract and each child service must override it.

### Benefits

- Reduces duplicate code in service classes.
- Forces service classes to follow the same structure.
- Allows shared behavior through `logAction()`.
- Keeps business logic classes consistent.

Presentation sentence:

> We used an abstract `BaseService` to share common service behavior while forcing each service, such as `BookingService` and `RoomService`, to provide its own service name.

---

## Extra Slide: Why We Used Inheritance and Overriding

### Inheritance

Inheritance means a child class gets behavior from a parent class. In our project, service classes inherit from `BaseService`.

Example:

```java
public class BookingService extends BaseService {
    @Override
    protected String getServiceName() {
        return "BookingService";
    }
}
```

### Why We Used Inheritance

We used inheritance so `BookingService`, `RoomService`, `PaymentService`, and `ReportService` can reuse the `logAction()` method from `BaseService`.

### Method Overriding

Overriding means a child class provides its own version of a method from the parent class.

In our project, each service overrides `getServiceName()`:

- `BookingService` returns `"BookingService"`.
- `RoomService` returns `"RoomService"`.
- `PaymentService` returns `"PaymentService"`.
- `ReportService` returns `"ReportService"`.

### Benefits

- Reuses shared code from the parent class.
- Allows each service to customize its own behavior.
- Reduces repeated logging code.
- Makes the service layer easier to maintain.

Presentation sentence:

> We used inheritance to reuse common service code, and we used overriding so each service can return its own name while still using the shared logging method.

---

## Extra Slide: Why We Used Polymorphism and Upcasting

### Polymorphism

Polymorphism means one parent type or interface type can refer to different child objects.

Example:

```java
IDao<Room> dao = new RoomDAO();
```

Here, the variable type is `IDao<Room>`, but the real object is `RoomDAO`.

### Upcasting

Upcasting means storing a child object in a parent or interface reference.

Examples:

```java
BaseService service = new BookingService();
IDao<Room> roomDao = new RoomDAO();
```

### Why We Used It

We used this idea because our project has many classes with similar behavior. DAO classes share the `IDao<T>` contract, and service classes share the `BaseService` parent.

### Benefits

- Makes code more flexible.
- Allows different DAO classes to follow the same structure.
- Makes future changes easier if we replace or extend a class.
- Supports clean architecture because code can depend on a general type instead of a specific class.

Presentation sentence:

> We used polymorphism through `IDao<T>` and `BaseService`, so our code can work with general types while the real object still performs its specific behavior.

---

## Extra Slide: Why Downcasting Is Useful

### Concept

Downcasting means converting a parent reference back to a specific child type.

Example:

```java
BaseService service = new BookingService();

if (service instanceof BookingService) {
    BookingService bookingService = (BookingService) service;
}
```

### Why It Can Be Used

Downcasting is useful when an object is stored as a general parent type, but we need to access methods that only exist in the child class.

For example, if a `BookingService` object is stored as `BaseService`, we may need to downcast it back to `BookingService` to call booking-specific methods such as `createBooking()`.

### Benefits

- Allows access to child-specific methods.
- Works together with polymorphism and upcasting.
- Useful when handling different service objects in a general way.

Important note:

- Downcasting must be used carefully.
- We should check with `instanceof` before casting.
- Wrong downcasting can cause a runtime error.

Presentation sentence:

> Downcasting is useful when we first handle an object as a general parent type, but later need to use methods from the specific child class. We use `instanceof` to make it safer.

---

## Extra Slide: Why We Used Overloading

### Concept

Overloading means using the same method or constructor name with different parameters.

Example from service constructors:

```java
public RoomService() {
    this(new RoomDAO(), new BookingDAO());
}

public RoomService(RoomDAO roomDAO, BookingDAO bookingDAO) {
    this.roomDAO = roomDAO;
    this.bookingDAO = bookingDAO;
}
```

### Why We Used It

We used constructor overloading to provide more than one way to create service objects.

### Benefits

- Simple creation for normal application use.
- Flexible creation for testing or custom DAO objects.
- Makes the code easier to reuse.
- Supports dependency injection because DAOs can be passed from outside.

Presentation sentence:

> We used constructor overloading so a service can be created in a simple way for normal use, or with custom DAO objects when we need more control during testing or integration.

---

## Slide 9: Code Implementation - Challenging Logic

### Challenging Part: Create Booking Workflow

Example file: `src/hotel/service/BookingService.java`

```java
public Booking createBooking(int customerId, int roomId, String checkIn, String checkOut) {
    if (!DateUtil.isValidDate(checkIn) || !DateUtil.isValidDate(checkOut)) {
        throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD.");
    }
    if (!DateUtil.isBefore(checkIn, checkOut)) {
        throw new IllegalArgumentException("Check-in date must be before check-out date.");
    }

    Room room = roomDAO.getById(roomId);
    if (room == null || !room.isAvailable()) {
        return null;
    }

    int nights = DateUtil.calculateNights(checkIn, checkOut);
    Booking booking = new Booking(
        0, customerId, roomId, checkIn, checkOut,
        room.getPricePerNight() * nights,
        "pending"
    );

    bookingDAO.add(booking);
    room.setAvailable(false);
    roomDAO.update(room);
    return booking;
}
```

### Explanation

This logic was challenging because creating a booking requires several checks and updates:

- Validate the date format.
- Make sure check-in is before check-out.
- Check whether the selected room exists and is available.
- Calculate the total price using room price and number of nights.
- Save the booking in the database.
- Update the room so it is no longer available.

This prevents double booking and keeps room data synchronized with booking data.

Screenshot suggestion:

- Take a screenshot of the `createBooking()` method in `BookingService.java`.

---

## Slide 10: Code Implementation - Database Storage

### SQLite Database

The project stores data in `hotel.db` using SQLite. The database schema is defined in `database/schema.sql`.

Main tables:

- `users`
- `rooms`
- `customers`
- `staff`
- `bookings`
- `payments`

### JDBC Connection Example

Example file: `src/hotel/config/DBConnection.java`

```java
private static final String DB_URL = "jdbc:sqlite:hotel.db";

public static Connection getConnection() throws SQLException {
    Class.forName("org.sqlite.JDBC");
    if (connection == null || connection.isClosed()) {
        connection = DriverManager.getConnection(DB_URL);
    }
    return connection;
}
```

### Explanation

The database connection is centralized in `DBConnection`, so DAO classes do not need to create their own database connection manually. This makes database access easier to maintain and keeps the connection code in one place.

---

## Slide 11: Main Features

### Admin Features

- View dashboard statistics.
- Manage rooms.
- Manage staff.
- Manage customers.
- Manage bookings and payments.
- View income and booking reports.

### Staff Features

- Login through shared login screen.
- View pending bookings.
- Confirm or cancel bookings.
- Manage check-in/check-out process.
- Update room status.

### Customer Features

- Browse available rooms.
- Create a booking.
- Make payment using the payment screen.
- View booking history.

---

## Slide 12: Technologies Used

| Technology / Library | Usage |
|---|---|
| Java 17 | Main programming language |
| Java Swing | Desktop graphical user interface |
| SQLite | Local database storage |
| JDBC | Connect Java application to SQLite |
| IntelliJ IDEA | Project development environment |
| Figma | UI design planning |
| DB Browser for SQLite / SQLite tools | Database viewing and testing |
| OOP | Structure project using classes, interfaces, inheritance, and encapsulation |

---

## Slide 13: Conclusion

### Summary

The Hotel Management System is a Java desktop application for managing hotel operations. The project includes role-based login, room management, booking management, payment processing, customer records, staff tools, and reporting.

The system applies Object-Oriented Programming concepts by using model classes, service classes, DAO classes, an abstract base service, and a generic DAO interface. The layered structure makes the project easier to understand, test, and extend.

### Future Work

- Migrate the database from SQLite to MySQL for multi-computer use.
- Build a JAR file and convert it into an EXE installer.
- Add deposit payment support such as 20% or 30%.
- Add more detailed booking statuses such as checked-in and checked-out.
- Add more detailed room statuses such as cleaning and maintenance.
- Improve report charts and export options.
- Add stronger validation and error handling in all forms.
- Implement a real QR code generator for payments.

---

## Slide 14: References

### Project Files and Internal Guides

- `docs/HMS_Team_Division.docx` - team role division and system plan.
- `docs/DeveloperCodebaseGuide.md` - codebase architecture and component guide.
- `docs/READMEforBackend.md` - backend development guide and OOP checklist.
- `docs/GUI_Team_Task_Playbook.md` - GUI integration rules and team workflow.
- `docs/READMEQueries.md` - SQLite usage guide.
- `database/schema.sql` - database table structure.

### Learning References

- Java Swing tutorials and examples for building desktop UI.
- JDBC examples for connecting Java to a database.
- SQLite documentation and examples for database schema and SQL queries.
- OOP class materials: encapsulation, inheritance, abstraction, polymorphism, interfaces, and method overriding.

Note: Add the exact tutorial website or YouTube links your team followed if your teacher requires URLs.

---

## Optional Speaker Notes

### Slide 1

Good morning/afternoon everyone. Today we are presenting our Hotel Management System, a Java Swing desktop application using SQLite.

### Slide 2

Our system is designed for hotel daily operations. It has three roles: admin, staff, and customer. Each role has its own dashboard and responsibilities.

### Slide 3

The main purpose is to reduce manual work and store hotel information in a database. Our objectives include login, booking, room management, payment, and reporting.

### Slide 5

We applied OOP by separating the system into classes and packages. Models store data, services handle business rules, DAOs handle SQL, and UI classes display screens.

### Slide 9

The booking function is one of the most important parts. It checks dates, room availability, calculates price, creates a booking, and updates the room status.

### Slide 13

In conclusion, our project demonstrates a complete hotel management workflow and applies OOP concepts in a practical application. In the future, we can improve it by using MySQL, adding better statuses, and building an installer.
