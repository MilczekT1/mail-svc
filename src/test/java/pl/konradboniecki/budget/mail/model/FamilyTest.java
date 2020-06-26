package pl.konradboniecki.budget.mail.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import pl.konradboniecki.budget.mail.service.RequestBodyValidator;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class FamilyTest {

    private String jsonWithFullFamily;
    private String jsonWithEmptyFamily;
    private String jsonWithFullFamilyAndAdditionalProp;
    private RequestBodyValidator validator;

    @BeforeAll
    public void setup() throws FileNotFoundException {
        jsonWithEmptyFamily = getJsonAsString("model/family-empty", "json");
        jsonWithFullFamily = getJsonAsString("model/family-full", "json");
        jsonWithFullFamilyAndAdditionalProp = getJsonAsString("model/family-additional-prop", "json");
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
        ObjectNode json = new ObjectMapper().readValue(jsonWithEmptyFamily, ObjectNode.class);

        Family family = validator.extractFamily("Family", json);

        Assertions.assertAll(
                () -> assertNull(family.getId()),
                () -> assertNull(family.getTitle())
        );
    }

    @Test
    public void givenJsonWithProperties_whenCreatingObject_thenPropertiesAreSet() throws IOException, BadRequestException {
        ObjectNode json = new ObjectMapper().readValue(jsonWithFullFamily, ObjectNode.class);

        Family family = validator.extractFamily("Family", json);

        Assertions.assertAll(
                () -> assertEquals(10, family.getId().longValue()),
                () -> assertEquals("test_title", family.getTitle())
        );
    }

    @Test
    public void givenJsonWithUnknownProperty_whenCreatingObject_thenPropertiesAreSet() throws IOException, BadRequestException {
        ObjectNode json = new ObjectMapper().readValue(jsonWithFullFamilyAndAdditionalProp, ObjectNode.class);

        Family family = validator.extractFamily("Family", json);

        Assertions.assertAll(
                () -> assertEquals(10, family.getId().longValue()),
                () -> assertEquals("test_title", family.getTitle())
        );
    }

    @Test
    public void givenJsonObjectNameNull_whenCreatingObject_thenExceptionIsThrown() throws IOException {
        String jsonObjectName = null;
        ObjectNode json = new ObjectMapper().readValue(jsonWithFullFamily, ObjectNode.class);

        assertThrows(BadRequestException.class, () -> {
            validator.extractFamily(jsonObjectName, json);
        });
    }

    @Test
    public void givenJsonObjectNameEmpty_whenCreatingObject_thenExceptionIsThrown() throws IOException {
        String jsonObjectName = "";
        ObjectNode json = new ObjectMapper().readValue(jsonWithFullFamily, ObjectNode.class);

        assertThrows(BadRequestException.class, () -> {
            validator.extractFamily(jsonObjectName, json);
        });
    }

    @Test
    public void givenJsonNull_whenCreatingObject_thenExceptionIsThrown() {
        String jsonObjectName = "name";
        ObjectNode json = null;

        assertThrows(NullPointerException.class, () -> {
            validator.extractFamily(jsonObjectName, json);
        });
    }
}
