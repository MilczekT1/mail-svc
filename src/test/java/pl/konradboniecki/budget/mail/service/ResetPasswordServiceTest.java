package pl.konradboniecki.budget.mail.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.mail.model.Account;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
public class ResetPasswordServiceTest {

    private ResetPasswordService resetPasswordService;
    @MockBean
    private MailService mailService;
    private Method validateInputMethod;

    @BeforeAll
    public void setup() throws NoSuchMethodException {
        validateInputMethod = ResetPasswordService.class.getDeclaredMethod("validateInput", Account.class, String.class);
        validateInputMethod.setAccessible(true);
        resetPasswordService = new ResetPasswordService(mailService);
    }

    @Test
    public void givenNullResetCode_whenCheckResetCode_thenThrow() {
        assertThrows(IllegalArgumentException.class, () -> resetPasswordService.sendNewPasswordActivationLink(new Account(), null));
    }

    @Test
    public void givenEmptyResetCode_whenCheckResetCode_thenThrow() {
        String resetCode = "";
        assertThrows(IllegalArgumentException.class, () -> resetPasswordService.sendNewPasswordActivationLink(new Account(), resetCode));
    }

    @Test
    public void givenAnyResetCode_whenCheckResetCode_thenDontThrow() {
        String resetCode = "resetCodeInAnyFormat";
        assertDoesNotThrow(() -> resetPasswordService.sendNewPasswordActivationLink(new Account(), resetCode));
    }

    @Test
    public void givenInvalidArguments_whenSendEmail_thenFailure() {
        String resetCodeNull = null;
        String resetCode = "resetCodeInAnyFormat";
        Account accNull = null;
        Account acc = new Account();

        IllegalArgumentException whenBothParamsInvalid = catchThrowableOfType(() -> resetPasswordService.sendNewPasswordActivationLink(accNull, resetCodeNull),
                IllegalArgumentException.class);
        IllegalArgumentException whenOnlyResetCodeInvalid = catchThrowableOfType(() -> resetPasswordService.sendNewPasswordActivationLink(acc, resetCodeNull),
                IllegalArgumentException.class);
        IllegalArgumentException whenOnlyAccountInvalid = catchThrowableOfType(() -> resetPasswordService.sendNewPasswordActivationLink(accNull, resetCode),
                IllegalArgumentException.class);

        Assertions.assertAll(
                () -> assertThat(whenBothParamsInvalid).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(whenOnlyResetCodeInvalid).isInstanceOf(IllegalArgumentException.class),
                () -> assertThat(whenOnlyAccountInvalid).isInstanceOf(IllegalArgumentException.class)
        );
    }

    @Test
    public void givenValidArguments_whenSendEmail_thenSuccess() {
        String resetCode = "resetCodeInAnyFormat";
        Account acc = new Account();
        acc.setFirstName("kon");
        acc.setLastName("bon");
        acc.setId(5L);

        doNothing().when(mailService).sendMailToUserUsingTemplate(anyString(), anyString(), any(), anyMap());
        assertDoesNotThrow(() ->
                resetPasswordService.sendNewPasswordActivationLink(acc, resetCode));
    }
}
