package hotel.dao;

import hotel.config.DBConnection;
import hotel.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO implements IDao<Customer> {

	@Override
	public void add(Customer customer) throws SQLException {
		String sql = "INSERT INTO customers (name, email, phone, address) VALUES (?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, customer.getName());
			stmt.setString(2, customer.getEmail());
			stmt.setString(3, customer.getPhone());
			stmt.setString(4, customer.getAddress());
			stmt.executeUpdate();

			try (ResultSet keys = stmt.getGeneratedKeys()) {
				if (keys.next()) {
					customer.setId(keys.getInt(1));
				}
			}
		}
	}

	@Override
	public Customer getById(int id) throws SQLException {
		String sql = "SELECT * FROM customers WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapCustomer(rs) : null;
			}
		}
	}

	@Override
	public List<Customer> getAll() throws SQLException {
		String sql = "SELECT * FROM customers ORDER BY id";
		List<Customer> customers = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				customers.add(mapCustomer(rs));
			}
		}
		return customers;
	}

	@Override
	public void update(Customer customer) throws SQLException {
		String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, customer.getName());
			stmt.setString(2, customer.getEmail());
			stmt.setString(3, customer.getPhone());
			stmt.setString(4, customer.getAddress());
			stmt.setInt(5, customer.getId());
			stmt.executeUpdate();
		}
	}

	@Override
	public void delete(int id) throws SQLException {
		String sql = "DELETE FROM customers WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
	}

	public Customer getByEmail(String email) throws SQLException {
		String sql = "SELECT * FROM customers WHERE email = ?";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapCustomer(rs) : null;
			}
		}
	}

	public List<Customer> searchByName(String keyword) throws SQLException {
		String sql = "SELECT * FROM customers WHERE name LIKE ? ORDER BY id";
		List<Customer> customers = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, "%" + keyword + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					customers.add(mapCustomer(rs));
				}
			}
		}
		return customers;
	}

	private Customer mapCustomer(ResultSet rs) throws SQLException {
		return new Customer(
				rs.getInt("id"),
				rs.getString("name"),
				rs.getString("email"),
				rs.getString("phone"),
				rs.getString("address")
		);
	}
}
