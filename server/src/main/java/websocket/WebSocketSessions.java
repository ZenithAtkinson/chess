package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessions {
    private final Map<Integer, Set<Session>> sessions = new ConcurrentHashMap<>();

    public void addSession(int gameId, Session session) {
        sessions.computeIfAbsent(gameId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
    }

    public void removeSession(Session session) {
        for (Set<Session> sessionSet : sessions.values()) {
            sessionSet.remove(session);
        }
    }

    public void removeSession(int gameId, Session session) {
        Set<Session> sessionSet = sessions.get(gameId);
        if (sessionSet != null) {
            sessionSet.remove(session);
        }
    }

    public void broadcast(String message, int gameId, Session exclude) throws IOException {
        Set<Session> sessionSet = sessions.get(gameId);
        if (sessionSet != null) {
            for (Session ses : sessionSet) {
                if (ses != exclude) {
                    sendMessage(ses, message);
                }
            }
        }
    }

    public void sendError(Session session, String message) throws IOException {
        ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, message);
        sendMessage(session, new Gson().toJson(error));
    }

    public void sendMessage(Session session, String message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }

    public void clear() {
        sessions.clear();
    }
}
