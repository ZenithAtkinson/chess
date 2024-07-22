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

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
            LoginResult loginResult = loginService.login(loginRequest);

            if (loginResult.authToken() != null) {
                response.status(200);
            } else {
                response.status(401);
            }

            return gson.toJson(loginResult);
        } catch (DataAccessException e) {
            response.status(401);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {}
}


