package handlers;

import com.google.gson.Gson;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearHandler implements Route {
    private final ClearService clearService;
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(ClearHandler.class);

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            LOGGER.debug("Handling clear request");
            clearService.clear();
            res.status(200);
            return gson.toJson(new ResponseMessage("Clear successful"));
        } catch (Exception e) {
            LOGGER.error("Error during clear: {}", e.getMessage());
            res.status(500);
            return gson.toJson(new ResponseMessage("Internal Server Error: " + e.getMessage()));
        }
    }

    private record ResponseMessage(String message) {
    }
}
