package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import request.LoginRequest;
import response.LoginResult;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;


public class LoginHandler implements Route {
    private final LoginService loginService;
    private final Gson gson = new Gson();

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.loginService = new LoginService(userDAO, authDAO);
    }

    @@Override
    // Handle the HTTP request to log in a user
    public Object handle(Request request, Response response) throws Exception {
        try {
            // Parse the login request from the request body
            LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
            // Service to log in the user
            LoginResult loginResult = loginService.login(loginRequest);

            if (loginResult.authToken() != null) {
                response.status(200); //OK
            } else {
                response.status(401); //Unauthorized
            }

            return gson.toJson(loginResult); //JSON
        } catch (DataAccessException e) {
            response.status(401); //Unauthorized
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage())); // Return error response as JSON
        } catch (Exception e) {
            response.status(500); // Internal Server Error
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage())); // Return error response as JSON
        }
    }

    //response record
    private record ErrorResponse(String message) {}
}


