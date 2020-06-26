package pl.konradboniecki.budget.mail.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import pl.konradboniecki.budget.mail.service.RequestBodyValidator;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class AccountTest {

    private String jsonWithFullAccount;
    private String jsonWithEmptyAccount;
    private String jsonWithFullAccountAndAdditionalProp;
    private RequestBodyValidator validator;

    @BeforeAll
    public void setup() throws FileNotFoundException {
        jsonWithEmptyAccount = getJsonAsString("model/account-empty", "json");
        jsonWithFullAccount = getJsonAsString("model/account-full", "json");
        jsonWithFullAccountAndAdditionalProp = getJsonAsString("model/account-additional-prop", "json");
        validator = new RequestBodyValidator();
    }

    private String getJsonAsString(String filename, String extension) throws FileNotFoundException {
        File file = new File(getClass().getClassLoader().getResource(filename + "." + extension).getFile());
        assertTrue(file.exists());

        StringBuilder result = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    @Test
    public void givenJsonWithoutProperties_whenCreatingObject_thenPropertiesNotSet() throws IOException, BadRequestException {
        ObjectNode json = new ObjectMapper().readValue(jsonWithEmptyAccount, ObjectNode.class);

        Account account = validator.extractAccount("Account", json);

        Assertions.assertAll(
                () -> assertNull(account.getId()),
                () -> assertNull(account.getEmail()),
                () -> assertNull(account.getFirstName()),
                () -> assertNull(account.getLastName())
        );
    }

    @Test
    public void givenJsonWithProperties_whenCreatingObject_thenPropertiesAreSet() throws IOException, BadRequestException {
        ObjectNode json = new ObjectMapper().readValue(jsonWithFullAccount, ObjectNode.class);

        Account account = validator.extractAccount("Account", json);

        Assertions.assertAll(
                () -> assertEquals(2, account.getId().longValue()),
                () -> assertEquals("test@mail.com", account.getEmail()),
                () -> assertEquals("kon", account.getFirstName()),
                () -> assertEquals("bon", account.getLastName())
        );
    }

    @Test
    public void givenJsonWithUnknownProperty_whenCreatingObject_thenPropertiesAreSet() throws IOException, BadRequestException {
        ObjectNode json = new ObjectMapper().readValue(jsonWithFullAccountAndAdditionalProp, ObjectNode.class);

        Account account = validator.extractAccount("Account", json);

        Assertions.assertAll(
                () -> assertEquals(2, account.getId().longValue()),
                () -> assertEquals("test@mail.com", account.getEmail()),
                () -> assertEquals("kon", account.getFirstName()),
                () -> assertEquals("bon", account.getLastName())
        );
    }

    @Test
    public void givenJsonObjectNameNull_whenCreatingObject_thenExceptionIsThrown() throws IOException {
        String jsonObjectName = null;
        ObjectNode json = new ObjectMapper().readValue(jsonWithFullAccount, ObjectNode.class);

        assertThrows(BadRequestException.class, () -> {
            validator.extractAccount(jsonObjectName, json);
        });
    }

    @Test
    public void givenJsonObjectNameEmpty_whenCreatingObject_thenExceptionIsThrown() throws IOException {
        String jsonObjectName = "";
        ObjectNode json = new ObjectMapper().readValue(jsonWithFullAccount, ObjectNode.class);

        assertThrows(BadRequestException.class, () -> {
            validator.extractAccount(jsonObjectName, json);
        });
    }

    @Test
    public void givenJsonNull_whenCreatingObject_thenExceptionIsThrown() {
        String jsonObjectName = "name";
        ObjectNode json = null;

        assertThrows(NullPointerException.class, () -> {
            validator.extractAccount(jsonObjectName, json);
        });
    }
}
