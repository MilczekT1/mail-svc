package pl.konradboniecki.budget.mail.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.konradboniecki.budget.mail.model.Account;
import pl.konradboniecki.budget.mail.model.Family;
import pl.konradboniecki.budget.mail.service.ExistingUserInvitationService;
import pl.konradboniecki.budget.mail.service.NewUserInvitationService;
import pl.konradboniecki.budget.mail.service.RequestBodyValidator;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/mail/invite-user")
public class InvitationToFamilyMailController {

    private ExistingUserInvitationService existingUserInvitationService;
    private NewUserInvitationService newUserInvitationService;
    private RequestBodyValidator validator;

    @Autowired
    public InvitationToFamilyMailController(ExistingUserInvitationService existingUserInvitationService,
                                            NewUserInvitationService newUserInvitationService,
                                            RequestBodyValidator validator) {
        this.existingUserInvitationService = existingUserInvitationService;
        this.newUserInvitationService = newUserInvitationService;
        this.validator = validator;
    }

    @PostMapping(value = "/existing", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendFamilyInvitationToExistingUser(@RequestBody ObjectNode json) {
        Family family = validator.extractFamily("Family", json);
        Account account = validator.extractAccount("Account", json);
        Account inviter = validator.extractAccount("Inviter", json);
        String invitationCode = validator.extractStringValue("InvitationCode", json);
        existingUserInvitationService.sendFamilyInvitationToExistingUser(family, account, inviter, invitationCode);
        return ResponseEntity.status(OK).build();
    }

    @PostMapping(value = "/new", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendFamilyInvitationToNewUser(@RequestBody ObjectNode json) {
        Account owner = validator.extractAccount("Inviter", json);
        Family family = validator.extractFamily("Family", json);
        String newMemberMail = validator.extractStringValue("NewMemberEmail", json);

        newUserInvitationService.sendFamilyInvitationToNewUser(owner, family, newMemberMail);
        log.info("Mail with invitation to family with id: " + family.getId() + " has been sent to new user with email adress: " + newMemberMail);
        return ResponseEntity.status(OK).build();
    }
}
