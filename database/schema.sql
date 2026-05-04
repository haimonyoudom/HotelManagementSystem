-- Table 1: users
CREATE TABLE IF NOT EXISTS users (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    username    TEXT    NOT NULL UNIQUE,
    password    TEXT    NOT NULL,
    role        TEXT    NOT NULL,        -- values: ADMIN, STAFF, CUSTOMER
    created_at  TEXT    NOT NULL
);

-- Table 2: rooms
CREATE TABLE IF NOT EXISTS rooms (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    room_number  TEXT    NOT NULL UNIQUE,
    type         TEXT    NOT NULL,       -- e.g. SINGLE, DOUBLE, SUITE
    price        REAL    NOT NULL,
    status       TEXT    NOT NULL,       -- AVAILABLE, BOOKED, CLEANING, MAINTENANCE
    description  TEXT
);

-- Table 3: customers
CREATE TABLE IF NOT EXISTS customers (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER NOT NULL,
    full_name  TEXT    NOT NULL,
    phone      TEXT,
    email      TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table 4: staff
CREATE TABLE IF NOT EXISTS staff (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id    INTEGER NOT NULL,
    full_name  TEXT    NOT NULL,
    position   TEXT,
    phone      TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table 5: bookings
CREATE TABLE IF NOT EXISTS bookings (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id     INTEGER NOT NULL,
    room_id         INTEGER NOT NULL,
    check_in        TEXT    NOT NULL,   -- format: yyyy-MM-dd HH:mm:ss
    check_out       TEXT    NOT NULL,
    status          TEXT    NOT NULL,   -- PENDING, APPROVED, CHECKED_IN, CHECKED_OUT, CANCELLED
    deposit_percent INTEGER NOT NULL,   -- 20 or 30
    created_at      TEXT    NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (room_id)     REFERENCES rooms(id)
);

-- Table 6: payments
CREATE TABLE IF NOT EXISTS payments (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id  INTEGER NOT NULL,
    amount      REAL    NOT NULL,
    paid_at     TEXT    NOT NULL,
    method      TEXT    NOT NULL,       -- e.g. QR, CASH
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);