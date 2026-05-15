package hotel.service;

import hotel.dao.BookingDAO;
import hotel.dao.RoomDAO;
import hotel.model.Booking;
import hotel.model.Room;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class RoomService extends BaseService {

	private final RoomDAO roomDAO;
	private final BookingDAO bookingDAO;

	public RoomService() {
		this(new RoomDAO(), new BookingDAO());
	}

	public RoomService(RoomDAO roomDAO, BookingDAO bookingDAO) {
		this.roomDAO = roomDAO;
		this.bookingDAO = bookingDAO;
	}

	public boolean addRoom(String number, String type, double price) {
		if (number == null || number.trim().isEmpty() || type == null || type.trim().isEmpty() || price <= 0) {
			return false;
		}

		try {
			Room room = new Room(0, number.trim(), type.trim(), price, true);
			roomDAO.add(room);
			logAction("Added room id=" + room.getId());
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean updateRoom(Room room) {
		if (room == null || room.getId() <= 0 || room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()
				|| room.getType() == null || room.getType().trim().isEmpty() || room.getPricePerNight() <= 0) {
			return false;
		}

		try {
			roomDAO.update(room);
			logAction("Updated room id=" + room.getId());
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean deleteRoom(int roomId) {
		try {
			List<Booking> relatedBookings = bookingDAO.getByRoomId(roomId);
			for (Booking booking : relatedBookings) {
				String status = booking.getStatus();
				if ("pending".equalsIgnoreCase(status) || "confirmed".equalsIgnoreCase(status)) {
					return false;
				}
			}
			roomDAO.delete(roomId);
			logAction("Deleted room id=" + roomId);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public List<Room> getAllRooms() {
		try {
			return roomDAO.getAll();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public List<Room> getAvailableRooms() {
		try {
			return roomDAO.getAvailableRooms();
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	public List<Room> searchRooms(String type) {
		if (type == null || type.trim().isEmpty()) {
			return getAllRooms();
		}
		try {
			return roomDAO.getRoomsByType(type.trim());
		} catch (SQLException e) {
			return Collections.emptyList();
		}
	}

	@Override
	protected String getServiceName() {
		return "RoomService";
	}
}
