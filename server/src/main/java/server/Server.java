package server;

import spark.Spark;
import handlers.*;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import service.ClearService;
import service.RegisterService;

public class Server {

    public int run(int desiredPort) {
        // Set the port
        Spark.port(desiredPort);

        // Set the location for static files
        Spark.staticFiles.location("web");

        //Initialize DAO'ss
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        //Initialize services
        RegisterService registerService = new RegisterService(userDAO, authDAO);

        //Register handlers
        Spark.post("/user", new RegisterHandler(registerService));
        Spark.post("/session", new LoginHandler(userDAO, authDAO));
        Spark.delete("/session", new LogoutHandler(authDAO));
        Spark.get("/game", new ListGamesHandler(gameDAO, authDAO));
        Spark.post("/game", new CreateGameHandler(gameDAO, authDAO));
        Spark.put("/game", new JoinGameHandler(gameDAO, authDAO));
        Spark.delete("/db", new ClearHandler(new ClearService(userDAO, gameDAO, authDAO)));

        // Exception handling
        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body("{\"message\":\"Internal Server Error: " + exception.getMessage() + "\"}");
        });

        // Await initialization
        Spark.awaitInitialization();

        // Return the actual port the server is running on
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }
}
