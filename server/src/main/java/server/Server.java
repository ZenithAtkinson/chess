package server;

import spark.*;

import java.net.HttpURLConnection;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Example endpoint
        Spark.get("/hello", (req, res) -> "Hello, World!");

        //This line initializes the server and can be removed once you have a functioning endpoint

        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
