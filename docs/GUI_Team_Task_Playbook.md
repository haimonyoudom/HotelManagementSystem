# GUI Team Task Playbook

Purpose: make Members 2, 3, and 4 implement their screens quickly using the current backend without breaking integration.

Source alignment:
- Team division file: docs/HMS_Team_Division.docx
- Existing backend implementation: config, dao, service, model, and main packages

## Current Backend Contract (Use This Exactly)

### Roles currently used in code
- admin
- staff
- customer

### Booking statuses currently used in code
- pending
- confirmed
- cancelled

### Payment statuses currently used in code
- paid
- refunded

### Room state currently supported in backend model
- boolean isAvailable only
- true means available
- false means unavailable/occupied/blocked

### Date format currently accepted by booking flow
- YYYY-MM-DD
- Example: 2026-05-20

Important mismatch with team document:
- Team doc proposes APPROVED, CHECKED_IN, CHECKED_OUT, and timestamp dates.
- Current backend does not support these states yet.
- For now, all GUI members must use the existing contract above to avoid runtime errors.

## Shared Integration Rules for GUI Members

1. Do not write SQL inside Swing panels.
2. Use service methods first; use DAO only where no service method exists yet.
3. Keep all status values lowercase to match current backend.
4. Parse and submit dates only in YYYY-MM-DD for booking creation.
5. Keep all UI role-routing logic aligned with admin, staff, customer.

## Member 2 Implementation Guide (Admin UI)

Owned screens:
- AdminDashboard
- ManageRoomsPanel
- ManageStaffPanel
- ManageCustomersPanel
- IncomeReportPanel

### 1) AdminDashboard
Goal: summary cards for rooms, customers, staff, income.

Use existing methods:
- Room count: RoomService.getAllRooms()
- Customer count: CustomerDAO.getAll()
- Staff count: StaffDAO.getAll()
- Revenue: PaymentService.getTotalRevenue()

Recommended card calculations:
- Total rooms = size of getAllRooms
- Available rooms = count where isAvailable is true
- Occupied rooms = total minus available
- Total customers = size of customer list
- Total staff = size of staff list
- Total revenue = payment service total

### 2) ManageRoomsPanel
Goal: list/add/edit/delete rooms.

Use existing methods:
- Load table: RoomService.getAllRooms()
- Add room: RoomService.addRoom(number, type, price)
- Update room: RoomService.updateRoom(room)
- Delete room: RoomService.deleteRoom(roomId)

Validation in UI before call:
- number not empty
- type not empty
- price > 0

### 3) ManageStaffPanel
Goal: list/filter/delete staff.

Use existing methods:
- List all: StaffDAO.getAll()
- Filter position: StaffDAO.getByPosition(position)
- Delete staff: StaffDAO.delete(staffId)

Note:
- There is no StaffService currently.
- Use DAO calls from controller logic until StaffService exists.

### 4) ManageCustomersPanel
Goal: list/search/add/delete customers.

Use existing methods:
- List all: CustomerDAO.getAll()
- Search by name: CustomerDAO.searchByName(keyword)
- Add customer: CustomerDAO.add(customer)
- Delete customer: CustomerDAO.delete(customerId)

### 5) IncomeReportPanel
Goal: show occupancy/revenue/booking summary and export.

Use existing methods:
- Occupancy text: ReportService.generateOccupancyReport()
- Revenue text: ReportService.generateRevenueReport()
- Booking summary text: ReportService.generateBookingSummary()
- Export report: ReportService.exportReport(content, filename)

## Member 3 Implementation Guide (Staff and Auth UI)

Owned screens:
- LoginFrame
- StaffDashboard
- PendingBookingsPanel
- CheckInOutPanel
- RoomStatusPanel
- Shared common components (MainFrame, SidebarPanel, HeaderPanel, DialogHelper)

### 1) LoginFrame and role routing
Use existing methods:
- Login: AuthService.login(username, password)
- Current user: AuthService.getCurrentUser()
- Role check: user.getRole()

Routing:
- admin -> Admin dashboard
- staff -> Staff dashboard
- customer -> Customer dashboard

### 2) StaffDashboard
Goal: quick summary for staff actions.

Use existing methods:
- All bookings: BookingService.getAllBookings()
- Pending count: filter bookings where status equals pending
- Available rooms count: RoomService.getAvailableRooms().size

### 3) PendingBookingsPanel
Goal: approve/reject pending bookings.

