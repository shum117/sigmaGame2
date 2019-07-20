/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author anton
 */
@WebSocket
public class MyWebSocketHandler {
    public static class News{
        public int id;
        public HashMap<String,String> additional_params;
    } 
    public static class NewsDto{
        public int current_period;
        public ArrayList<News> news;
    } 
    
    private static final HashSet<Session> connections = new HashSet<>();
    private Session ss = null;
    private static final Gson gson = new GsonBuilder().setLenient().create();

    public static void broadcast(NewsDto o) {
        for (Session connection : connections) {
            try {
                System.out.println("WebSocket: send to " + connection.getRemote().toString());
                connection.getRemote().sendString(gson.toJson(o));
            } catch (IOException ex) {
                Logger.getLogger(MyWebSocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        connections.remove(ss);
        ss = null;
        System.out.println("WebSocket: Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        if (ss == null)
            System.out.println("WebSocket: sudden null");
        System.out.println("WebSocket: Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        ss = session;
        connections.add(ss);
        System.out.println("WebSocket: Connect: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        if (ss == null)
            System.out.println("WebSocket: sudden null");
        System.out.println("WebSocket: Message: " + message);
    }
}