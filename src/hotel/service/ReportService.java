package hotel.service;

import hotel.dao.BookingDAO;
import hotel.dao.PaymentDAO;
import hotel.dao.RoomDAO;
import hotel.model.Booking;
import hotel.model.Payment;
import hotel.model.Room;
import hotel.util.ReportExporter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService extends BaseService {

	private final RoomDAO roomDAO;
	private final BookingDAO bookingDAO;
	private final PaymentDAO paymentDAO;

	public ReportService() {
		this(new RoomDAO(), new BookingDAO(), new PaymentDAO());
	}

	public ReportService(RoomDAO roomDAO, BookingDAO bookingDAO, PaymentDAO paymentDAO) {
		this.roomDAO = roomDAO;
		this.bookingDAO = bookingDAO;
		this.paymentDAO = paymentDAO;
	}

	public String generateOccupancyReport() {
		try {
			List<Room> rooms = roomDAO.getAll();
			int total = rooms.size();
			int available = 0;
			for (Room room : rooms) {
				if (room.isAvailable()) {
					available++;
				}
			}
			int occupied = total - available;
			return "Occupancy Report\n"
					+ "Total Rooms: " + total + "\n"
					+ "Occupied Rooms: " + occupied + "\n"
					+ "Available Rooms: " + available;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to generate occupancy report", e);
		}
	}

	public String generateRevenueReport() {
		try {
			double revenue = paymentDAO.getTotalRevenue();
			int paidBookings = 0;
			List<Payment> payments = paymentDAO.getAll();
			for (Payment payment : payments) {
				if ("paid".equalsIgnoreCase(payment.getStatus())) {
					paidBookings++;
				}
			}
			return "Revenue Report\n"
					+ "Total Revenue: " + revenue + "\n"
					+ "Number of Paid Bookings: " + paidBookings;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to generate revenue report", e);
		}
	}

	public String generateBookingSummary() {
		try {
			List<Booking> bookings = bookingDAO.getAll();
			Map<String, Integer> countByStatus = new HashMap<>();
			for (Booking booking : bookings) {
				String status = booking.getStatus();
				countByStatus.put(status, countByStatus.getOrDefault(status, 0) + 1);
			}

			return "Booking Summary\n"
					+ "Pending: " + countByStatus.getOrDefault("pending", 0) + "\n"
					+ "Confirmed: " + countByStatus.getOrDefault("confirmed", 0) + "\n"
					+ "Cancelled: " + countByStatus.getOrDefault("cancelled", 0);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to generate booking summary", e);
		}
	}

	public void exportReport(String content, String filename) {
		ReportExporter.exportToTxt(content, filename);
		logAction("Exported report: " + filename);
	}

	@Override
	protected String getServiceName() {
		return "ReportService";
	}
}