Use existing methods:
- Data source option A: BookingService.getAllBookings() then filter pending
- Data source option B: BookingDAO.getByStatus("pending")
- Approve: BookingService.confirmBooking(bookingId)
- Reject: BookingService.cancelBooking(bookingId)

Deposit selector note:
- Team doc asks for 20 percent or 30 percent deposit.
- Current PaymentService.processPayment requires full booking total.
- Temporary UI rule: collect deposit selection as UI-only metadata, do not call partial payment until backend supports deposits.

### 4) CheckInOutPanel
Current backend limitation:
- No check-in/check-out status methods yet.
- No check-in/check-out timestamp fields in booking table.

Temporary behavior:
- Use confirmed as checked-in equivalent for now.
- Use cancelled only for cancellation flow.
- Show timestamp in UI view layer only (not persisted) until backend schema update is added.

### 5) RoomStatusPanel
Current backend limitation:
- Only boolean room availability exists.
- No CLEANING or MAINTENANCE state in Room model.

Temporary mapping:
- AVAILABLE -> isAvailable true
- BOOKED/CLEANING/MAINTENANCE -> isAvailable false

Use existing methods:
- Load rooms: RoomService.getAllRooms()
- Update status by toggling room.setAvailable(...) then RoomService.updateRoom(room)

## Member 4 Implementation Guide (Customer UI)

Owned screens:
- CustomerDashboard
- BrowseRoomsPanel
- BookingPanel
- PaymentQRPanel
- BookingHistoryPanel
- QRCodeGenerator

### 1) CustomerDashboard
Goal: welcome and booking summary.

Use existing methods:
- Customer bookings: BookingService.getBookingsForCustomer(customerId)
- Summary counts by status: pending, confirmed, cancelled

### 2) BrowseRoomsPanel
Use existing methods:
- All available rooms: RoomService.getAvailableRooms()
- Optional filter by type: RoomService.searchRooms(type)

### 3) BookingPanel
Use existing methods:
- Create booking: BookingService.createBooking(customerId, roomId, checkIn, checkOut)

Input rules:
- date format must be YYYY-MM-DD
- check-in must be before check-out
- room must be available

### 4) PaymentQRPanel
Current backend limitation:
- QRCodeGenerator is currently empty.
- PaymentService expects full amount, not deposit percent.

Use existing methods now:
- Payment submit: PaymentService.processPayment(bookingId, fullAmount, method)
- Fetch booking amount before display using booking list lookup

Temporary UI behavior:
- Show demo QR image placeholder.
- Show selected deposit percent as informational text only.
- Execute real payment only with full booking total.

### 5) BookingHistoryPanel
Use existing methods:
- BookingService.getBookingsForCustomer(customerId)
- Show statuses exactly as pending, confirmed, cancelled

## Existing Methods Quick Reference

Auth service:
- login(username, password)
- logout()
- isLoggedIn()
- getCurrentUser()

Room service:
- addRoom(number, type, price)
- updateRoom(room)
- deleteRoom(roomId)
- getAllRooms()
- getAvailableRooms()
- searchRooms(type)

Booking service:
- createBooking(customerId, roomId, checkIn, checkOut)
- cancelBooking(bookingId)
- confirmBooking(bookingId)
- getBookingsForCustomer(customerId)
- getAllBookings()

Payment service:
- processPayment(bookingId, amount, method)
- refundPayment(paymentId)
- getPaymentsForBooking(bookingId)
- getTotalRevenue()

Report service:
- generateOccupancyReport()
- generateRevenueReport()
- generateBookingSummary()
- exportReport(content, filename)

DAO methods often needed by admin/staff screens:
- CustomerDAO.getAll, searchByName, add, delete
- StaffDAO.getAll, getByPosition, delete
- UserDAO.getAll, getByUsername, update, delete
- BookingDAO.getByStatus

## Team Integration Checklist (Before Merge)

1. All three members use the same status values: pending, confirmed, cancelled.
2. All booking date inputs follow YYYY-MM-DD.
3. No Swing classes call SQL directly.
4. Admin and staff permissions checked in UI before action buttons execute.
5. Each panel tested with live data from hotel.db.
6. No member introduces new status strings without backend alignment.

## Recommended Next Backend Additions (After GUI MVP)

These are optional follow-up tasks after current GUI implementation is stable:
- Add BookingStatus enum and new statuses: checked_in, checked_out
- Add deposit payment support in PaymentService
- Add room status field beyond boolean availability
- Add StaffService and CustomerService for cleaner UI-service separation
- Implement QRCodeGenerator

Generated on: 2026-05-15
