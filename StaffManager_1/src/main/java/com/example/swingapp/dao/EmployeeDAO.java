package com.example.swingapp.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.Employee;
import com.example.swingapp.util.DBConnection;

public class EmployeeDAO implements BaseDAO<Employee> {

	@Override
	public boolean insert(Employee e) {
		var sql = "INSERT INTO tbl_employee(name, role, phone, email, dob, gender, active) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setString(1, e.getName());
			ps.setString(2, e.getRole());
			ps.setString(3, e.getPhone());
			ps.setString(4, e.getEmail());
			ps.setDate(5, new Date(e.getDob().getTime()));
			ps.setString(6, e.getGender());
			ps.setInt(7, e.getActive());

			return ps.executeUpdate() > 0;

		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(Employee e) {
		var sql = "UPDATE tbl_employee SET name=?, role=?, phone=?, email=?, dob=?, gender=?, active=? WHERE id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setString(1, e.getName());
			ps.setString(2, e.getRole());
			ps.setString(3, e.getPhone());
			ps.setString(4, e.getEmail());
			ps.setDate(5, new Date(e.getDob().getTime()));
			ps.setString(6, e.getGender());
			ps.setInt(7, e.getActive());
			ps.setInt(8, e.getId());

			return ps.executeUpdate() > 0;

		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(int id) {
		var sql = """
					Update tbl_Employee
					set active = 0
					where id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Employee> getAll() {
		List<Employee> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_Employee";
		try (var conn = DBConnection.getConnection();
				var st = conn.createStatement();
				var rs = st.executeQuery(sql)) {

			while (rs.next()) {
				var e = new Employee(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("role"),
						rs.getString("phone"),
						rs.getString("email"),
						rs.getDate("dob"),
						rs.getString("gender"),
						rs.getInt("active")
						);
				list.add(e);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public List<Employee> getByRestaurantId(int id) {
		List<Employee> list = new ArrayList<>();
		var sql = """
				SELECT e.*
				FROM tbl_Employee e
				JOIN tbl_Employee_Restaurant er ON er.employee_id = e.id
				JOIN tbl_Restaurant r ON r.id = er.restaurant_id
				WHERE er.restaurant_id = ?
				ORDER BY e.name
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			var rs = ps.executeQuery();
			while (rs.next()) {
				var e = new Employee(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("role"),
						rs.getString("phone"),
						rs.getString("email"),
						rs.getDate("dob"),
						rs.getString("gender"),
						rs.getInt("active")
						);
				list.add(e);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return list;
	}
	public Employee getById(int id) {
		var sql = "SELECT * FROM tbl_employee WHERE id = ?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			var rs = ps.executeQuery();
			if (rs.next()) {
				return new Employee(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("role"),
						rs.getString("phone"),
						rs.getString("email"),
						rs.getDate("dob"),
						rs.getString("gender"),
						rs.getInt("active")
						);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getEmployeeIdByName(String name) {
		var sql = "SELECT Id FROM tbl_Employee WHERE Name = ?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setString(1, name);
			var rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public boolean checkPhone(String phone) {
		var sql = """
				select 1
				            from tbl_Employee e
				            where e.phone = ?
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)){
			ps.setString(1,phone);
			var rs = ps.executeQuery();
			while(rs.next()) {
				return true;
			}
		}catch(Exception e) {
			System.err.println("Database error occurred while checking phone: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	public boolean hasActiveContract(int employeeId) {
		var sql = """
					select top 1 1
					from tbl_Employee e
					join tbl_Contract c on c.employee_id = e.id
					where e.id = ? and c.status = 'Active'
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)){
			ps.setInt(1,employeeId);
			var rs = ps.executeQuery();
			while(rs.next()) {
				return true;
			}
		}catch(Exception e) {
			System.err.println("Database error occurred while checking phone: " + e.getMessage());
			e.printStackTrace();
		}


		return false;
	}
}
