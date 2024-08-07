package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessions {
    private final Map<Integer, Set<Session>> sessionMap = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameID, Session session) {
        sessionMap.computeIfAbsent(gameID, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session) {
        if (sessionMap.containsKey(gameID)) {
            sessionMap.get(gameID).remove(session);
            if (sessionMap.get(gameID).isEmpty()) {
                sessionMap.remove(gameID);
            }
        }
    }

    public Set<Session> getSessionsForGame(int gameID) {
        return sessionMap.getOrDefault(gameID, Collections.emptySet());
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    public void broadcastMessage(int gameID, ServerMessage message, Session exceptThisSession) throws IOException {
        for (Session session : getSessionsForGame(gameID)) {
            if (session != exceptThisSession) {
                sendMessage(session, message);
            }
        }
    }
}
