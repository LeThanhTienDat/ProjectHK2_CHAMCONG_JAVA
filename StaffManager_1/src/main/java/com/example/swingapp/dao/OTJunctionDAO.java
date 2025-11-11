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
			ps.setString(5, o.getOtConfirm());
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
			ps.setString(3, o.getOtConfirm());
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
						rs.getString("ot_confirm")
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
						rs.getString("ot_confirm")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean checkInOt(int workScheduleId, int otTypeId) {
		var workScheduleService = new WorkScheduleService();
		var info = workScheduleService.getWorkDate(workScheduleId);
		if (info == null) {
			return false;
		}
		var otInfo = this.getOtTime(otTypeId);

		var workDate = (java.sql.Date) info[0];
		var inTime = otInfo.getOtStart();
		var checkInTime = workDate.toLocalDate().atTime(inTime.toLocalTime());
		System.out.println("Kiểm tra checkInType: "+checkInTime);
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
		var info = workScheduleService.getWorkDate(workScheduleId);
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
	public List<Object[]> getFullOtByWorkScheduleId(int workScheduleId){
		List<Object[]> list = new ArrayList<>();
		var sql =
				"""
				SELECT 	o.*,
						ot.ot_name,
						ot.ot_start,
						ot.ot_end,
						ot.active
				FROM tbl_Ot_Junction o
				join tbl_Ot_Type ot on ot.id = o.ot_type_id
				where o.work_schedule_id = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setInt(1, workScheduleId);
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(new Object[] {
							rs.getInt("id"),
							rs.getInt("work_schedule_id"),
							rs.getInt("ot_type_id"),
							rs.getTimestamp("ot_check_in_time"),
							rs.getTimestamp("ot_check_out_time"),
							rs.getString("ot_name"),
							rs.getTime("ot_start"),
							rs.getTime("ot_end"),
							rs.getInt("active")
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	public List<Object[]> getAllOtRecordsForMonth(int year, int month){
		List<Object[]> list = new ArrayList<>();
		var sql =
				"""
				SELECT 	o.*,
						ot.ot_name,
						ot.ot_start,
						ot.ot_end,
						ot.active,
						ws.employee_id,
						ws.work_date
				FROM tbl_Ot_Junction o
				left join tbl_Ot_Type ot on ot.id = o.ot_type_id
				left join tbl_Work_Schedule ws on o.work_schedule_id = ws.id
				where YEAR(ws.work_date) = ? and MONTH(work_date) = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

			ps.setInt(1, year);
			ps.setInt(2,month);
			try (var rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(new Object[] {
							rs.getInt("id"),
							rs.getInt("work_schedule_id"),
							rs.getInt("ot_type_id"),
							rs.getTimestamp("ot_check_in_time"),
							rs.getTimestamp("ot_check_out_time"),
							rs.getString("ot_name"),
							rs.getTime("ot_start"),
							rs.getTime("ot_end"),
							rs.getInt("active"),
							rs.getInt("employee_id"),
							rs.getDate("work_date")
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean confirmOt(int otJunctionId) {
		var sql = """
					Update tbl_Ot_Junction
					set ot_confirm = 'confirmed'
					where id = ?
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql);
				){
			ps.setInt(1, otJunctionId);
			var rs=ps.executeUpdate();
			return rs >0;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean rejectOt(int otJunctionId) {
		var sql = """
					Update tbl_Ot_Junction
					set ot_confirm = 'rejected'
					where id = ?
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql);
				){
			ps.setInt(1, otJunctionId);
			var rs=ps.executeUpdate();
			return rs >0;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Object[]> getOtConfirmList(String keyword, int restaurantId, String date){
		List<Object[]> list = new ArrayList();
		var sql = "{CALL SP_GetOtConfirmList (?,?,?)}";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareCall(sql);
				){
			ps.setString(1,keyword);
			ps.setInt(2, restaurantId);
			ps.setString(3, date);

			var rs = ps.executeQuery();
			while(rs.next()) {
				var item = new Object[15];
				item[0] = rs.getInt("restaurant_id");
				item[1] =rs.getString("restaurant_name");
				item[2] =rs.getInt("employee_id");
				item[3] =rs.getString("employee_name");
				item[4] =rs.getString("employee_phone");
				item[5] =rs.getInt("work_schedule_id");
				item[6] =rs.getDate("work_date");
				item[7] =rs.getInt("ot_junction_id");
				item[8] =rs.getTimestamp("ot_check_in_time");
				item[9] =rs.getTimestamp("ot_check_out_time");
				item[10] =rs.getString("ot_confirm");
				item[11] =rs.getInt("ot_type_id");
				item[12] =rs.getString("ot_name");
				item[13] =rs.getTime("ot_start");
				item[14] =rs.getTime("ot_end");
				list.add(item);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Object[]> getAllOtConfirmList(String from, String to){
		List<Object[]> list = new ArrayList();
		var sql = "{CALL SP_GetAllOtConfirmList (?,?)}";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareCall(sql);
				){
			ps.setString(1,from);
			ps.setString(2, to);

			var rs = ps.executeQuery();
			while(rs.next()) {
				var item = new Object[15];
				item[0] = rs.getInt("restaurant_id");
				item[1] =rs.getString("restaurant_name");
				item[2] =rs.getInt("employee_id");
				item[3] =rs.getString("employee_name");
				item[4] =rs.getString("employee_phone");
				item[5] =rs.getInt("work_schedule_id");
				item[6] =rs.getDate("work_date");
				item[7] =rs.getInt("ot_junction_id");
				item[8] =rs.getTimestamp("ot_check_in_time");
				item[9] =rs.getTimestamp("ot_check_out_time");
				item[10] =rs.getString("ot_confirm");
				item[11] =rs.getInt("ot_type_id");
				item[12] =rs.getString("ot_name");
				item[13] =rs.getTime("ot_start");
				item[14] =rs.getTime("ot_end");
				list.add(item);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
