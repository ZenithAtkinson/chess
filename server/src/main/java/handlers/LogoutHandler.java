package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import service.LogoutService;
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
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("Authorization");

        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7); // Remove "Bearer " prefix
        }

        try {
            logoutService.logout(authToken);
            res.status(200);
            return gson.toJson(new LogoutResponse(true, "Logged out successfully"));
        } catch (Exception e) {
            res.status(401);
            return gson.toJson(new LogoutResponse(false, "Error: Unauthorized"));
        }
    }

    // Inner class for logout response
    private static class LogoutResponse {
        private boolean success;
        private String message;

        public LogoutResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
