package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.RegisterRequest;
import response.RegisterResult;
import service.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.HttpURLConnection;

public class RegisterHandler implements Route {
    private final RegisterService registerService;
    private final Gson gson = new Gson();

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);
        try {
            RegisterResult result = registerService.register(registerRequest);
            response.status(HttpURLConnection.HTTP_OK);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            if ("User already exists".equals(e.getMessage())) {
                response.status(HttpURLConnection.HTTP_FORBIDDEN);
            } else {
                response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
            return gson.toJson(new RegisterResult(e.getMessage()));
        }
    }
}
