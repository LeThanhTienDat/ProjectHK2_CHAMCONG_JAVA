package com.example.swingapp.dao;

import com.example.swingapp.model.Absent;
import com.example.swingapp.util.DBConnection;

import java.sql.*;
import java.util.*;

public class AbsentDAO implements BaseDAO<Absent> {

    @Override
    public boolean insert(Absent a) {
        String sql = "INSERT INTO tbl_absent(absent_type, reason, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAbsentType());
            ps.setString(2, a.getReason());
            ps.setString(3, a.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Absent a) {
        String sql = "UPDATE tbl_absent SET absent_type=?, reason=?, description=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getAbsentType());
            ps.setString(2, a.getReason());
            ps.setString(3, a.getDescription());
            ps.setInt(4, a.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tbl_absent WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Absent> getAll() {
        List<Absent> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_absent";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Absent(
                        rs.getInt("id"),
                        rs.getString("absent_type"),
                        rs.getString("reason"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
