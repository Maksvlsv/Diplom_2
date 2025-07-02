package site.nomoreparties.stellarburgers.client;

import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String BASE_PATH = "/api/orders";

    public Response createOrder(Order order, String accessToken) {
        if (accessToken != null) {
            return given()
                    .baseUri(BASE_URI)
                    .basePath(BASE_PATH)
                    .header("Authorization", accessToken)
                    .header("Content-type", "application/json")
                    .body(order)
                    .post();
        } else {
            return given()
                    .baseUri(BASE_URI)
                    .basePath(BASE_PATH)
                    .header("Content-type", "application/json")
                    .body(order)
                    .post();
        }
    }

    public Response getAvailableIngredients() {
        return given()
                .baseUri(BASE_URI)
                .basePath("/api/ingredients")
                .get();
    }
}