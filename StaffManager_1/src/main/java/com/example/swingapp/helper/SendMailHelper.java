package com.example.swingapp.helper;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.example.swingapp.util.ConfigLoader;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SendMailHelper {
	private String getHost;
	private String getUsername;
	private String getPassword;
	private String getFrom;
	private int getPortInt;

	// 🔥 CONSTRUCTOR để khởi tạo an toàn
	public SendMailHelper() {
		var portStr = ConfigLoader.getEnv("SMTP_PORT");

		// Gán giá trị
		getHost = ConfigLoader.getEnv("SMTP_HOST");
		getUsername = ConfigLoader.getEnv("SMTP_USERNAME");
		getPassword = ConfigLoader.getEnv("SMTP_PASSWORD");
		getFrom = ConfigLoader.getEnv("MAIL_FROM");

		// Xử lý PORT
		if (portStr == null || portStr.trim().isEmpty()) {
			System.err.println("CẢNH BÁO: SMTP_PORT bị thiếu. Mặc định dùng 587.");
			getPortInt = 587;
		} else {
			try {
				// Sử dụng trim() để loại bỏ ký tự thừa (cả khoảng trắng và ký tự ẩn)
				getPortInt = Integer.parseInt(portStr.trim());
			} catch (NumberFormatException e) {
				// Nếu vẫn dính ký tự lạ (như ""587";"), báo lỗi rõ ràng hơn
				throw new IllegalStateException("Lỗi cấu hình: SMTP_PORT phải là số nguyên. Giá trị hiện tại: [" + portStr + "]", e);
			}
		}

		if (getUsername == null || getPassword == null) {
			throw new IllegalStateException("Lỗi cấu hình: Thiếu SMTP_USERNAME hoặc SMTP_PASSWORD.");
		}
	}


	public boolean SendMail(String fromEmail, String toEmail, String subject, String body) throws Exception {
		// ==== NHẬP THÔNG TIN SMTP Ở ĐÂY ====
		var host = getHost;
		var	port = getPortInt;
		var username = getUsername;
		var password = getPassword;
		var from = fromEmail;

		// ==== THIẾT LẬP PROPERTIES ====
		var props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", String.valueOf(port));
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.connectiontimeout", "10000");
		props.put("mail.smtp.timeout", "10000");
		props.put("mail.debug", "true"); // bật log

		if (port == 465) {
			props.put("mail.smtp.ssl.enable", "true");
		} else {
			props.put("mail.smtp.starttls.enable", "true");
		}

		// ==== AUTH ====
		var session = Session.getInstance(props, new Authenticator() {
			@Override protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		// ==== TẠO EMAIL (text/HTML đơn giản) ====
		try {
			var msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			msg.setSubject(subject, StandardCharsets.UTF_8.name());
			msg.setContent(body, "text/html; charset=UTF-8");

			// Nếu cần gửi file đính kèm, dùng Multipart (mẫu):
			// MimeBodyPart body = new MimeBodyPart(); body.setContent(html, "text/html; charset=UTF-8");
			// MimeBodyPart att = new MimeBodyPart(); att.attachFile("D:/path/to/file.pdf");
			// Multipart mp = new MimeMultipart(); mp.addBodyPart(body); mp.addBodyPart(att); msg.setContent(mp);

			Transport.send(msg);
			System.out.println("Đã gửi!");
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}

	}
}
