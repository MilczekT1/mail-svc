package pl.konradboniecki.budget.mail.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.konradboniecki.budget.mail.model.Account;
import pl.konradboniecki.budget.mail.model.Family;
import pl.konradboniecki.budget.mail.service.ExistingUserInvitationService;
import pl.konradboniecki.budget.mail.service.MailService;
import pl.konradboniecki.budget.mail.service.NewUserInvitationService;
import pl.konradboniecki.budget.mail.service.RequestBodyValidator;
import pl.konradboniecki.chassis.tools.ChassisSecurityBasicAuthHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class InvitationToFamilyMailControllerTest {

    private String baseUrl;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private RequestBodyValidator validator;
    @MockBean
    private ExistingUserInvitationService existingUserInvitationService;
    @MockBean
    private NewUserInvitationService newUserInvitationService;
    @MockBean
    private MailService mailService;

    private String invitationToFamilyNewUserRequestAsString;
    private String invitationToFamilyExistingUserRequestAsString;
    private String invalidRequest;
    private HashMap<String, String> emptyUrlVariables = new HashMap<>();

    @BeforeAll
    public void setup() throws IOException {
        baseUrl = "http://localhost:" + port;
        String healthCheckUrl = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = rest.getForEntity(healthCheckUrl, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("{\"status\":\"UP\"}");

        invitationToFamilyNewUserRequestAsString = getFileContentAsString("controller/InvitationToFamilyForNewUserRequest", "json");
        invitationToFamilyExistingUserRequestAsString = getFileContentAsString("controller/InvitationToFamilyForExistingUserRequest", "json");
        invalidRequest = getFileContentAsString("controller/InvalidRequest", "json");
    }

    @Test
    public void givenNewUserInvitationRequest_whenSuccess_thenResponseIs200() {
        // Given:
        String url = baseUrl + "/api/mail/invite-user/new";
        doNothing().when(newUserInvitationService).sendFamilyInvitationToNewUser((any(Account.class)), any(Family.class), anyString());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<String> entity = new HttpEntity<>(invitationToFamilyNewUserRequestAsString, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenNewUserInvitationRequest_whenInvalidRequest_thenResponseIs400() {
        // Given:
        String url = baseUrl + "/api/mail/invite-user/new";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<String> entity = new HttpEntity<>(invalidRequest, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void givenOldUserInvitationRequest_whenSuccess_thenResponseIs200() {
        // Given:
        String url = baseUrl + "/api/mail/invite-user/existing";
        doNothing().when(existingUserInvitationService).sendFamilyInvitationToExistingUser(any(Family.class), any(Account.class), any(Account.class), any(String.class));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<String> entity = new HttpEntity<>(invitationToFamilyExistingUserRequestAsString, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    //TODO: this test should return internal server error
    public void givenOldUserInvitationRequest_whenFailure_thenResponseIsStill200() {
        // Given:
        String url = baseUrl + "/api/mail/invite-user/existing";
        doNothing().when(existingUserInvitationService).sendFamilyInvitationToExistingUser(any(Family.class), any(Account.class), any(Account.class), any(String.class));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<String> entity = new HttpEntity<>(invitationToFamilyExistingUserRequestAsString, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void givenOldUserInvitationRequest_whenInvalidRequest_thenResponseIs400() {
        // Given:
        String url = baseUrl + "/api/mail/invite-user/existing";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(ChassisSecurityBasicAuthHelper.getEncodedCredentials());
        HttpEntity<String> entity = new HttpEntity<>(invalidRequest, httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class, emptyUrlVariables);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void givenBAHeaderIsMissing_whenInviteExistingUser_thenUnauthorized() {
        // Given:
        String url = baseUrl + "/api/mail/invite-user/existing";
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void givenBAHeaderIsMissing_whenInviteNewUser_thenUnauthorized() {
        // Given:
        String url = baseUrl + "/api/mail/invite-user/new";
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        // When:
        ResponseEntity<Void> responseEntity = rest.exchange(url, HttpMethod.POST, entity, Void.class);
        //Then:
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String getFileContentAsString(String filename, String extension) throws FileNotFoundException {
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
}
