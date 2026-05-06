package com.kmaebashi.sporder.websocket;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/endpoint")
public class WebSocketEndpoint {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("open: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        session.getBasicRemote().sendText("echo: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("close: " + session.getId());
    }

}
