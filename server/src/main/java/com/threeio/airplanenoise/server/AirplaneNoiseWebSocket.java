/**
 * Created by jgoldberg on 7/24/15.
 */
package com.threeio.airplanenoise.server;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;


@WebSocket
public class AirplaneNoiseWebSocket {
    private RemoteEndpoint remote;

    private static AirplaneManager manager;

    public static void setAirplaneManager(AirplaneManager m) {
        manager = m;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket Opened");
        this.remote = session.getRemote();
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Message from Client: " + message);

        String hexIdent = manager.updatePlaneWithMessage(message);

        Airplane thePlane = manager.getAirplaneByHexIdent(hexIdent);

            if (thePlane.getLat() != 0.0 && thePlane.getLon() != 0.0) {
                System.out.println("plane "+thePlane.getHexIdent()+" altitude "+thePlane.getAltitude()+" coords ("+thePlane.getLat()+","+thePlane.getLon()+"), last update "+thePlane.getLastUpdate().toString());
            }


    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket Closed. Code:" + statusCode);
    }
}
