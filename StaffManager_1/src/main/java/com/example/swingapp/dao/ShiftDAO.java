package com.example.swingapp.dao;

import com.example.swingapp.model.Shift;
import com.example.swingapp.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftDAO implements BaseDAO<Shift> {

    @Override
    public boolean insert(Shift s) {
        String sql = "INSERT INTO tbl_shift(shift_name, start_time, end_time) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getShiftName());
            ps.setTime(2, s.getStartTime());
            ps.setTime(3, s.getEndTime());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Shift s) {
        String sql = "UPDATE tbl_shift SET shift_name=?, start_time=?, end_time=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getShiftName());
            ps.setTime(2, s.getStartTime());
            ps.setTime(3, s.getEndTime());
            ps.setInt(4, s.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tbl_shift WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Shift> getAll() {
        List<Shift> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_shift";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Shift s = new Shift(
                        rs.getInt("id"),
                        rs.getString("shift_name"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time")
                );
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
