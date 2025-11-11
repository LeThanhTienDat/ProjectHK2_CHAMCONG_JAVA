package com.example.swingapp.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.AttendanceLockStatus;
import com.example.swingapp.util.DBConnection;

public class AttendanceLockStatusDAO implements BaseDAO<AttendanceLockStatus> {

	@Override
	public boolean insert(AttendanceLockStatus a) {
		return false;
	}

	@Override
	public boolean update(AttendanceLockStatus a) {
		return false;
	}

	@Override
	public boolean delete(int id) {
		return false;
	}

	@Override
	public List<AttendanceLockStatus> getAll() {
		List<AttendanceLockStatus> list = new ArrayList<>();
		return list;
	}
	public AttendanceLockStatus getLast() {
		var item = new AttendanceLockStatus();
		var sql = """
				    SELECT TOP 1 a.*
				    FROM tbl_Attendance_lock_status a
				    ORDER BY a.locked_year DESC, a.locked_month DESC
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)){
			var rs = ps.executeQuery();
			while(rs.next()) {
				item.setId(rs.getInt("id"));
				item.setLockedMonth(rs.getInt("locked_month"));
				item.setLockedYear(rs.getInt("locked_year"));
				item.setLockedAt(rs.getDate("locked_at"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return item;
	}
	public boolean lockMonth(int month, int year) {
		var sql = """
					insert into tbl_Attendance_lock_status(locked_month, locked_year,locked_at)
					values
					(?,?,?)
				""";
		var now = LocalDateTime.now();
		var timestamp = Timestamp.valueOf(now);
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)){
			ps.setInt(1, month);
			ps.setInt(2,year);
			ps.setTimestamp(3, timestamp);
			var rs = ps.executeUpdate();
			return rs > 0;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}


}
