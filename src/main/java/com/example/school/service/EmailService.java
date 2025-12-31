package com.example.school.service;

import org.springframework.beans.factory.annotation.Value;
import com.example.school.entity.Establishment;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String smtpHost;
    private final String smtpUser;
    private final String from;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.host:}") String smtpHost,
            @Value("${spring.mail.username:}") String smtpUser,
            @Value("${app.mail.from:${spring.mail.username:no-reply@osm.local}}") String from
    ) {
        this.mailSender = mailSender;
        this.smtpHost = smtpHost;
        this.smtpUser = smtpUser;
        this.from = from;
    }

    public boolean isConfigured() {
        return smtpHost != null && !smtpHost.isBlank();
    }

    public boolean send(String to, String subject, String body) {
        if (!isConfigured()) {
            System.err.println("EMAIL NOT CONFIGURED: set spring.mail.host/username/password to send emails.");
            return false;
        }
        String safeTo = sanitizeAddress(to, null);
        if (safeTo == null) {
            System.err.println("EMAIL NOT SENT: invalid recipient: " + to);
            return false;
        }
        String safeFrom = sanitizeAddress(from, sanitizeAddress(smtpUser, "no-reply@osm.local"));
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(safeFrom);
            message.setTo(safeTo);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (MailException ex) {
            System.err.println("EMAIL SEND FAILED to " + to + ": " + ex.getMessage());
            return false;
        }
    }

    public boolean isTenantConfigured(Establishment establishment) {
        if (establishment == null) {
            return false;
        }
        if (!establishment.isSmtpEnabled()) {
            return false;
        }
        if (establishment.getSmtpHost() == null || establishment.getSmtpHost().isBlank()) {
            return false;
        }
        if (establishment.getSmtpUsername() == null || establishment.getSmtpUsername().isBlank()) {
            return false;
        }
        if (establishment.getSmtpPassword() == null || establishment.getSmtpPassword().isBlank()) {
            return false;
        }
        Integer port = establishment.getSmtpPort();
        return port != null && port > 0;
    }

    public boolean sendForTenant(Establishment establishment, String to, String subject, String body) {
        if (!isTenantConfigured(establishment)) {
            System.err.println("EMAIL NOT CONFIGURED for tenant: configure SMTP before sending emails.");
            return false;
        }
        String safeTo = sanitizeAddress(to, null);
        if (safeTo == null) {
            System.err.println("EMAIL NOT SENT: invalid recipient: " + to);
            return false;
        }
        String safeFrom = sanitizeAddress(
                establishment.getSmtpFrom(),
                sanitizeAddress(establishment.getSmtpUsername(), "no-reply@osm.local")
        );
        try {
            JavaMailSenderImpl sender = buildTenantSender(establishment);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(safeFrom);
            message.setTo(safeTo);
            message.setSubject(subject);
            message.setText(body);
            sender.send(message);
            return true;
        } catch (MailException ex) {
            System.err.println("EMAIL SEND FAILED to " + to + ": " + ex.getMessage());
            return false;
        }
    }

    private JavaMailSenderImpl buildTenantSender(Establishment establishment) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(establishment.getSmtpHost());
        sender.setPort(establishment.getSmtpPort());
        sender.setUsername(establishment.getSmtpUsername());
        sender.setPassword(establishment.getSmtpPassword());
        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", String.valueOf(establishment.isSmtpAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(establishment.isSmtpStarttls()));
        return sender;
    }

    private String sanitizeAddress(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        if (trimmed.isBlank()) {
            return fallback;
        }
        try {
            InternetAddress address = new InternetAddress(trimmed, true);
            address.validate();
            return trimmed;
        } catch (AddressException ex) {
            return fallback;
        }
    }
}
