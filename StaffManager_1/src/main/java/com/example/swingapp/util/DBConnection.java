package com.example.swingapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=PROJECT_CHAMCONG_3;encrypt=false;trustServerCertificate=false";
	private static final String USER = "sa";
	private static final String PASSWORD = "123456";

	public static Connection getConnection() {
		try {
			var conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("Kết nối SQL Server thành công!");
			return conn;

		} catch (SQLException e) {
			System.err.println("Lỗi kết nối SQL Server!");
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		getConnection();
	}
}
