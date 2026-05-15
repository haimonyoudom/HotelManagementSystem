package hotel.service;

import hotel.dao.BookingDAO;
import hotel.dao.PaymentDAO;
import hotel.model.Booking;
import hotel.model.Payment;
import hotel.util.DateUtil;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PaymentService extends BaseService {

	private final PaymentDAO paymentDAO;
	private final BookingDAO bookingDAO;

	public PaymentService() {
		this(new PaymentDAO(), new BookingDAO());
	}

	public PaymentService(PaymentDAO paymentDAO, BookingDAO bookingDAO) {
		this.paymentDAO = paymentDAO;
		this.bookingDAO = bookingDAO;
	}

	public Payment processPayment(int bookingId, double amount, String method) {
		try {
			Booking booking = bookingDAO.getById(bookingId);
			if (booking == null) {
				return null;
			}

			if (Math.abs(booking.getTotalPrice() - amount) > 0.000001) {
				throw new IllegalArgumentException("Paid amount must match booking total price.");
			}

			Payment payment = new Payment(0, bookingId, amount, DateUtil.today(), method, "paid");
			paymentDAO.add(payment);

			booking.setStatus("confirmed");
			bookingDAO.update(booking);

			logAction("Processed payment id=" + payment.getId() + " for booking id=" + bookingId);
			return payment;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to process payment", e);
		}
	}

	public boolean refundPayment(int paymentId) {
		try {
			Payment payment = paymentDAO.getById(paymentId);
			if (payment == null) {
				return false;
			}
			payment.setStatus("refunded");
			paymentDAO.update(payment);
			logAction("Refunded payment id=" + paymentId);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public List<Payment> getPaymentsForBooking(int bookingId) {
		try {
			return paymentDAO.getByBookingId(bookingId);
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public double getTotalRevenue() {
		try {
			return paymentDAO.getTotalRevenue();
		} catch (SQLException e) {
			return 0.0;
		}
	}

	@Override
	protected String getServiceName() {
		return "PaymentService";
	}
}
