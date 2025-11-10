package com.example.swingapp.service;

import java.util.List;

import com.example.swingapp.dao.AccountDAO;
import com.example.swingapp.model.Account;

public class AccountService {

	private final AccountDAO dao = new AccountDAO();

	public List<Account> getAll() {
		return dao.getAll();
	}

	public boolean add(Account a) {
		return dao.insert(a);
	}

	public boolean update(Account a) {
		return dao.update(a);
	}

	public boolean delete(int id) {
		return dao.delete(id);
	}
	public Account checkAccountInfo(String accountName, String auth, String email) {
		return dao.checkAccountInfo(accountName, auth, email);
	}
	public boolean insertResetCode(String code, int accountId) {
		return dao.insertCode(code,accountId);
	}
	public String getTokenByAccountId(int accountId) {
		return dao.getTotken(accountId);
	}
	public boolean updatePassword(int accountId, String newPassword, String newSalt) {
		return dao.updateNewPassword(accountId, newPassword, newSalt);
	}
}
