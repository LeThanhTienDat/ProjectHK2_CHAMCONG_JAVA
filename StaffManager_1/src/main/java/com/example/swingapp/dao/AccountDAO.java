package com.example.swingapp.dao;

import com.example.swingapp.model.Account;
import com.example.swingapp.util.DBConnection;
import java.sql.*;
import java.util.*;

public class AccountDAO implements BaseDAO<Account> {

	@Override
	public boolean insert(Account a) {
	    String sql = "INSERT INTO tbl_account(account_name, password, salt, active, employee_id, auth) VALUES (?, ?, ?, ?, ?, ?)";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, a.getAccountName());
	        ps.setString(2, a.getPassword());
	        ps.setString(3, a.getSalt());
	        ps.setBoolean(4, a.isActive());

	        if (a.getEmployeeId() == 0) {
	            ps.setNull(5, java.sql.Types.INTEGER);
	        } else {
	            ps.setInt(5, a.getEmployeeId());
	        }

	        ps.setString(6, a.getAuth());
	        return ps.executeUpdate() > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


    @Override
    public boolean update(Account a) {
        String sql = "UPDATE tbl_account SET password=?, active=?, auth=? WHERE account_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getPassword());
            ps.setBoolean(2, a.isActive());
            ps.setString(3, a.getAuth());
            ps.setInt(4, a.getAccountId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tbl_account WHERE account_id=?";
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
    public List<Account> getAll() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM tbl_account";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Account(
                        rs.getInt("account_id"),
                        rs.getString("account_name"),
                        rs.getString("password"),
                        rs.getString("salt"),
                        rs.getBoolean("active"),
                        rs.getInt("employee_id"),
                        rs.getString("auth")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
