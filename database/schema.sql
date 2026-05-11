-- Table 1: users
CREATE TABLE IF NOT EXISTS users (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    username      TEXT    NOT NULL UNIQUE,
    password      TEXT    NOT NULL,
    role          TEXT    NOT NULL,
    created_at    TEXT    NOT NULL
);

-- Table 2: rooms
CREATE TABLE IF NOT EXISTS rooms (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    room_number    TEXT    NOT NULL UNIQUE,
    type           TEXT    NOT NULL,
    price_per_night REAL    NOT NULL,
    is_available   INTEGER NOT NULL DEFAULT 1
);

-- Table 3: customers
CREATE TABLE IF NOT EXISTS customers (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    name     TEXT    NOT NULL,
    email    TEXT    NOT NULL UNIQUE,
    phone    TEXT,
    address  TEXT
);

-- Table 4: staff
CREATE TABLE IF NOT EXISTS staff (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    name      TEXT    NOT NULL,
    position  TEXT    NOT NULL,
    salary    REAL    NOT NULL,
    user_id   INTEGER NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Table 5: bookings
CREATE TABLE IF NOT EXISTS bookings (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id    INTEGER NOT NULL,
    room_id        INTEGER NOT NULL,
    check_in_date  TEXT    NOT NULL,
    check_out_date TEXT    NOT NULL,
    total_price    REAL    NOT NULL,
    status         TEXT    NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

-- Table 6: payments
CREATE TABLE IF NOT EXISTS payments (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id    INTEGER NOT NULL,
    amount        REAL    NOT NULL,
    payment_date  TEXT    NOT NULL,
    method        TEXT    NOT NULL,
    status        TEXT    NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);