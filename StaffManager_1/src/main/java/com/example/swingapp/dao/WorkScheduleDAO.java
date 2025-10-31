package com.example.swingapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.WorkSchedule;
import com.example.swingapp.util.DBConnection;

public class WorkScheduleDAO implements BaseDAO<WorkSchedule> {

	@Override
	public boolean insert(WorkSchedule w) {
		String sql =
				"INSERT INTO tbl_work_schedule(" +
						"employee_id, shift_id, work_date, come_late, early_leave, absent_id, " +
						"time_work, total_ot, check_in_time, check_out_time) " +
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, w.getEmployeeId());
			ps.setInt(2, w.getShiftId());
			ps.setDate(3, w.getWorkDate());
			ps.setBoolean(4, w.isComeLate());
			ps.setBoolean(5, w.isEarlyLeave());
			ps.setInt(6, w.getAbsentId());
			ps.setDouble(7, w.getTimeWork());
			ps.setDouble(8, w.getTotalOt());
			ps.setTime(9, w.getCheckInTime());
			ps.setTime(10, w.getCheckOutTime());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(WorkSchedule w) {
		String sql =
				"UPDATE tbl_work_schedule SET " +
						"shift_id=?, work_date=?, come_late=?, early_leave=?, absent_id=?, " +
						"time_work=?, total_ot=?, check_in_time=?, check_out_time=? " +
						"WHERE id=?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, w.getShiftId());
			ps.setDate(2, w.getWorkDate());
			ps.setBoolean(3, w.isComeLate());
			ps.setBoolean(4, w.isEarlyLeave());
			ps.setInt(5, w.getAbsentId());
			ps.setDouble(6, w.getTimeWork());
			ps.setDouble(7, w.getTotalOt());
			ps.setTime(8, w.getCheckInTime());
			ps.setTime(9, w.getCheckOutTime());
			ps.setInt(10, w.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(int id) {
		String sql = "DELETE FROM tbl_work_schedule WHERE id=?";
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
	public List<WorkSchedule> getAll() {
		List<WorkSchedule> list = new ArrayList<>();
		String sql = "SELECT * FROM tbl_work_schedule";
		try (Connection conn = DBConnection.getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new WorkSchedule(
						rs.getInt("id"),
						rs.getInt("employee_id"),
						rs.getInt("shift_id"),
						rs.getDate("work_date"),
						rs.getBoolean("come_late"),
						rs.getBoolean("early_leave"),
						rs.getInt("absent_id"),
						rs.getDouble("time_work"),
						rs.getDouble("total_ot"),
						rs.getTime("check_in_time"),
						rs.getTime("check_out_time")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
