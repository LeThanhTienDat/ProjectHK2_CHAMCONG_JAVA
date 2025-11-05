package com.example.swingapp.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.WorkSchedule;
import com.example.swingapp.util.DBConnection;

public class WorkScheduleDAO implements BaseDAO<WorkSchedule> {

	@Override
	public boolean insert(WorkSchedule w) {
		var sql =
				"""
				INSERT INTO tbl_work_schedule(employee_id, shift_id, work_date)
				VALUES (?, ?, ?)""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, w.getEmployeeId());
			ps.setInt(2, w.getShiftId());
			ps.setDate(3, w.getWorkDate());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(WorkSchedule w) {
		var sql =
				"""
				UPDATE tbl_work_schedule SET \
				shift_id=?, work_date=?, come_late=?, early_leave=?, absent_id=?, \
				time_work=?, total_ot=?, check_in_time=?, check_out_time=? \
				WHERE id=?""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
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
		var sql = "DELETE FROM tbl_work_schedule WHERE id=?";
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
	public List<WorkSchedule> getAll() {
		List<WorkSchedule> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_work_schedule";
		try (var conn = DBConnection.getConnection();
				var st = conn.createStatement();
				var rs = st.executeQuery(sql)) {
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


	public int addAndReturnId(WorkSchedule ws) {
		var sql = """
				INSERT INTO tbl_Work_Schedule(employee_id, shift_id, work_date)
				VALUES (?, ?, ?)
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, ws.getEmployeeId());
			if(ws.getShiftId() != null) {
				ps.setInt(2, ws.getShiftId());
			} else {
				ps.setNull(2, java.sql.Types.INTEGER); // quan trọng: setNull với OT
			}
			ps.setDate(3, ws.getWorkDate());


			var affected = ps.executeUpdate();
			if (affected == 0) {
				return -1;
			}

			try (var rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return -1;
	}

	public WorkSchedule getById(int id) {
		var sql = "SELECT * FROM tbl_work_schedule WHERE id = ?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (var rs = ps.executeQuery()) {
				if (rs.next()) {
					return new WorkSchedule(
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
							);
				}
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}
	public boolean updateShift(int workScheduleId, int shiftId) {
		var sql =
				"""
				UPDATE tbl_work_schedule
				SET shift_id = ?
				where id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, shiftId);
			ps.setInt(2, workScheduleId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkInShift(int workScheduleId) {
		var info = getWorkDateAndTime(workScheduleId);
		if (info == null) {
			return false;
		}
		var workDate = (java.sql.Date) info[0];
		var startTime = (java.sql.Time) info[2];
		var checkInTime = workDate.toLocalDate().atTime(startTime.toLocalTime());
		var sql =
				"""
				UPDATE tbl_work_schedule
				SET check_in_time = ?
				where id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setTimestamp(1, java.sql.Timestamp.valueOf(checkInTime));
			ps.setInt(2, workScheduleId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean checkOutShift(int workScheduleId) {
		var info = getWorkDateAndTime(workScheduleId);
		if (info == null) {
			return false;
		}
		var workDate = (java.sql.Date) info[0];
		var endTime = (java.sql.Time) info[3];
		var checkOutTime = workDate.toLocalDate().atTime(endTime.toLocalTime());
		var sql =
				"""
				UPDATE tbl_work_schedule
				SET check_out_time = ?
				where id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setTimestamp(1, java.sql.Timestamp.valueOf(checkOutTime));
			ps.setInt(2, workScheduleId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public Object[] getWorkDateAndTime(int workScheduleId) {
		var sql = """
				    SELECT ws.work_date, s.shift_name, s.start_time, s.end_time
				    FROM tbl_Work_Schedule ws
				    JOIN tbl_Shift s ON ws.shift_id = s.id
				    WHERE ws.id = ?
				""";
		try (
				var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql);
				) {
			ps.setInt(1, workScheduleId);
			var rs = ps.executeQuery();

			if (rs.next()) {
				var getShiftInfo = new Object[] {
						rs.getDate("work_date"),
						rs.getString("shift_name"),
						rs.getTime("start_time"),
						rs.getTime("end_time")
				};
				return getShiftInfo;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
