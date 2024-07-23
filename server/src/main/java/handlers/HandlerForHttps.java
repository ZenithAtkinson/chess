package handlers;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.HttpURLConnection;

public abstract class HandlerForHttps<T> implements Route {

    @Override
    // HTTP request
    public Object handle(Request request, Response response) throws Exception {
        Gson gson = new Gson();
        String authToken = request.headers("Authorization");

        T requestObject = null;
        //Get the class of the request object
        Class<T> requestClass = getRequestClass();
        if (requestClass != null) {
            //Parse the request body to the request object
            requestObject = gson.fromJson(request.body(), requestClass);
        }

        Object result = getResult(requestObject, authToken);

        response.type("application/json");
        response.status(HttpURLConnection.HTTP_OK);
        //JSON
        return gson.toJson(result);
    }
    //class of the request object
    protected abstract Class<T> getRequestClass();
    //result from the request object and auth token
    protected abstract Object getResult(T request, String authToken) throws Exception;
}
