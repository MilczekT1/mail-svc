package pl.konradboniecki.budget.mail.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.mail.model.Account;
import pl.konradboniecki.budget.mail.model.Family;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
public class NewUserInvitationServiceTest {

    @MockBean
    private MailService mailService;
    private NewUserInvitationService newUserInvitationService;
    private Method createContextForTemplateMethod;

    @BeforeAll
    public void setup() throws NoSuchMethodException {
        createContextForTemplateMethod = NewUserInvitationService.class.getDeclaredMethod("createContextForTemplate", Account.class, Family.class, String.class);
        createContextForTemplateMethod.setAccessible(true);

        newUserInvitationService = new NewUserInvitationService(mailService);
    }

    @Test
    public void givenNullEmail_whenCheckEmail_thenThrow() {
        assertThrows(IllegalArgumentException.class, () -> newUserInvitationService.sendFamilyInvitationToNewUser(new Account(), new Family(), null));
    }

    @Test
    public void givenEmptyEmail_whenCheckEmail_thenIsEmailOkMethodReturnFalse() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> newUserInvitationService.sendFamilyInvitationToNewUser(new Account(), new Family(), ""));
    }

    @Test
    public void givenArguments_whenCreateContext_thenReturnMap() throws Exception {
        Account acc = new Account();
        acc.setFirstName("testFirstName");
        acc.setLastName("testLastName");
        acc.setId(5L);
        acc.setEmail("test@email.com");
        String email = "test@email.com";
        Family family = new Family();
        family.setId(5L);
        family.setTitle("testTitle");

        Map<String, String> map = (Map<String, String>) createContextForTemplateMethod.invoke(newUserInvitationService, (acc), family, email);
        Assertions.assertAll(
                () -> assertTrue(map.containsKey("recipient")),
                () -> assertTrue(map.containsKey("familyTitle")),
                () -> assertTrue(map.containsKey("ownersFirstName")),
                () -> assertTrue(map.containsKey("ownersLastName")),
                () -> assertTrue(map.containsKey("ownersEmail")),
                () -> assertTrue(map.containsKey("registerLink"))
        );
    }

    @Test
    public void givenInvalidArguments_whenSendEmail_thenReturnFalse() throws Exception {
        assertThrows(IllegalArgumentException.class, () ->
                newUserInvitationService.sendFamilyInvitationToNewUser(null, new Family(), "emailInAnyFormat")
        );
    }

    @Test
    public void givenValidArguments_whenSendEmailAndFailure_thenReturnFalse() {
        doNothing().when(mailService).sendMailToUserUsingTemplate(anyString(), anyString(), anyString(), anyMap());
        assertDoesNotThrow(() ->
                newUserInvitationService.sendFamilyInvitationToNewUser(new Account(), new Family(), "emailInAnyFormat"));
    }

    @Test
    public void givenValidArguments_whenSendEmailAndSuccess_thenReturnTrue() {
        doNothing().when(mailService).sendMailToUserUsingTemplate(anyString(), anyString(), anyString(), anyMap());
        assertDoesNotThrow(() ->
                newUserInvitationService.sendFamilyInvitationToNewUser(new Account(), new Family(), "emailInAnyFormat"));
    }

    @ParameterizedTest
    @MethodSource("createAccountFamilyEmailMatrix")
    public void givenInvalidArguments_whenCheckInput_thenThrow(Account account, Family family, String email) {
        assertThrows(IllegalArgumentException.class, () -> newUserInvitationService.sendFamilyInvitationToNewUser(account, family, email));
    }

    private static Stream<Arguments> createAccountFamilyEmailMatrix() {
        String email = "emailInAnyFormat";
        Account acc = new Account();
        Family family = new Family();
        return Stream.of(
                Arguments.of(null, null, null),
                Arguments.of(null, null, email),
                Arguments.of(acc, null, null),
                Arguments.of(acc, null, email),
                Arguments.of(acc, family, null),
                Arguments.of(null, family, null),
                Arguments.of(null, family, email)
        );
    }
}
