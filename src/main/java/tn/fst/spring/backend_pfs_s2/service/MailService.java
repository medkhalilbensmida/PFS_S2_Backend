package tn.fst.spring.backend_pfs_s2.service;

import java.util.Map;

import java.util.HashMap;

import jakarta.annotation.PostConstruct;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import tn.fst.spring.backend_pfs_s2.dto.MailRequest;
/**
 * Service for sending emails using a template engine.
 * This service utilizes JavaMailSender for sending emails and SpringTemplateEngine
 * for processing email templates with context variables.
 * 
 * Dependencies:
 * - JavaMailSender: For creating and sending email messages.
 * - SpringTemplateEngine: For processing email templates.
 * 
 * Methods:
 * - {@link #/sendEmail(String, String, String, Map)}: Sends an email with the specified
 *   recipient, subject, template, and context variables.
 */
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendEmail(MailRequest request) throws MessagingException {
        // Create a new email message
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper  helper = new MimeMessageHelper(message, true , "UTF-8");

        helper.setFrom(fromMail);
        helper.setTo(request.getToEmail());
        helper.setSubject(request.getSubject());

        // Process the email template with context variables
        if (request.getIsHtml()) {
            String template = request.getTemplate();
            Context context = new Context();

            Map<String, Object> contextVariables = request.getContext();
            for (Map.Entry<String, Object> entry:contextVariables.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
        
            String htmlContent = templateEngine.process(template, context);
            System.out.println(htmlContent);
            helper.setText(htmlContent, true);
            
        } else {
            helper.setText(request.getMessage(),false);
        }

        // Handle attachments
        if (request.getAttachments() != null) {
            for (MailRequest.Attachment attachment : request.getAttachments()) {
                helper.addAttachment(attachment.getFileName(), new ByteArrayResource(attachment.getFileData()));
            }
        }
        mailSender.send(message);

    }

    //@PostConstruct
    public void sendTestEmail() {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", "Test User");
            variables.put("message", "This is an automated test email.");

            MailRequest testRequest = new MailRequest();
            testRequest.setToEmail("test@localhost");
            testRequest.setSubject("Test Email");
            testRequest.setTemplate("Test");
            testRequest.setIsHtml(true);
            testRequest.setContext(variables);

            sendEmail(testRequest);
            System.out.println("Test email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

