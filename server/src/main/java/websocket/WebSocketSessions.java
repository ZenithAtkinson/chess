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
import java.util.logging.Logger;

public class WebSocketSessions {
    private static final Logger logger = Logger.getLogger(WebSocketSessions.class.getName());
    private final Map<Integer, Set<Session>> sessionMap = new ConcurrentHashMap<>();

    public void addSessionToGame(int gameID, Session session) {
        logger.info("Adding session to gameID: " + gameID + ", session: " + session);
        sessionMap.computeIfAbsent(gameID, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
        logger.info("Current sessions for gameID " + gameID + ": " + sessionMap.get(gameID));
    }

    public void removeSessionFromGame(int gameID, Session session) {
        logger.info("Removing session from gameID: " + gameID + ", session: " + session);
        if (sessionMap.containsKey(gameID)) {
            sessionMap.get(gameID).remove(session);
            if (sessionMap.get(gameID).isEmpty()) {
                sessionMap.remove(gameID);
            }
        }
        logger.info("Current sessions for gameID " + gameID + ": " + sessionMap.getOrDefault(gameID, Collections.emptySet()));
    }

    public Set<Session> getSessionsForGame(int gameID) {
        return sessionMap.getOrDefault(gameID, Collections.emptySet());
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            logger.info("Sending message to session: " + session + ", message: " + message);
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

    public void removeSession(Session session) {
        logger.info("Removing session: " + session);
        sessionMap.values().forEach(sessions -> sessions.remove(session));
    }
}
