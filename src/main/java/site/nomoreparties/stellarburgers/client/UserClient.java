package site.nomoreparties.stellarburgers.client;

import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.model.User;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    public Response createUser(User user) {
        return given()
                .baseUri(BASE_URI)
                .basePath("/api/auth/register")
                .header("Content-type", "application/json")
                .body(user)
                .post();
    }
}