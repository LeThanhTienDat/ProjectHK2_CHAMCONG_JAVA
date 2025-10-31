package com.example.swingapp.service;

import com.example.swingapp.dao.AccountDAO;
import com.example.swingapp.model.Account;
import java.util.List;

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
}
