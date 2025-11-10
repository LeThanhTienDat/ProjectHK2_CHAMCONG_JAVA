package com.example.swingapp.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.swingapp.model.Account;
import com.example.swingapp.util.DBConnection;

public class AccountDAO implements BaseDAO<Account> {

	@Override
	public boolean insert(Account a) {
		var sql = "INSERT INTO tbl_account(account_name, password, salt, active, employee_id, auth) VALUES (?, ?, ?, ?, ?, ?)";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {

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
		var sql = "UPDATE tbl_account SET password=?, active=?, auth=? WHERE account_id=?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
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
		var sql = "DELETE FROM tbl_account WHERE account_id=?";
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
	public List<Account> getAll() {
		List<Account> list = new ArrayList<>();
		var sql = "SELECT * FROM tbl_account";
		try (var conn = DBConnection.getConnection();
				var st = conn.createStatement();
				var rs = st.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Account(
						rs.getInt("account_id"),
						rs.getString("account_name"),
						rs.getString("password"),
						rs.getString("salt"),
						rs.getBoolean("active"),
						rs.getInt("employee_id"),
						rs.getString("auth"),
						rs.getString("reset_code")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public Account findByUsername(String username) {
		var sql = "SELECT * FROM tbl_Account WHERE account_name = ?";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			var rs = ps.executeQuery();
			if (rs.next()) {
				var a = new Account();
				a.setAccountId(rs.getInt("account_id"));
				a.setAccountName(rs.getString("account_name"));
				a.setPassword(rs.getString("password"));
				a.setSalt(rs.getString("salt"));
				a.setActive(rs.getBoolean("active"));
				a.setAuth(rs.getString("auth"));
				a.setEmployeeId(rs.getInt("employee_id"));
				return a;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public Account checkAccountInfo(String accountName, String auth, String email) {
		var sql ="""
					select ac.account_id
					from tbl_Account ac
					join tbl_Employee e on e.id = ac.employee_id
					where ac.account_name = ? and ac.auth = ? and e.email = ?
				""";
		try (var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)) {
			ps.setString(1, accountName);
			ps.setString(2,auth);
			ps.setString(3,email);
			var rs = ps.executeQuery();
			if (rs.next()) {
				var account = new Account();
				account.setAccountId(rs.getInt("account_id"));
				return account;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public boolean insertCode(String code, int accountId) {
		var sql = """
					Update tbl_Account
					set reset_code = ?
					where account_id = ?
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)){
			ps.setString(1,code);
			ps.setInt(2, accountId);
			var rs = ps.executeUpdate();
			return rs > 0;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public String getTotken(int accountId) {
		var token = "";
		var sql = """
					select a.reset_code
					from tbl_Account a
					where a.account_id = ?
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql)){
			ps.setInt(1, accountId);
			var rs = ps.executeQuery();
			while(rs.next()) {
				token = rs.getString("reset_code");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		return token;
	}
	public boolean updateNewPassword(int accountId, String newHashedPassword, String newSalt) {
		var sql = """
					update tbl_Account
					set salt = ?, password = ?
					where account_id =?
				""";
		try(var conn = DBConnection.getConnection();
				var ps = conn.prepareStatement(sql);){
			ps.setString(1, newSalt);
			ps.setString(2,newHashedPassword);
			ps.setInt(3,accountId);
			var rs = ps.executeUpdate();
			return rs >0;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}


}
