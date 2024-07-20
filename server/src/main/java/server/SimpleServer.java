package server;

import spark.*;

public class SimpleServer {

    public static void main(String[] args) {
        Spark.port(8080);
        Spark.staticFiles.location("/web");

        Spark.get("/hello", (req, res) -> "Hello, World!");

        Spark.init();
        Spark.awaitInitialization();
    }
}
