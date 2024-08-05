package ServerUtils;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.util.List;


import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.ListGamesResult;

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

    //Double check AuthData values
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
    // Double check AuthData values
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
    // Removes everything, very useful in tests (mostly used there)
    public void clearDatabase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, Void.class);
    }
    //Create a game with request
    public GameData createGame(CreateGameRequest request) throws ResponseException {
        var path = "/game";
        System.out.println("Sending create game request: " + gson.toJson(request));
        GameData out = this.makeRequest("POST", path, request, GameData.class);
        System.out.println("Received create game response: " + gson.toJson(out));
        return out;
    }
    //List games, doesnt use request.
    public List<GameData> listGames() throws ResponseException {
        var path = "/game";
        ListGamesResult response = this.makeRequest("GET", path, null, ListGamesResult.class);
        System.out.println("Received list games response: " + gson.toJson(response));
        return response.games();  // .games() method of record to retrieve the list. Doesnt catch null ptr...
    }
    //Uses JoinGameRequest to join
    public void joinGame(JoinGameRequest request) throws ResponseException {
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

            //Read response body
            String responseBody = readResponseBody(http);
            System.out.println("Received response body: " + responseBody);

            //Deserialize  response body
            if (responseClass != null) {
                T response = gson.fromJson(responseBody, responseClass);
                System.out.println("Deserialized response: " + gson.toJson(response));
                return response;
            }
            return null;
        } catch (Exception ex) {
            throw new ResponseException(500, "make request error:" + ex.getMessage());
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

    //Error catcher
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

    /**
     * Reads the error response body from an HttpURLConnection.
     * @param http The HttpURLConnection object from which to read the error response.
     * @return A string containing the entire error response body.
     * @throws IOException If an I/O error occurs.
     */
    private static String readErrorResponseBody(HttpURLConnection http) throws IOException {
        StringBuilder responseBody = new StringBuilder();

        //BufferedReader: for to read from the error of the HttpURLConnection
        try (BufferedReader in = new BufferedReader(new InputStreamReader(http.getErrorStream()))) {
            String inputLine;
            //append continually to body
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine);
            }
        }

        return responseBody.toString();
    }


    //PetShop method of finding error codes
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
