package com.gar.system.service.impl;

import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.gar.system.model.GarUser;
import com.gar.system.service.GarMailService;

@Service
public class GarMailServiceImpl implements GarMailService {

	private final Logger logger =LoggerFactory.getLogger(GarMailServiceImpl.class);
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Async
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		logger.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
			message.setTo(to);
			message.setFrom("");
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			logger.debug("Sent email to User '{}'", to);
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.warn("Email could not be sent to user '{}'", to, e);
			} else {
				logger.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
			}
		}
	}

	@Async
	public void sendEmailFromTemplate(GarUser user, String templateName, String titleKey) {
		Locale locale = Locale.forLanguageTag(user.getLangKey());
		Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("baseUrl", "");
		String content = templateEngine.process(templateName, context);
		String subject = messageSource.getMessage(titleKey, null, locale);
		sendEmail(user.getEmail(), subject, content, false, true);

	}

	@Override
	@Async
	public void sendActivationEmail(GarUser user) {
		logger.debug("Sending activation email to '{}'", user.getEmail());
		sendEmailFromTemplate(user, "activationEmail", "email.activation.title");
	}

	@Override
	@Async
	public void sendCreationEmail(GarUser user) {
		logger.debug("Sending creation email to '{}'", user.getEmail());
		sendEmailFromTemplate(user, "creationEmail", "email.activation.title");
	}

	@Override
	@Async
	public void sendPasswordResetMail(GarUser user) {
		logger.debug("Sending password reset email to '{}'", user.getEmail());
		sendEmailFromTemplate(user, "passwordResetEmail", "email.reset.title");
	}

}
