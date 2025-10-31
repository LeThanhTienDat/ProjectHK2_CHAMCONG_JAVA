package com.example.swingapp.dao;

import com.example.swingapp.model.EmployeeRestaurant;
import com.example.swingapp.util.DBConnection;

import java.sql.*;
import java.util.*;

public class EmployeeRestaurantDAO implements BaseDAO<EmployeeRestaurant> {

    @Override
    public boolean insert(EmployeeRestaurant e) {
        String sql = "INSERT INTO tbl_employee_restaurant(employee_id, restaurant_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getEmployeeId());
            ps.setInt(2, e.getRestaurantId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    @Override
    public boolean update(EmployeeRestaurant e) {
        String sql = "UPDATE tbl_employee_restaurant SET employee_id=?, restaurant_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getEmployeeId());
            ps.setInt(2, e.getRestaurantId());
            ps.setInt(3, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tbl_employee_restaurant WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    @Override
    public List<EmployeeRestaurant> getAll() {
        List<EmployeeRestaurant> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_employee_restaurant";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new EmployeeRestaurant(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getInt("restaurant_id")
                ));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }
}
