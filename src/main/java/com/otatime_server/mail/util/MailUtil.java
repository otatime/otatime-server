package com.otatime_server.mail.util;

import static com.otatime_server.mail.MailConst.CERTCODE;
import static com.otatime_server.mail.MailConst.EMAIL_TEMPLATE;
import static com.otatime_server.mail.MailConst.MAIL;
import static com.otatime_server.mail.MailConst.MAIL_SUBJECT;
import static com.otatime_server.mail.MailConst.NUMBER_AND_ALPHABET;
import static com.otatime_server.mail.MailConst.UTF8;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
public class MailUtil {
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String from;

    @Async
    public CompletableFuture<String> sendMail(String to) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF8);
            helper.setTo(to);
            helper.setSubject(MAIL_SUBJECT);
            helper.setFrom(from);

            String certCode = generateCertCode();
            Context context = new Context();
            context.setVariable(MAIL, to);
            context.setVariable(CERTCODE, certCode);

            String htmlContent = templateEngine.process(EMAIL_TEMPLATE, context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return CompletableFuture.completedFuture(certCode);

        } catch (MessagingException e) {
            throw new IllegalStateException("MAIL_CREATION_FAILED");
        }
    }

    private String generateCertCode() {
        final String candidateChars = NUMBER_AND_ALPHABET;
        final int certCodeLength = 6;
        SecureRandom random = new SecureRandom();
        StringBuilder certCode = new StringBuilder(certCodeLength);

        for (int i = 0; i < certCodeLength; i++) {
            int index = random.nextInt(candidateChars.length());
            certCode.append(candidateChars.charAt(index));
        }
        return certCode.toString();
    }
}

