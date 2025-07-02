package site.nomoreparties.stellarburgers.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_PATH = "/api/orders";

    @Step("Создание заказа с токеном: {token}")
    public Response createOrder(Order order, String token) {
        if (token != null) {
            return given()
                    .header("Authorization", token)
                    .header("Content-type", "application/json")
                    .body(order)
                    .when()
                    .post(BASE_PATH);
        } else {
            return createOrderWithoutToken(order);
        }
    }

    @Step("Создание заказа без токена")
    public Response createOrderWithoutToken(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(BASE_PATH);
    }
}