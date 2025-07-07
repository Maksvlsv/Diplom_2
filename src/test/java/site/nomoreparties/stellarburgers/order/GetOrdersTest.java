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

import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        user = User.getRandom();
        createUser(user);
        accessToken = loginUser(new UserCredentials(user.getEmail(), user.getPassword()));
        createOrderForUser(accessToken);
    }

    @Test
    public void authorizedUserCanGetOrders() {
        Response response = orderClient.getOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", not(empty()));
    }

    @Test
    public void unauthorizedUserCannotGetOrders() {
        Response response = orderClient.getOrders(null);

        response.then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }


    @Step("Создание пользователя")
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

    @Step("Создание хотя бы одного заказа")
    private void createOrderForUser(String token) {
        List<String> ingredients = orderClient.getAvailableIngredients()
                .then()
                .statusCode(200)
                .extract()
                .path("data._id");

        Order order = new Order(ingredients);

        orderClient.createOrder(order, token)
                .then()
                .statusCode(200);
    }
}