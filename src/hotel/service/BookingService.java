package hotel.service;

import hotel.dao.BookingDAO;
import hotel.dao.RoomDAO;
import hotel.model.Booking;
import hotel.model.Room;
import hotel.util.DateUtil;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class BookingService extends BaseService {

	private final BookingDAO bookingDAO;
	private final RoomDAO roomDAO;

	public BookingService() {
		this(new BookingDAO(), new RoomDAO());
	}

	public BookingService(BookingDAO bookingDAO, RoomDAO roomDAO) {
		this.bookingDAO = bookingDAO;
		this.roomDAO = roomDAO;
	}

	public Booking createBooking(int customerId, int roomId, String checkIn, String checkOut) {
		if (!DateUtil.isValidDate(checkIn) || !DateUtil.isValidDate(checkOut)) {
			throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD.");
		}
		if (!DateUtil.isBefore(checkIn, checkOut)) {
			throw new IllegalArgumentException("Check-in date must be before check-out date.");
		}

		try {
			Room room = roomDAO.getById(roomId);
			if (room == null || !room.isAvailable()) {
				return null;
			}

			int nights = DateUtil.calculateNights(checkIn, checkOut);
			if (nights <= 0) {
				throw new IllegalArgumentException("Booking must be at least 1 night.");
			}

			Booking booking = new Booking(
					0,
					customerId,
					roomId,
					checkIn,
					checkOut,
					room.getPricePerNight() * nights,
					"pending"
			);
			bookingDAO.add(booking);

			room.setAvailable(false);
			roomDAO.update(room);

			logAction("Created booking id=" + booking.getId());
			return booking;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to create booking", e);
		}
	}

	public boolean cancelBooking(int bookingId) {
		try {
			Booking booking = bookingDAO.getById(bookingId);
			if (booking == null) {
				return false;
			}

			booking.setStatus("cancelled");
			bookingDAO.update(booking);

			Room room = roomDAO.getById(booking.getRoomId());
			if (room != null) {
				room.setAvailable(true);
				roomDAO.update(room);
			}

			logAction("Cancelled booking id=" + bookingId);
			return true;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to cancel booking", e);
		}
	}

	public boolean confirmBooking(int bookingId) {
		try {
			Booking booking = bookingDAO.getById(bookingId);
			if (booking == null) {
				return false;
			}
			booking.setStatus("confirmed");
			bookingDAO.update(booking);
			logAction("Confirmed booking id=" + bookingId);
			return true;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to confirm booking", e);
		}
	}

	public List<Booking> getBookingsForCustomer(int customerId) {
		try {
			return bookingDAO.getByCustomerId(customerId);
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public List<Booking> getAllBookings() {
		try {
			return bookingDAO.getAll();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	@Override
	protected String getServiceName() {
		return "BookingService";
	}
}
