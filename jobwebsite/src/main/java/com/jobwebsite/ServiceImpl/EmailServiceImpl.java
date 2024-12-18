package com.jobwebsite.ServiceImpl;

import com.jobwebsite.Controller.UserController;
import com.jobwebsite.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.host}")
    private String defaultHost;

    @Value("${spring.mail.port}")
    private int defaultPort;

    @Value("${spring.mail.username}")
    private String defaultUsername;

    @Value("${spring.mail.password}")
    private String defaultPassword;

    @Value("${superadmin.mail.host}")
    private String superAdminHost;

    @Value("${superadmin.mail.port}")
    private int superAdminPort;

    @Value("${superadmin.mail.username}")
    private String superAdminUsername;

    @Value("${superadmin.mail.password}")
    private String superAdminPassword;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public void sendEmail(String from, String to, String subject, String text, boolean useSuperAdmin) {
        JavaMailSender mailSender = useSuperAdmin ? getSuperAdminMailSender() : getDefaultMailSender();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(mimeMessage);
            System.out.println("Email sent successfully from " + from);
        } catch (MessagingException e) {
            throw new RuntimeException("Error while creating email message: " + e.getMessage(), e);
        }
    }
    private JavaMailSender getDefaultMailSender() {
        return configureMailSender(defaultHost, defaultPort, defaultUsername, defaultPassword);
    }
    private JavaMailSender getSuperAdminMailSender() {
        return configureMailSender(superAdminHost, superAdminPort, superAdminUsername, superAdminPassword);
    }


    private JavaMailSender configureMailSender(String host, int port, String username, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    //********************** Forgot Password ********************************

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("acchajobs1@gmail.com");
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
            logger.info("Email sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}", to, e);
        }
    }

}
