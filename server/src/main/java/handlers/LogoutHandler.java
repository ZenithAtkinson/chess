package handlers;

import dataaccess.AuthDAO;
import Service.LogoutService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final LogoutService logoutService;
    private final Gson gson = new Gson();

    public LogoutHandler(AuthDAO authDAO) {
        this.logoutService = new LogoutService(authDAO);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        // Get the auth token from the header
        String authToken = request.headers("Authorization");

        // Process the request
        logoutService.logout(authToken);

        // Return a success response
        response.type("application/json");
        response.status(200);
        return gson.toJson(new Object());
    }
}
