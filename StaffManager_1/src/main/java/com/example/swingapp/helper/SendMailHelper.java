package com.example.swingapp.helper;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SendMailHelper {
	public static boolean SendMail(String fromEmail, String toEmail, String subject, String body) throws Exception {
		// ==== NHẬP THÔNG TIN SMTP Ở ĐÂY ====
		var host = "smtp.gmail.com";   // ví dụ: Gmail
		var    port = 587;                // 587 (STARTTLS) hoặc 465 (SSL)
		var username = "datleoffice264@gmail.com";
		var password = "ppnv zubl uuyi gjlh";
		//		from = "datleoffice264@gmail.com";
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
