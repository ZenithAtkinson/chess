package ServerUtils;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import response.CreateGameResult;

public class ServerFacade {

    private final String serverUrl;
    private AuthData authData;
    private Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    public AuthData register(UserData request) throws ResponseException {
        var path = "/user";
        System.out.println("Sending register request: " + gson.toJson(request));
        AuthData out = this.makeRequest("POST", path, request, AuthData.class);
        if (out != null) {
            this.authData = out;
        }
        System.out.println("Received register response: " + gson.toJson(out));
        return out;
    }

    public AuthData login(UserData request) throws ResponseException {
        var path = "/session";
        System.out.println("Sending login request: " + gson.toJson(request));
        AuthData out = this.makeRequest("POST", path, request, AuthData.class);
        if (out != null) {
            this.authData = out;
        }
        System.out.println("Received login response: " + gson.toJson(out));
        return out;
    }

    public void clearDatabase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, Void.class);
    }

    public GameData createGame(CreateGameRequest request) throws ResponseException {
        var path = "/game";
        System.out.println("Sending create game request: " + gson.toJson(request));
        GameData out = this.makeRequest("POST", path, request, GameData.class);
        System.out.println("Received create game response: " + gson.toJson(out));
        return out;
    }

    public GameData[] listGames() throws ResponseException {
        var path = "/game";
        System.out.println("Sending list games request with auth token: " + (authData != null ? authData.getAuthToken() : "No Auth Token"));
        GameData[] out = this.makeRequest("GET", path, null, GameData[].class);
        System.out.println("Received list games response: " + gson.toJson(out));
        return out;
    }

    public void joinGame(GameData request) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, request, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = new URL(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authData != null && authData.getAuthToken() != null) {
                String authHeader = "Bearer " + authData.getAuthToken();
                http.addRequestProperty("Authorization", authHeader);
                System.out.println("Auth Token Sent: " + authHeader);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);

            // Read the response body
            String responseBody = readResponseBody(http);
            System.out.println("Received response body: " + responseBody);

            // Deserialize the response body
            T response = gson.fromJson(responseBody, responseClass);
            System.out.println("Deserialized response: " + gson.toJson(response));

            return response;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            String errorResponseBody = readErrorResponseBody(http);
            throw new ResponseException(status, "failure, HTTP connection not successful: " + status + ", " + errorResponseBody);
        }
    }

    private static String readResponseBody(HttpURLConnection http) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine);
            }
        }
        return responseBody.toString();
    }

    private static String readErrorResponseBody(HttpURLConnection http) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(http.getErrorStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine);
            }
        }
        return responseBody.toString();
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
