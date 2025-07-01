package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.User;

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
}