package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.User;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Test
    public void userCanUpdateDataWithAuth() {
        User user = generateRandomUser();
        accessToken = createUserAndGetToken(user);

        User updatedUser = new User("new_" + user.getEmail(), "newPassword123", "NewName");
        Response updateResponse = updateUserWithToken(accessToken, updatedUser);

        checkSuccessfulUpdate(updateResponse, updatedUser);
    }

    @Test
    public void userCannotUpdateDataWithoutAuth() {
        User updatedUser = new User("unauth@example.com", "somePassword", "NoAuthUser");
        Response updateResponse = updateUserWithToken(null, updatedUser);

        checkUnauthorizedError(updateResponse);
    }

    @After
    @Step("Удаление пользователя после теста")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }


    @Step("Генерация случайного пользователя")
    private User generateRandomUser() {
        return new User(
                UUID.randomUUID() + "@yandex.ru",
                "password123",
                "TestUser"
        );
    }

    @Step("Создание пользователя и получение токена")
    private String createUserAndGetToken(User user) {
        Response response = userClient.createUser(user);
        response.then().statusCode(200);
        return response.then().extract().path("accessToken");
    }

    @Step("Обновление данных пользователя (токен: {0})")
    private Response updateUserWithToken(String token, User updatedUser) {
        return userClient.updateUser(token, updatedUser);
    }

    @Step("Проверка успешного обновления данных")
    private void checkSuccessfulUpdate(Response response, User expectedUser) {
        response.then()
                .statusCode(200)
                .body("user.name", equalTo(expectedUser.getName()))
                .body("user.email", equalTo(expectedUser.getEmail()));
    }

    @Step("Проверка ошибки при обновлении без авторизации")
    private void checkUnauthorizedError(Response response) {
        response.then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}