package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import request.RegisterRequest;
import response.RegisterResult;
import service.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterHandler implements Route {
    private static final Logger LOGGER = Logger.getLogger(RegisterHandler.class.getName());
    private final RegisterService registerService;
    private final Gson gson = new Gson();

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public Object handle(Request req, Response res) {
        //LOGGER.info("Received registration request");
        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
        RegisterResult result;
        try {
            //LOGGER.info("Processing registration for user: " + request.getUsername());
            result = registerService.register(request);
            res.status(200); // Success
            //LOGGER.info("User registered successfully: " + request.getUsername());
        } catch (DataAccessException e) {
            //LOGGER.log(Level.SEVERE, "DataAccessException during registration: " + e.getMessage(), e);
            if (e.getMessage().contains("Missing required fields")) {
                res.status(400); // Bad Request
            } else if (e.getMessage().contains("User already exists")) {
                res.status(403); // Forbidden
            } else {
                res.status(500); // Internal Server Error
            }
            result = new RegisterResult(e.getMessage());
        } catch (Exception e) {
            //LOGGER.log(Level.SEVERE, "Exception during registration: " + e.getMessage(), e);
            res.status(500); // Internal Server Error
            result = new RegisterResult("Internal Server Error: " + e.getMessage());
        }
        //LOGGER.info("Registration response: " + gson.toJson(result));
        return gson.toJson(result);
    }
}
