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
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {

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

    @Test
    public void userCanCreateOrderWithAuthAndIngredients() {
        List<String> ingredients = getValidIngredients();
        Order order = new Order(ingredients);

        Response response = createOrderWithToken(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void userCanCreateOrderWithoutAuth() {
        List<String> ingredients = getValidIngredients();
        Order order = new Order(ingredients);

        Response response = createOrderWithoutToken(order);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Step("Получение валидных ингредиентов из API")
    private List<String> getValidIngredients() {
        return orderClient.getAvailableIngredients()
                .then()
                .statusCode(200)
                .extract()
                .path("data._id");
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

    @Step("Создание заказа с авторизацией")
    private Response createOrderWithToken(Order order, String token) {
        return orderClient.createOrder(order, token);
    }

    @Step("Создание заказа без авторизации")
    private Response createOrderWithoutToken(Order order) {
        return orderClient.createOrder(order, null);
    }
}