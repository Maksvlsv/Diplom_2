package site.nomoreparties.stellarburgers.client;

import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.model.User;
import site.nomoreparties.stellarburgers.model.UserCredentials;

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

    // Новый метод: create() — обёртка над createUser
    public Response create(User user) {
        return createUser(user);
    }

    public Response loginUser(User user) {
        return given()
                .baseUri(BASE_URI)
                .basePath(BASE_PATH + "/login")
                .header("Content-type", "application/json")
                .body(user)
                .post();
    }

    // Новый метод: login() с использованием UserCredentials
    public Response login(UserCredentials credentials) {
        User user = new User(credentials.getEmail(), credentials.getPassword(), null);
        return loginUser(user);
    }

    public Response updateUser(String accessToken, User updatedUser) {
        var request = given()
                .baseUri(BASE_URI)
                .basePath("/api/auth/user")
                .header("Content-type", "application/json")
                .body(updatedUser);

        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }

        return request.patch();
    }
}