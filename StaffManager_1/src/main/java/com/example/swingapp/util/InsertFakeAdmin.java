package com.example.swingapp.util;

import com.example.swingapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class InsertFakeAdmin {
    public static void main(String[] args) {
        // Thay thông tin admin bạn muốn
        String username = "admin";
        String plainPassword = "123456"; // đổi nếu cần
        String auth = "ADMIN";
        boolean active = true;

        // Sinh salt + hash
        String salt = PasswordUtils.generateSalt();
        String hashed = PasswordUtils.hashPassword(plainPassword, salt);

        String sql = "INSERT INTO tbl_account(account_name, password, salt, active, employee_id, auth) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashed);
            ps.setString(3, salt);
            ps.setBoolean(4, active);

            // Đặt employee_id = NULL (admin không cần liên kết employee)
            ps.setNull(5, java.sql.Types.INTEGER);

            ps.setString(6, auth);

            int r = ps.executeUpdate();
            if (r > 0) {
                System.out.println("Admin inserted successfully: " + username);
            } else {
                System.out.println(" Insert failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
