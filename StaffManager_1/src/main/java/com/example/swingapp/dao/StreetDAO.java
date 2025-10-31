package com.example.swingapp.dao;

import com.example.swingapp.model.Street;
import com.example.swingapp.util.DBConnection;

import java.sql.*;
import java.util.*;

public class StreetDAO implements BaseDAO<Street> {

    @Override
    public boolean insert(Street s) {
        String sql = "INSERT INTO tbl_street(street_name) VALUES(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStreetName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Street s) {
        String sql = "UPDATE tbl_street SET street_name=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getStreetName());
            ps.setInt(2, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tbl_street WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Street> getAll() {
        List<Street> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_street";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Street(rs.getInt("id"), rs.getString("street_name")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
