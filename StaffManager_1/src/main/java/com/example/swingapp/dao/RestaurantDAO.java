package com.example.swingapp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.Restaurant;
import com.example.swingapp.util.DBConnection;

public class RestaurantDAO implements BaseDAO<Restaurant> {

	@Override
	public boolean insert(Restaurant r) {
		var sql = "INSERT INTO tbl_restaurant(name, street_id) VALUES(?, ?)";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setString(1, r.getName());
			ps.setInt(2, r.getStreetId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

	@Override
	public boolean update(Restaurant r) {
		var sql = "UPDATE tbl_restaurant SET name=?, street_id=? WHERE id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setString(1, r.getName());
			ps.setInt(2, r.getStreetId());
			ps.setInt(3, r.getId());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

	@Override
	public boolean delete(int id) {
		var sql = "DELETE FROM tbl_restaurant WHERE id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) { e.printStackTrace(); return false; }
	}

	@Override
	public List<Restaurant> getAll() {
		List<Restaurant> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_Restaurant";
		try (var conn = DBConnection.getConnection();
				var st = conn.createStatement();
				var rs = st.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Restaurant(
						rs.getInt("id"),
						rs.getString("name"),
						rs.getInt("street_id")));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return list;
	}
}
