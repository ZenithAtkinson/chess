package handlers;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import request.RegisterRequest;
import response.RegisterResult;
import Service.UserService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // Deserialize the request object from JSON
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);

        // Process the request and get the result
        RegisterResult result = userService.register(registerRequest);

        // Serialize the result object to JSON
        response.type("application/json");
        return gson.toJson(result);
    }
}
