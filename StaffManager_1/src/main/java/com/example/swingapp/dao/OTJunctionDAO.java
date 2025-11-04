package com.example.swingapp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.OTJunction;
import com.example.swingapp.util.DBConnection;

public class OTJunctionDAO implements BaseDAO<OTJunction> {

	@Override
	public boolean insert(OTJunction o) {
		var sql =
				"""
				INSERT INTO tbl_ot_junction(\
				work_schedule_id, ot_type_id, ot_check_in_time, ot_check_out_time, ot_confirm\
				) VALUES (?, ?, ?, ?, ?)""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, o.getWorkScheduleId());
			ps.setInt(2, o.getOtTypeId());
			ps.setTime(3, o.getOtCheckInTime());
			ps.setTime(4, o.getOtCheckOutTime());
			ps.setBoolean(5, o.isOtConfirm());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(OTJunction o) {
		var sql = "UPDATE tbl_ot_junction SET ot_check_in_time=?, ot_check_out_time=?, ot_confirm=? WHERE id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setTime(1, o.getOtCheckInTime());
			ps.setTime(2, o.getOtCheckOutTime());
			ps.setBoolean(3, o.isOtConfirm());
			ps.setInt(4, o.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(int id) {
		var sql = "DELETE FROM tbl_ot_junction WHERE id=?";
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
	public List<OTJunction> getAll() {
		List<OTJunction> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_ot_junction";
		try (var conn = DBConnection.getConnection();
				var st = conn.createStatement();
				var rs = st.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new OTJunction(
						rs.getInt("id"),
						rs.getInt("work_schedule_id"),
						rs.getInt("ot_type_id"),
						rs.getTime("ot_check_in_time"),
						rs.getTime("ot_check_out_time"),
						rs.getBoolean("ot_confirm")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	public List<OTJunction> getByWorkScheduleId(int id){
		List<OTJunction> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_ot_junction where work_schedule_id = ?";
		try (var conn = DBConnection.getConnection();
				var st = conn.prepareStatement(sql);){
			st.setInt(1, id);
			var rs = st.executeQuery();
			while (rs.next()) {
				list.add(new OTJunction(
						rs.getInt("id"),
						rs.getInt("work_schedule_id"),
						rs.getInt("ot_type_id"),
						rs.getTime("ot_check_in_time"),
						rs.getTime("ot_check_out_time"),
						rs.getBoolean("ot_confirm")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
