package com.example.swingapp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.OTType;
import com.example.swingapp.util.DBConnection;

public class OTTypeDAO implements BaseDAO<OTType> {

	@Override
	public boolean insert(OTType o) {
		var sql = "INSERT INTO tbl_ot_type(ot_name, ot_start, ot_end) VALUES (?, ?, ?)";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setString(1, o.getOtName());
			ps.setTime(2, o.getOtStart());
			ps.setTime(3, o.getOtEnd());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

	@Override
	public boolean update(OTType o) {
		var sql = "UPDATE tbl_ot_type SET ot_name=?, ot_start=?, ot_end=? WHERE id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setString(1, o.getOtName());
			ps.setTime(2, o.getOtStart());
			ps.setTime(3, o.getOtEnd());
			ps.setInt(4, o.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

	@Override
	public boolean delete(int id) {
		var sql = """
				Update tbl_Ot_Type \
				set active = 0\
				WHERE id=?""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

	@Override
	public List<OTType> getAll() {
		List<OTType> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_ot_type where active = 1";
		try (var conn = DBConnection.getConnection();
				var st = conn.createStatement();
				var rs = st.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new OTType(
						rs.getInt("id"),
						rs.getString("ot_name"),
						rs.getTime("ot_start"),
						rs.getTime("ot_end")
						));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return list;
	}
	public OTType getById(int id) {
		var sql = "SELECT * FROM tbl_ot_type WHERE id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (var rs = ps.executeQuery()) {
				if (rs.next()) {
					return new OTType(
							rs.getInt("id"),
							rs.getString("ot_name"),
							rs.getTime("ot_start"),
							rs.getTime("ot_end")
							);
				}
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return null;
	}
}
