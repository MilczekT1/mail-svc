package pl.konradboniecki.budget.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.mail.model.Account;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static pl.konradboniecki.budget.mail.service.MailTemplate.CONFIRMATION_NEW_PASSWORD;

@Slf4j
@Service
public class ResetPasswordService {

    @Value("${budget.baseUrl.gateway}")
    private String BASE_URL;
    ;
    private MailService mailService;

    @Autowired
    public ResetPasswordService(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendNewPasswordActivationLink(Account account, String resetCode) throws BadRequestException {
        validateInput(account, resetCode);

        Map<String, String> contextVariables = new HashMap<>();
        contextVariables.put("recipient", account.getFirstName() + " " + account.getLastName());
        contextVariables.put("resetLink",
                BASE_URL + "/api/reset-password/" + account.getId() + "/" + resetCode);

        log.info("Mail with password reset link has been sent to " + account.getEmail());
        mailService.sendMailToUserUsingTemplate("Budget - New Password Activation",
                CONFIRMATION_NEW_PASSWORD, account.getEmail(), contextVariables);
    }

    private void validateInput(Account account, String resetCode) throws BadRequestException {
        checkArgument(account != null);
        checkArgument(!StringUtils.isBlank(resetCode));
    }
}
