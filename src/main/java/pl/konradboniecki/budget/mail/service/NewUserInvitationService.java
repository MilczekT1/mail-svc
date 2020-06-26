package pl.konradboniecki.budget.mail.service;

import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.mail.model.Account;
import pl.konradboniecki.budget.mail.model.Family;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class NewUserInvitationService {

    @Value("${budget.baseUrl.gateway}")
    private String BASE_URL;
    private MailService mailService;

    @Autowired
    public NewUserInvitationService(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendFamilyInvitationToNewUser(Account owner, Family family, String newMemberMail) {
        isInputOk(owner, family, newMemberMail);
        Map<String, String> ctxtVariables = createContextForTemplate(owner, family, newMemberMail);
        String title = "Budget - Invitation to family";
        mailService.sendMailToUserUsingTemplate(title, MailTemplate.INVITE_FAMILY_NEW_USER, newMemberMail, ctxtVariables);
    }

    private Map<String, String> createContextForTemplate(Account owner, Family family, String newMemberMail) {
        Map<String, String> ctxtVariables = new HashMap<>();
        ctxtVariables.put("recipient", newMemberMail);
        ctxtVariables.put("familyTitle", family.getTitle());
        ctxtVariables.put("ownersFirstName", owner.getFirstName());
        ctxtVariables.put("ownersLastName", owner.getLastName());
        ctxtVariables.put("ownersEmail", owner.getEmail());
        ctxtVariables.put("registerLink", BASE_URL + "/register");
        return ctxtVariables;
    }

    private void isInputOk(Account account, Family family, String email) {
        checkArgument(account != null);
        checkArgument(family != null);
        checkArgument(!StringUtils.isBlank(email));
    }
}
