package com.vlad.app;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.vlad.app.config.EmailConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@SpringBootConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmailConfiguration.class)
public class EmailTest {

	@Resource
	private JavaMailSenderImpl emailSender;

	private GreenMail testSmtp;

	@Before
	public void testSmtpInit(){
		testSmtp = new GreenMail(ServerSetupTest.SMTP);
		testSmtp.setUser(emailSender.getUsername(), emailSender.getPassword());
		testSmtp.start();
	}

	@Test
	public void testEmail() throws InterruptedException, MessagingException {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom("test@sender.com");
		message.setTo("test@receiver.com");
		message.setSubject("test subject");
		message.setText("test message");
		emailSender.setProtocol("smtp");
		emailSender.send(message);

		Message[] messages = testSmtp.getReceivedMessages();
		assertEquals(1, messages.length);
		assertEquals("test subject", messages[0].getSubject());
		String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
		assertEquals("test message", body);
	}

	@After
	public void cleanup(){
		testSmtp.stop();
	}
}
