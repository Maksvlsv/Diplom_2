package site.nomoreparties.stellarburgers.client;

import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.model.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    private static final String BASE_PATH = "/api/auth";

    public Response deleteUser(String accessToken) {
        return given()
                .baseUri(BASE_URI)
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_PATH + "/user");
    }

    public Response createUser(User user) {
        return given()
                .baseUri(BASE_URI)
                .basePath("/api/auth/register")
                .header("Content-type", "application/json")
                .body(user)
                .post();
    }
}