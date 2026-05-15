package hotel.dao;

import hotel.config.DBConnection;
import hotel.model.Staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO implements IDao<Staff> {

	@Override
	public void add(Staff staff) throws SQLException {
		String sql = "INSERT INTO staff (name, position, salary, user_id) VALUES (?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, staff.getName());
			stmt.setString(2, staff.getPosition());
			stmt.setDouble(3, staff.getSalary());
			stmt.setInt(4, staff.getUserId());
			stmt.executeUpdate();

			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys.next()) {
					staff.setId(keys.getInt(1));
				}
			}
		}
	}

	@Override
	public Staff getById(int id) throws SQLException {
		String sql = "SELECT * FROM staff WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapStaff(rs) : null;
			}
		}
	}

	@Override
	public List<Staff> getAll() throws SQLException {
		String sql = "SELECT * FROM staff ORDER BY id";
		List<Staff> staffList = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				staffList.add(mapStaff(rs));
			}
		}
		return staffList;
	}

	@Override
	public void update(Staff staff) throws SQLException {
		String sql = "UPDATE staff SET name = ?, position = ?, salary = ?, user_id = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, staff.getName());
			stmt.setString(2, staff.getPosition());
			stmt.setDouble(3, staff.getSalary());
			stmt.setInt(4, staff.getUserId());
			stmt.setInt(5, staff.getId());
			stmt.executeUpdate();
		}
	}

	@Override
	public void delete(int id) throws SQLException {
		String sql = "DELETE FROM staff WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
	}

	public List<Staff> getByPosition(String position) throws SQLException {
		String sql = "SELECT * FROM staff WHERE position = ? ORDER BY id";
		List<Staff> staffList = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, position);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					staffList.add(mapStaff(rs));
				}
			}
		}
		return staffList;
	}

	private Staff mapStaff(ResultSet rs) throws SQLException {
		return new Staff(
				rs.getInt("id"),
				rs.getString("name"),
				rs.getString("position"),
				rs.getDouble("salary"),
				rs.getInt("user_id")
		);
	}
}
