package com.example.swingapp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.Shift;
import com.example.swingapp.util.DBConnection;

public class ShiftDAO implements BaseDAO<Shift> {

	@Override
	public boolean insert(Shift s) {
		var sql = "INSERT INTO tbl_shift(shift_name, start_time, end_time) VALUES (?, ?, ?)";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setString(1, s.getShiftName());
			ps.setTime(2, s.getStartTime());
			ps.setTime(3, s.getEndTime());
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(Shift s) {
		var sql = "UPDATE tbl_shift SET shift_name=?, start_time=?, end_time=? WHERE id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setString(1, s.getShiftName());
			ps.setTime(2, s.getStartTime());
			ps.setTime(3, s.getEndTime());
			ps.setInt(4, s.getId());
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(int id) {
		var sql = "DELETE FROM tbl_shift WHERE id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setInt(1, id);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Shift> getAll() {
		List<Shift> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_shift";

		try (var conn = DBConnection.getConnection();
				var st = conn.createStatement();
				var rs = st.executeQuery(sql)) {

			while (rs.next()) {
				var s = new Shift(
						rs.getInt("id"),
						rs.getString("shift_name"),
						rs.getTime("start_time"),
						rs.getTime("end_time")
						);
				list.add(s);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	public Shift getById(int id) {
		var shift = new Shift();
		var sql = "SELECT * FROM tbl_shift where id = ?";

		try (var conn = DBConnection.getConnection();
				var st = conn.prepareStatement(sql);){
			st.setInt(1, id);
			var rs = st.executeQuery();
			while (rs.next()) {
				shift = new Shift(
						rs.getInt("id"),
						rs.getString("shift_name"),
						rs.getTime("start_time"),
						rs.getTime("end_time")
						);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return shift;
	}
}
