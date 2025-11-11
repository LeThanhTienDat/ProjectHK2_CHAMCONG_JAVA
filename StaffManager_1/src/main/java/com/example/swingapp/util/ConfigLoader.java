package com.example.swingapp.util;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.example.swingapp.ui.LoginForm;
import com.formdev.flatlaf.FlatLightLaf;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigLoader {

	private static Dotenv dotenv;

	public static void loadEnvironment() {
		try {
			dotenv = Dotenv.load();
		} catch (Exception e) {
			System.err.println("CẢNH BÁO: Không tìm thấy tệp .env. Đang sử dụng System Environment.");
		}
	}

	public static String getEnv(String key) {
		if (dotenv != null) {
			return dotenv.get(key);
		}
		return System.getenv(key);
	}

	public static void main(String[] args) {
		loadEnvironment();
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
	}
}