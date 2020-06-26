package pl.konradboniecki.budget.mail.contractbases;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import pl.konradboniecki.budget.mail.MailServiceApp;
import pl.konradboniecki.budget.mail.model.Account;
import pl.konradboniecki.budget.mail.service.MailService;
import pl.konradboniecki.budget.mail.service.ResetPasswordService;

import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = MailServiceApp.class,
        webEnvironment = WebEnvironment.RANDOM_PORT
)
public abstract class PasswordMgtClientBase {

    @LocalServerPort
    int port;
    @MockBean
    protected MailService mailService;
    @MockBean
    protected ResetPasswordService resetPasswordService;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        RestAssured.config = config().redirect(redirectConfig().followRedirects(false));
        Account existingAccount = new Account()
                .setId(2L)
                .setFirstName("kon")
                .setLastName("bon")
                .setEmail("test@mail.com");
        String resetCode = "29431ce1-8282-4489-8dd9-50f91e4c5653";

        doNothing().when(resetPasswordService).sendNewPasswordActivationLink(eq(existingAccount), eq(resetCode));
    }
}
