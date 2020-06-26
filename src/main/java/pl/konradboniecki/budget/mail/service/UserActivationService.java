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
import static pl.konradboniecki.budget.mail.service.MailTemplate.CONFIRMATION_SIGN_UP;

@Slf4j
@Service
public class UserActivationService {

    @Value("${budget.baseUrl.gateway}")
    private String BASE_URL;
    private MailService mailService;

    @Autowired
    public UserActivationService(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendSignUpConfirmation(Account account, String activationCode) throws BadRequestException {
        validateInput(account, activationCode);

        Map<String, String> contextVariables = new HashMap<>();
        contextVariables.put("recipient", account.getFirstName() + " " + account.getLastName());
        contextVariables.put("activationLink",
                BASE_URL + "/api/account/activate/" + account.getId() + "/" + activationCode);
        log.info("Attempting to send activation link to: " + account.getEmail());
        mailService.sendMailToUserUsingTemplate("Budget - Sign up completed",
                CONFIRMATION_SIGN_UP, account.getEmail(), contextVariables);
    }

    private void validateInput(Account account, String activationCode) throws BadRequestException {
        checkArgument(account != null, "Account is null.");
        checkArgument(!StringUtils.isBlank(activationCode), "Activation code is blank.");
    }
}
