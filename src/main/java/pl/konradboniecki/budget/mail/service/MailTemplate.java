package pl.konradboniecki.budget.mail.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MailTemplate provides names for html templates with mail messages.
 **/

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MailTemplate implements Serializable {
    static final String INVITE_FAMILY_OLD_USER = "familyInvitationForExistingUser";
    static final String INVITE_FAMILY_NEW_USER = "familyInvitationForNewUser";
    static final String CONFIRMATION_SIGN_UP = "signUpConfirmationMail";
    static final String CONFIRMATION_NEW_PASSWORD = "newPasswordConfirmationMail";
}
