package pl.konradboniecki.budget.mail.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.konradboniecki.budget.mail.model.Account;
import pl.konradboniecki.budget.mail.model.Family;
import pl.konradboniecki.chassis.exceptions.BadRequestException;

@Slf4j
@Service
public class RequestBodyValidator {

    public Account extractAccount(String jsonObjectName, @NonNull ObjectNode json) {
        if (StringUtils.isBlank(jsonObjectName))
            throw new BadRequestException("Invalid input");
        JsonNode accNode = json.get(jsonObjectName);
        if (accNode == null) {
            throw new BadRequestException("object not present in json");
        }
        return getAccountProperties(accNode);
    }

    private Account getAccountProperties(JsonNode accNode) {
        Account acc = new Account();
        if (accNode.has("id")) acc.setId(accNode.get("id").asLong());
        if (accNode.has("firstName")) acc.setFirstName(accNode.get("firstName").asText());
        if (accNode.has("lastName")) acc.setLastName(accNode.get("lastName").asText());
        //TODO: Pattern.matches("\\w+@\\w+.[a-zA-Z]+", account.getEmail());
        if (accNode.has("email")) acc.setEmail(accNode.get("email").asText());
        return acc;
    }

    public Family extractFamily(String jsonObjectName, @NonNull ObjectNode json) {
        if (StringUtils.isBlank(jsonObjectName))
            throw new BadRequestException("Invalid input");

        JsonNode familyNode = json.get(jsonObjectName);
        if (familyNode == null) {
            throw new BadRequestException("object not present in json");
        }
        return getFamilyProperties(familyNode);
    }

    private Family getFamilyProperties(JsonNode familyNode) {
        Family family = new Family();
        if (familyNode.has("id")) family.setId(familyNode.get("id").asLong());
        if (familyNode.has("title")) family.setTitle(familyNode.get("title").asText());
        return family;
    }

    public String extractStringValue(String propertyName, ObjectNode json) {
        JsonNode stringNode = json.get(propertyName);
        if (stringNode != null) {
            return stringNode.asText();
        } else {
            throw new BadRequestException(propertyName + " is missing in payload.");
        }
    }
}
