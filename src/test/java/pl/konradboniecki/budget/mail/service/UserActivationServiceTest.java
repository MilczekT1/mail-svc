package pl.konradboniecki.budget.mail.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.mail.model.Account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
public class UserActivationServiceTest {

    @MockBean
    private MailService mailService;
    private UserActivationService userActivationService;

    @BeforeAll
    public void setup() {
        userActivationService = new UserActivationService(mailService);
    }

    @Test
    public void givenInvalidArguments_whenSendEmail_thenFailure() {
        String activationCodeNull = null;
        String activationCode = "activationCodeInAnyFormat";
        Account accNull = null;
        Account acc = new Account();

        IllegalArgumentException whenBothParamsInvalid = catchThrowableOfType(() -> userActivationService.sendSignUpConfirmation(accNull, activationCodeNull),
                IllegalArgumentException.class);

        IllegalArgumentException whenOnlyActivationCodeInvalid = catchThrowableOfType(() -> userActivationService.sendSignUpConfirmation(acc, activationCodeNull),
                IllegalArgumentException.class);
        IllegalArgumentException whenOnlyAccountInvalid = catchThrowableOfType(() -> userActivationService.sendSignUpConfirmation(accNull, activationCode),
                IllegalArgumentException.class);


        assertAll(
                () -> assertThat(whenBothParamsInvalid).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(whenOnlyAccountInvalid).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(whenOnlyActivationCodeInvalid).isInstanceOf(IllegalArgumentException.class)
        );
    }

    @Test
    public void givenValidArguments_whenSendEmail_thenSuccess() {
        String activationCode = "activationCodeInAnyFormat";
        Account acc = new Account();
        acc.setFirstName("kon");
        acc.setLastName("bon");
        acc.setId(5L);

        doNothing().when(mailService).sendMailToUserUsingTemplate(anyString(), anyString(), any(), anyMap());
        assertDoesNotThrow(() ->
                userActivationService.sendSignUpConfirmation(acc, activationCode));
    }
}
