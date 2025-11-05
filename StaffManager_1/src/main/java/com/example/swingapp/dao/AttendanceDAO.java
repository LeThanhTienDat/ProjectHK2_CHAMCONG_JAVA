package com.example.swingapp.dao;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.example.swingapp.model.WorkDetail;
import com.example.swingapp.model.WorkSchedule;
import com.example.swingapp.util.DBConnection;

public class AttendanceDAO {

	public List<Object[]> loadBasicEmployeeData() {
		List<Object[]> rows = new ArrayList<>();

		var sql = """
				SELECT
				    e.id AS emp_id,
				    e.name AS emp_name,
				    r.id as restaurant_id,
				    e.role,
				    ISNULL(r.name, 'Chưa phân công') AS restaurant_name
				FROM dbo.tbl_Employee e
				LEFT JOIN dbo.tbl_Employee_Restaurant er ON e.id = er.employee_id and er.active = 'True'
				LEFT JOIN dbo.tbl_Restaurant r ON er.restaurant_id = r.id
				ORDER BY e.id
				""";

		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql);
				var rs = ps.executeQuery()) {

			while (rs.next()) {
				var row = new Object[7];
				row[0] = rs.getInt("emp_id");
				row[1] = rows.size() + 1;
				row[2] = "NV" + String.format("%05d", rs.getInt("emp_id"));
				row[3] = rs.getString("emp_name");
				row[4] = rs.getString("role");
				row[5] = rs.getString("restaurant_name");
				row[6] = rs.getInt("restaurant_id");
				rows.add(row);
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu: " + ex.getMessage());
			ex.printStackTrace();
		}

		return rows;
	}

	public List<String> getShiftsForEmployee(String employeeName, String date) {
		List<String> shifts = new ArrayList<>();

		var sql = """
				    SELECT s.shift_name, s.start_time, s.end_time
				    FROM tbl_Work_Schedule ws
				    JOIN tbl_Employee e ON ws.employee_id = e.id
				    JOIN tbl_Shift s ON ws.shift_id = s.id
				    WHERE e.name = ? AND ws.work_date = ?
				""";

		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setString(1, employeeName);
			ps.setDate(2, java.sql.Date.valueOf(date));

			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					var shiftName = rs.getString("shift_name");
					var startTime = rs.getTime("start_time");
					var endTime = rs.getTime("end_time");

					var formatted = shiftName + " (" +
							(startTime != null ? startTime.toLocalTime().toString() : "N/A") + " - " +
							(endTime != null ? endTime.toLocalTime().toString() : "N/A") + ")";

					shifts.add(formatted);
				}
			}

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Lỗi khi lấy ca làm: " + ex.getMessage());
			ex.printStackTrace();
		}

		return shifts;
	}
	public WorkDetail getWorkDetail(String employeeName, String date, String shiftName) {
		WorkDetail detail = null;
		var sql = """
				    SELECT ws.check_in_time, ws.check_out_time,
				           ws.come_late, ws.early_leave, ws.time_work, ws.total_ot
				    FROM tbl_Work_Schedule ws
				    JOIN tbl_Employee e ON e.id = ws.employee_id
				    JOIN tbl_Shift s ON s.id = ws.shift_id
				    WHERE e.name = ? AND ws.work_date = ? AND s.shift_name = ?
				""";

		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setString(1, employeeName);
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setString(3, shiftName);

			try (var rs = ps.executeQuery()) {
				if (rs.next()) {
					detail = new WorkDetail(
							rs.getString("check_in_time"),
							rs.getString("check_out_time"),
							rs.getInt("come_late"),
							rs.getInt("early_leave"),
							rs.getFloat("time_work"),
							rs.getFloat("total_ot")
							);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return detail;
	}

	public List<java.time.LocalDate> getWorkedDays(int employeeId, int year, int month) {
		List<java.time.LocalDate> days = new ArrayList<>();

		var sql = """
				    SELECT work_date
				    FROM tbl_Work_Schedule
				    WHERE employee_id = ?
				      AND YEAR(work_date) = ?
				      AND MONTH(work_date) = ?
				      AND check_in_time IS NOT NULL
				      AND check_out_time IS NOT NULL
				""";

		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);
			ps.setInt(2, year);
			ps.setInt(3, month);

			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					days.add(rs.getDate("work_date").toLocalDate());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return days;
	}

	public List<Object[]> getAllWorkSchedules(int year, int month) {
		List<Object[]> list = new ArrayList<>();

		var sql = """
				    SELECT
				        employee_id,
				        work_date,
				        come_late,
				        early_leave,
				        time_work,
				        check_in_time,
				        check_out_time
				    FROM tbl_Work_Schedule
				    WHERE YEAR(work_date) = ? AND MONTH(work_date) = ?
				""";

		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setInt(1, year);
			ps.setInt(2, month);

			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(new Object[] {
							rs.getInt("employee_id"),
							rs.getDate("work_date").toLocalDate(),
							rs.getInt("come_late"),
							rs.getInt("early_leave"),
							rs.getInt("time_work"),
							rs.getTimestamp("check_in_time"),
							rs.getTimestamp("check_out_time")
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	public int hasWorkSchedule(int employeeId, String currentDate) {
		var id = 0;
		var sql = """
					select ws.id
					from tbl_Work_Schedule ws
					where employee_id = ? and work_date = ?
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);
			ps.setString(2, currentDate);
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					id = rs.getInt("id");
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public int hasShiftId(int employeeId, String currentDate) {
		var id = 0;
		var sql = """
					select ws.id
					from tbl_Work_Schedule ws
					where employee_id = ? and work_date = ? and (shift_id != null or shift_id != 0)
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);
			ps.setString(2, currentDate);
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					id = rs.getInt("id");
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		return id;
	}



	public WorkSchedule getWorkScheduleByIdDate(int employeeId, String currentDate) {
		var item = new WorkSchedule();
		var sql = """
					select ws.*
					from tbl_Work_Schedule ws
					where employee_id = ? and work_date = ?
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, employeeId);
			ps.setString(2, currentDate);
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					item.setId(rs.getInt("id"));
					item.setEmployeeId(rs.getInt("employee_id"));
					item.setShiftId(rs.getInt("shift_id"));
					item.setWorkDate(rs.getDate("work_date"));
					item.setGetComeLate(rs.getInt("come_late"));
					item.setGetEarlyLeave(rs.getInt("early_leave"));
					item.setAbsentId(rs.getInt("absent_id"));
					item.setTimeWork(rs.getInt("time_work"));
					item.setTotalOt(rs.getInt("total_ot"));
					item.setCheckInTime(rs.getTime("check_in_time"));
					item.setCheckOutTime(rs.getTime("check_out_time"));
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		return item;
	}







}
