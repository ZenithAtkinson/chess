package handlers;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.HttpURLConnection;

public abstract class HandlerForHttps<T> implements Route {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        String authToken = request.headers("Authorization");

        T requestObject = null;
        Class<T> requestClass = getRequestClass();
        if (requestClass != null) {
            requestObject = gson.fromJson(request.body(), requestClass);
        }

        Object result = getResult(requestObject, authToken);

        response.type("application/json");
        response.status(HttpURLConnection.HTTP_OK);

        return gson.toJson(result);
    }

    protected abstract Class<T> getRequestClass();

    protected abstract Object getResult(T request, String authToken) throws Exception;
}
