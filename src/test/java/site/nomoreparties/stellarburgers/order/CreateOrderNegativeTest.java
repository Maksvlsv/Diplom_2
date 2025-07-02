package site.nomoreparties.stellarburgers.order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.client.OrderClient;
import site.nomoreparties.stellarburgers.client.UserClient;
import site.nomoreparties.stellarburgers.model.Order;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserCredentials;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class CreateOrderNegativeTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        user = User.getRandom();
        createUser(user);
        accessToken = loginUser(new UserCredentials(user.getEmail(), user.getPassword()));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    public void userCannotCreateOrderWithoutIngredients() {
        Order order = new Order(List.of());

        Response response = orderClient.createOrder(order, accessToken);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void userCannotCreateOrderWithInvalidIngredientHash() {
        Order order = new Order(List.of("invalid_ingredient_hash"));

        Response response = orderClient.createOrder(order, accessToken);

        response.then()
                .statusCode(500);
    }

    @Step("Создание пользователя через API")
    private void createUser(User user) {
        userClient.createUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Авторизация пользователя")
    private String loginUser(UserCredentials credentials) {
        User user = new User(credentials.getEmail(), credentials.getPassword(), null);
        return userClient.loginUser(user)
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }
}