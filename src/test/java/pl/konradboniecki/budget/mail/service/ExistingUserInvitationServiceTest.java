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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = WebEnvironment.NONE,
        properties = "spring.cloud.config.enabled=false"
)
public class ExistingUserInvitationServiceTest {

    @MockBean
    private MailService mailService;
    private ExistingUserInvitationService existingUserInvitationService;
    private Method isInputOkMethod;
    private Method createContextForTemplateMethod;

    @BeforeAll
    public void setup() throws NoSuchMethodException {
        isInputOkMethod = ExistingUserInvitationService.class.getDeclaredMethod("validateInput", Family.class, Account.class, Account.class, String.class);
        isInputOkMethod.setAccessible(true);

        createContextForTemplateMethod = ExistingUserInvitationService.class.getDeclaredMethod("createContextForTemplate", Family.class, Account.class, Account.class, String.class);
        createContextForTemplateMethod.setAccessible(true);
        existingUserInvitationService = new ExistingUserInvitationService(mailService);
    }

    @Test
    //TODO: to be parametrized
    public void givenNullInvitationCode_whenCheckInvitationCode_thenThrow() {
        assertThrows(IllegalArgumentException.class, () -> existingUserInvitationService.sendFamilyInvitationToExistingUser(new Family(), new Account(), new Account(), null));
    }

    @Test
    //TODO: to be parametrized
    public void givenEmptyInvitationCode_whenCheckInvitationCode_thenIsInvitationCodeOkMethodReturnFalse() {
        assertThrows(IllegalArgumentException.class, () -> existingUserInvitationService.sendFamilyInvitationToExistingUser(new Family(), new Account(), new Account(), ""));
    }

    @Test
    public void givenAnyInvitationCode_whenCheckInvitationCode_thenIsInvitationCodeOkMethodReturnTrue() {
        assertDoesNotThrow(() -> existingUserInvitationService.sendFamilyInvitationToExistingUser(new Family(), new Account(), new Account(), "invitationCodeInAnyFormat"));
    }

    @Test
    public void givenArguments_whenCreateContext_thenReturnMap() throws Exception {
        Account acc = new Account();
        acc.setFirstName("testFirstName");
        acc.setLastName("testLastName");
        acc.setId(5L);
        acc.setEmail("test@email.com");
        Account owner = new Account();
        owner.setFirstName("testFirstName");
        owner.setLastName("testLastName");
        owner.setId(5L);
        owner.setEmail("test@email.com");
        String invitationCode = "invitationCodeInAnyFormat";
        Family family = new Family();
        family.setId(5L);
        family.setTitle("testTitle");

        Map<String, String> map = (Map) createContextForTemplateMethod.invoke(existingUserInvitationService, family, acc, owner, invitationCode);

        Assertions.assertAll(
                () -> assertTrue(map.containsKey("recipient")),
                () -> assertTrue(map.containsKey("familyTitle")),
                () -> assertTrue(map.containsKey("ownersFirstName")),
                () -> assertTrue(map.containsKey("ownersLastName")),
                () -> assertTrue(map.containsKey("ownersEmail")),
                () -> assertTrue(map.containsKey("invitationLink"))
        );
    }

    @Test
    public void givenInvalidArguments_whenSendEmail_thenReturnFalse() {
        assertThrows(IllegalArgumentException.class, () -> existingUserInvitationService.sendFamilyInvitationToExistingUser(null, new Account(), new Account(), "invitationCodeInAnyFormat"));
    }

    @Test
    public void givenValidArguments_whenSendEmailAndSuccess_thenReturnTrue() {
        Account acc = new Account();
        acc.setEmail("email");
        assertDoesNotThrow(() ->
                existingUserInvitationService.sendFamilyInvitationToExistingUser(new Family(), acc, acc, "invitationCodeInAnyFormat"));
    }

    @ParameterizedTest
    @MethodSource("createInputMatrix")
    public void givenInvalidArguments_whenCheckInput_thenIsInputOkMethodReturnFalse(Family family, Account account, Account owner, String invitationCode) {
        assertThrows(IllegalArgumentException.class, () -> existingUserInvitationService.sendFamilyInvitationToExistingUser(family, account, owner, invitationCode));
    }

    private static Stream<Arguments> createInputMatrix() {
        String invitationCode = "invitationCodeInAnyFormat";
        Account account = new Account();
        Account owner = new Account();
        Family family = new Family();
        return Stream.of(
                Arguments.of(null, null, null, null),
                Arguments.of(family, null, null, null),
                Arguments.of(family, account, null, null),
                Arguments.of(null, account, null, null),
                Arguments.of(null, account, owner, null),
                Arguments.of(family, account, owner, null),
                Arguments.of(family, null, owner, null),
                Arguments.of(null, null, owner, null),
                Arguments.of(null, null, owner, invitationCode),
                Arguments.of(family, null, owner, invitationCode),
                Arguments.of(null, account, owner, invitationCode),
                Arguments.of(null, account, null, invitationCode),
                Arguments.of(family, account, null, invitationCode),
                Arguments.of(family, null, null, invitationCode),
                Arguments.of(null, null, null, invitationCode)
        );
    }
}
