package com.vlad.app.service.email;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;

@Component
@Log4j2
public class EmailServiceImpl implements EmailService {

	private static final String NOREPLY_ADDRESS = "noreply@unitbv.ro";

	private final JavaMailSender emailSender;

	private final SimpleMailMessage template;

	public EmailServiceImpl(@Qualifier("getJavaMailSender") JavaMailSender emailSender, SimpleMailMessage template) {
		this.emailSender = emailSender;
		this.template = template;
	}

	@Override public void sendSimpleMessage(String to, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(NOREPLY_ADDRESS);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);

			emailSender.send(message);
		} catch (MailException exception) {
			exception.printStackTrace();
			log.error(exception.getMessage());
		}
		log.info("Sent email to: " + to);
	}

	@Override public void sendSimpleMessageUsingTemplate(String to, String subject, String... templateModel) {
		String text = String.format(Objects.requireNonNull(template.getText()), templateModel);
		sendSimpleMessage(to, subject, text);
	}

	@Override public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(NOREPLY_ADDRESS);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text);

			FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
			helper.addAttachment("Invoice", file);

			emailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
