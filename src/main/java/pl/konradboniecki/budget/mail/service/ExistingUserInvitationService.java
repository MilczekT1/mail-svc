package pl.konradboniecki.budget.mail.service;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.mail.model.Account;
import pl.konradboniecki.budget.mail.model.Family;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static pl.konradboniecki.budget.mail.service.MailTemplate.INVITE_FAMILY_OLD_USER;

@Slf4j
@Service
public class ExistingUserInvitationService {

    @Value("${budget.baseUrl.gateway}")
    private String BASE_URL;
    private MailService mailService;

    @Autowired
    public ExistingUserInvitationService(MailService mailService) {
        this.mailService = mailService;
    }

    public void sendFamilyInvitationToExistingUser(Family family, Account account, Account owner, String invitationCode) {
        validateInput(family, account, owner, invitationCode);
        log.info("Sending mail with invitation to family with id: {}, to user with id: {} on behalf of user with id: {}", family.getId(), account.getId(), owner.getId());
        Map<String, String> ctxVariables = createContextForTemplate(family, account, owner, invitationCode);
        String title = "Budget - Invitation to the new family";

        mailService.sendMailToUserUsingTemplate(title, INVITE_FAMILY_OLD_USER, account.getEmail(), ctxVariables);
    }

    private Map<String, String> createContextForTemplate(Family family, Account account, Account owner, String invitationCode) {
        Map<String, String> ctxVariables = new HashMap<>();
        ctxVariables.put("recipient", account.getFirstName() + " " + account.getLastName());
        ctxVariables.put("familyTitle", family.getTitle());
        ctxVariables.put("ownersFirstName", owner.getFirstName());
        ctxVariables.put("ownersLastName", owner.getLastName());
        ctxVariables.put("ownersEmail", owner.getEmail());
        ctxVariables.put("invitationLink", BASE_URL + "/home/family/" + family.getId() + "/addMember/" + account.getId() + "/" + invitationCode);
        return ctxVariables;
    }

    private void validateInput(Family family, Account account, Account owner, String invitationCode) {
        checkArgument(account != null);
        checkArgument(owner != null);
        checkArgument(family != null);
        checkArgument(!StringUtils.isBlank(invitationCode));
    }
}
