package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.RegisterRequest;
import response.RegisterResult;
import service.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final RegisterService registerService;
    private final Gson gson = new Gson();

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public Object handle(Request req, Response res) {
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        RegisterResult result;
        try {
            result = registerService.register(request);
            res.status(200); //Success
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Missing required fields")) {
                res.status(400); //Bad Request
            } else if (e.getMessage().contains("User already exists")) {
                res.status(403); //Forbidden
            } else {
                res.status(500); //Internal Server Error
            }
            result = new RegisterResult(e.getMessage());
        } catch (Exception e) {
            res.status(500); //Internal Server Error
            result = new RegisterResult("Internal Server Error: " + e.getMessage());
        }
        return gson.toJson(result);
    }
}
