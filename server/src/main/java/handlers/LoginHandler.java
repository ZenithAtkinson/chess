package handlers;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import request.LoginRequest;
import response.LoginResult;
import Service.LoginService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final LoginService loginService;
    private final Gson gson = new Gson();

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.loginService = new LoginService(userDAO, authDAO);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // Deserialize the request object from JSON
        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

        // Process the request and get the result
        LoginResult result = loginService.login(loginRequest);

        // Serialize the result object to JSON
        response.type("application/json");
        return gson.toJson(result);
    }
}
