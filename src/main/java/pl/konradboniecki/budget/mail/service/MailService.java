package pl.konradboniecki.budget.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.konradboniecki.chassis.exceptions.InternalServerErrorException;

import javax.mail.internet.MimeMessage;
import java.util.Map;

// TODO: @RefreshScope() to enable refresh of values annotated with @Value
@Slf4j
@Service
public class MailService {

    private JavaMailSender javaMailSender;
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String emailAdress;
    @Value("${budget.mail.replyTo}")
    private String replyToEmailAdress;

    @Autowired
    public MailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMailToUserUsingTemplate(String title, String templateName, String destination, Map<String, String> contextVariables) {
        String message = processHtml(templateName, contextVariables);
        sendEmail(title, message, destination);
    }

    private String processHtml(String template, Map<String, String> contextVariables) {
        Context context = new Context();
        for (Map.Entry<String, String> entry : contextVariables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        return templateEngine.process(template, context);
    }

    private void sendEmail(String title, String message, String destination) {
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail);
        try {
            helper.setTo(destination);
            helper.setFrom(emailAdress);
            helper.setSubject(title);
            helper.setText(message, true);
            helper.setReplyTo(replyToEmailAdress);
            javaMailSender.send(mail);
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to send mail to: " + destination, e);
        }
    }
}
