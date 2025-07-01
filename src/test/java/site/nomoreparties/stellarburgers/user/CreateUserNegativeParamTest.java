package site.nomoreparties.stellarburgers.user;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.User;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserNegativeParamTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    private final String email;
    private final String password;
    private final String name;

    public CreateUserNegativeParamTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "email: {0}, password: {1}, name: {2}")
    public static Collection<Object[]> getTestData() {
        return Arrays.asList(new Object[][]{
                {null, "password123", "TestUser"},       // отсутствует email
                {"user@yandex.ru", null, "TestUser"},     // отсутствует password
                {"user@yandex.ru", "password123", null}   // отсутствует name
        });
    }

    @Test
    public void userCreationWithMissingFieldShouldReturnError() {
        User user = new User(email, password, name);

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}