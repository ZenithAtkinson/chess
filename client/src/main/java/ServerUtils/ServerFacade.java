package ServerUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import com.google.gson.Gson;
import model.UserData;
import model.AuthData;

public class ServerFacade {
    /**
     *     This will handle all the server HHTP requests from UI to the server/database
     */
    private static final String BASE_URL = "http://localhost:8080";
    private static final Gson gson = new Gson();

    public AuthData register(UserData request) {
        return execute("/user", "POST", request, AuthData.class);
    }

    public AuthData login(UserData request) {
        return execute("/login", "POST", request, AuthData.class);
    }

    private <T> T execute(String endpoint, String method, Object requestData, Class<T> responseType) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            if (requestData != null) {
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = gson.toJson(requestData).getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return gson.fromJson(content.toString(), responseType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
