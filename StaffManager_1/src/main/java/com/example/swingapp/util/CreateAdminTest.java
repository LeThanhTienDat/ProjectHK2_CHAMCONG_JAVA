package com.example.swingapp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CreateAdminTest {

	/**
	 * Sinh salt ngẫu nhiên và encode Base64
	 */
	public static String genSalt() {
		var salt = new byte[16]; // 16 bytes = 128 bit
		new SecureRandom().nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	/**
	 * Hash mật khẩu với salt bằng SHA-256 và encode Base64
	 */
	public static String hashPassword(String password, String salt) {
		try {
			var md = MessageDigest.getInstance("SHA-256");
			md.update(Base64.getDecoder().decode(salt)); // decode Base64 salt trước
			var hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hashed);
		} catch (Exception e) {
			throw new RuntimeException("Lỗi khi mã hóa mật khẩu", e);
		}
	}

	/**
	 * Tạo câu lệnh SQL INSERT cho Admin
	 */
	public static String generateAdminInsertQuery(String accountName, String rawPassword) {
		var salt = genSalt();
		var hashedPassword = hashPassword(rawPassword, salt);

		var active = 1;
		var auth = "ADMIN";

		var sqlQuery = String.format(
				"""
				INSERT INTO tbl_Account (account_name, password, salt, active, auth) VALUES (
				    '%s',
				    '%s',
				    '%s',
				    %d,
				    '%s'
				);
				""",
				accountName,
				hashedPassword,
				salt,
				active,
				auth
				);

		return sqlQuery;
	}

	public static void main(String[] args) {
		// Tạo tài khoản test
		var username = "admin2";
		var password = "123456";

		if (args != null && args.length >= 2) {
			username = args[0];
			password = args[1];
		}

		var sql = generateAdminInsertQuery(username, password);

		System.out.println("=== DỮ LIỆU ADMIN ĐƯỢC SINH RA ===");
		System.out.println("Username: " + username);
		System.out.println("Raw Password: " + password);
		System.out.println("---------------------------");
		System.out.println("Bạn có thể copy và chạy query sau trong SQL Server:");
		System.out.println("-------------------------------------------------");
		System.out.println(sql);
		System.out.println("-------------------------------------------------");
	}
}
