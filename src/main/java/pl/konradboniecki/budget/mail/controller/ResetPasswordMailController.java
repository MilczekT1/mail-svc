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
import pl.konradboniecki.budget.mail.service.RequestBodyValidator;
import pl.konradboniecki.budget.mail.service.ResetPasswordService;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/mail")
public class ResetPasswordMailController {

    private ResetPasswordService resetPasswordService;
    private RequestBodyValidator validator;

    @Autowired
    public ResetPasswordMailController(ResetPasswordService resetPasswordService, RequestBodyValidator validator) {
        this.resetPasswordService = resetPasswordService;
        this.validator = validator;
    }

    @PostMapping(value = "/reset-password", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendNewPasswordActivationLink(@RequestBody ObjectNode json) {
        Account account = validator.extractAccount("Account", json);
        String resetCode = validator.extractStringValue("ResetCode", json);
        resetPasswordService.sendNewPasswordActivationLink(account, resetCode);
        return ResponseEntity.status(OK).build();
    }
}
