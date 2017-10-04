package com.sumi.transaku.core.services;

import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class MailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	
	private final JavaMailSender javaMailSender;
	@Autowired
    private TemplateEngine htmlTemplateEngine;
	
	@Autowired
    MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
	
	@Async
	public void sendEmail(String sender, String recipients, String subject, String content){
		LOGGER.info("Sending email {} {} {} {}", sender, recipients, subject, content);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String[] targets = recipients.split(",");
        mailMessage.setTo(targets);
        mailMessage.setReplyTo(sender);
        mailMessage.setFrom(sender);
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        javaMailSender.send(mailMessage);
	}
	
	@Async
	public void sendMailWithInline(String sender, final String recipientName, String subject, final String recipientEmail, String content)
			throws MessagingException {
		LOGGER.info("Sending email to "+recipientEmail);
		// Prepare the evaluation context
//		final Context ctx = new Context(locale);
		Locale locale = new Locale("ind");
		final Context ctx = new Context(locale);
		ctx.setVariable("name", recipientName);
		ctx.setVariable("content", content);

		// Prepare message using a Spring helper
		final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
		MimeMessageHelper message;
		try {
			message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			message.setSubject(subject);
			message.setFrom(sender);
			String[] targets = recipientEmail.split(",");
		    
			message.setTo(targets);
		
			// Create the HTML body using Thymeleaf
			final String htmlContent = this.htmlTemplateEngine.process("email_template", ctx);
			message.setText(htmlContent, true); // true = isHtml
		} catch (javax.mail.MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // true


		// Send mail
		this.javaMailSender.send(mimeMessage);

	}
}
