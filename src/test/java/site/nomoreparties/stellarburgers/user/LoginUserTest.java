package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.User;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LoginUserTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;
    private User testUser;

    @Test
    public void userCanLoginWithValidCredentials() {
        testUser = generateRandomUser();
        Response createResponse = userClient.createUser(testUser);
        accessToken = createResponse.then().extract().path("accessToken");

        Response loginResponse = userClient.loginUser(testUser);
        checkLoginSuccess(loginResponse);
    }

    @Test
    public void loginFailsWithInvalidPassword() {
        testUser = generateRandomUser();
        userClient.createUser(testUser);

        User invalid = new User(testUser.getEmail(), "wrongPassword", null);

        Response loginResponse = userClient.loginUser(invalid);
        checkLoginFailed(loginResponse);
    }

    @Test
    public void loginFailsWithInvalidEmail() {
        testUser = generateRandomUser();
        userClient.createUser(testUser);

        User invalid = new User("nonexistent@yandex.ru", testUser.getPassword(), null);

        Response loginResponse = userClient.loginUser(invalid);
        checkLoginFailed(loginResponse);
    }

    @After
    @Step("Удаление пользователя после тестов")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }



    @Step("Генерация нового пользователя")
    private User generateRandomUser() {
        return new User(
                java.util.UUID.randomUUID() + "@yandex.ru",
                "password123",
                "TestUser"
        );
    }

    @Step("Проверка успешного входа")
    private void checkLoginSuccess(Response response) {
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Step("Проверка ошибки входа с неверными данными")
    private void checkLoginFailed(Response response) {
        response.then()
                .statusCode(401)
                .body("message", is("email or password are incorrect"));
    }
}