package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.User;
import org.junit.After;

import java.util.UUID;

import static org.hamcrest.Matchers.is;

public class CreateUserTest {

    private final UserClient userClient = new UserClient();

    @Test
    public void userCanBeCreated() {
        User user = generateRandomUser();

        Response response = userClient.createUser(user);

        checkUserCreated(response);
    }

    @Test
    public void createDuplicateUserReturnsError() {
        User user = generateRandomUser();

        // Первый раз создаём пользователя
        Response firstResponse = userClient.createUser(user);
        checkUserCreated(firstResponse);

        // Второй раз — тот же пользователь
        Response secondResponse = userClient.createUser(user);
        checkDuplicateUserError(secondResponse);
    }




    // ===== Allure Steps =====

    @Step("Генерация уникального пользователя")
    private User generateRandomUser() {
        return new User(
                UUID.randomUUID() + "@yandex.ru",
                "password123",
                "TestUser"
        );
    }

    @Step("Проверка, что пользователь создан успешно")
    private void checkUserCreated(Response response) {
        response.then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Step("Проверка, что второй запрос вернул ошибку — пользователь уже существует")
    private void checkDuplicateUserError(Response response) {
        response.then()
                .statusCode(403)
                .body("message", is("User already exists"));
    }
}