package com.example.swingapp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.Contract;
import com.example.swingapp.util.DBConnection;

public class ContractDAO implements BaseDAO<Contract> {

	@Override
	public boolean insert(Contract c) {
		System.out.println("[DEBUG] Insert contract:");
		System.out.println("Employee ID: " + c.getEmployeeId());
		System.out.println("Start Date: " + c.getStartDate());
		System.out.println("End Date: " + c.getEndDate());
		System.out.println("Salary: " + c.getSalary());
		System.out.println("Position: " + c.getPosition());
		System.out.println("Status: " + c.getStatus());


		System.out.println("[DEBUG] Insert contract:");
		System.out.println("Employee ID: " + c.getEmployeeId());
		var sql = "INSERT INTO tbl_contract(employee_id, start_date, end_date, salary, position, status) VALUES(?, ?, ?, ?, ?, ?)";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, c.getEmployeeId());
			ps.setDate(2, c.getStartDate());
			ps.setDate(3, c.getEndDate());
			ps.setDouble(4, c.getSalary());
			ps.setString(5, c.getPosition());
			ps.setString(6, c.getStatus());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("[ERROR] Insert failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(Contract c) {
		var sql = "UPDATE tbl_contract SET start_date=?, end_date=?, salary=?, position=?, status=? WHERE contract_id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setDate(1, c.getStartDate());
			ps.setDate(2, c.getEndDate());
			ps.setDouble(3, c.getSalary());
			ps.setString(4, c.getPosition());
			ps.setString(5, c.getStatus());
			ps.setInt(6, c.getContractId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

	@Override
	public boolean delete(int id) {
		var sql = "DELETE FROM tbl_contract WHERE contract_id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

	@Override
	public List<Contract> getAll() {
		List<Contract> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_contract";
		try (var conn = DBConnection.getConnection();
				var st = conn.createStatement();
				var rs = st.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Contract(
						rs.getInt("contract_id"),
						rs.getInt("employee_id"),
						rs.getDate("start_date"),
						rs.getDate("end_date"),
						rs.getDouble("salary"),
						rs.getString("position"),
						rs.getString("status")
						));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return list;
	}
	public boolean hasActiveContract(int id) {
		var sql = """
				    SELECT COUNT(*) AS count
				    FROM tbl_Contract
				    WHERE employee_id = ? AND status = 'Active'
				""";

		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (var rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("count") > 0;
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;

	}
}
