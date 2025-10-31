package com.example.swingapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.MonthlySummary;
import com.example.swingapp.util.DBConnection;

public class MonthlySummaryDAO implements BaseDAO<MonthlySummary> {

	@Override
	public boolean insert(MonthlySummary m) {
		String sql =
				"INSERT INTO tbl_monthly_summary(" +
						"employee_id, total_shift, bonus, final_salary, status, " +
						"month, year, total_come_late, total_early_leave, " +
						"total_over_time, total_work_time, al_used, " +
						"total_work_salary, total_ot_salary) " +
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, m.getEmployeeId());
			ps.setInt(2, m.getTotalShift());
			ps.setDouble(3, m.getBonus());
			ps.setDouble(4, m.getFinalSalary());
			ps.setString(5, m.getStatus());
			ps.setInt(6, m.getMonth());
			ps.setInt(7, m.getYear());
			ps.setInt(8, m.getTotalComeLate());
			ps.setInt(9, m.getTotalEarlyLeave());
			ps.setDouble(10, m.getTotalOverTime());
			ps.setDouble(11, m.getTotalWorkTime());
			ps.setDouble(12, m.getAlUsed());
			ps.setDouble(13, m.getTotalWorkSalary());
			ps.setDouble(14, m.getTotalOtSalary());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(MonthlySummary m) {
		String sql = "UPDATE tbl_monthly_summary SET final_salary=?, status=? WHERE id=?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setDouble(1, m.getFinalSalary());
			ps.setString(2, m.getStatus());
			ps.setInt(3, m.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(int id) {
		String sql = "DELETE FROM tbl_monthly_summary WHERE id=?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<MonthlySummary> getAll() {
		List<MonthlySummary> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_monthly_summary";
		try (Connection conn = DBConnection.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new MonthlySummary(
						rs.getInt("id"),
						rs.getInt("employee_id"),
						rs.getInt("total_shift"),
						rs.getDouble("bonus"),
						rs.getDouble("final_salary"),
						rs.getString("status"),
						rs.getInt("month"),
						rs.getInt("year"),
						rs.getInt("total_come_late"),
						rs.getInt("total_early_leave"),
						rs.getDouble("total_over_time"),
						rs.getDouble("total_work_time"),
						rs.getDouble("al_used"),
						rs.getDouble("total_work_salary"),
						rs.getDouble("total_ot_salary")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
