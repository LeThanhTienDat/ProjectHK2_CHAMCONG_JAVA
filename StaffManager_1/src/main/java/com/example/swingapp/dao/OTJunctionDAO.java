package com.example.swingapp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.OTJunction;
import com.example.swingapp.model.OTType;
import com.example.swingapp.service.WorkScheduleService;
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

	public boolean checkInOt(int workScheduleId, int otTypeId) {
		var workScheduleService = new WorkScheduleService();
		var info = workScheduleService.getWorkDateAndTime(workScheduleId);
		if (info == null) {
			return false;
		}
		var otInfo = this.getOtTime(otTypeId);

		var workDate = (java.sql.Date) info[0];
		var inTime = otInfo.getOtStart();
		var checkInTime = workDate.toLocalDate().atTime(inTime.toLocalTime());
		var sql =
				"""
				UPDATE tbl_Ot_Junction
				SET ot_check_in_time = ?
				where work_schedule_id = ? and ot_type_id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setTimestamp(1, java.sql.Timestamp.valueOf(checkInTime));
			ps.setInt(2, workScheduleId);
			ps.setInt(3, otTypeId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean checkOutOt(int workScheduleId, int otTypeId) {
		var workScheduleService = new WorkScheduleService();
		var info = workScheduleService.getWorkDateAndTime(workScheduleId);
		if (info == null) {
			return false;
		}
		var otInfo = this.getOtTime(otTypeId);

		var workDate = (java.sql.Date) info[0];
		var outTime = otInfo.getOtEnd();
		var checkOutTime = workDate.toLocalDate().atTime(outTime.toLocalTime());
		var sql =
				"""
				UPDATE tbl_Ot_Junction
				SET ot_check_out_time = ?
				where work_schedule_id = ? and ot_type_id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setTimestamp(1, java.sql.Timestamp.valueOf(checkOutTime));
			ps.setInt(2, workScheduleId);
			ps.setInt(3, otTypeId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public OTType getOtTime(int ot_type_id) {
		var otTime = new OTType();
		var sql =
				"""
				SELECT o.*
				FROM tbl_Ot_Type o
				where id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, ot_type_id);
			var rs = ps.executeQuery();
			while (rs.next()) {
				otTime.setId(rs.getInt("id"));
				otTime.setOtName(rs.getString("ot_name"));
				otTime.setOtStart(rs.getTime("ot_start"));
				otTime.setOtEnd(rs.getTime("ot_end"));
			}
			return otTime;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
