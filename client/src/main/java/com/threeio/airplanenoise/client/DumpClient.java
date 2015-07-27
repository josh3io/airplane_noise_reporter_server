package com.threeio.airplanenoise.client;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jgoldberg on 7/24/15.
 */


public class DumpClient {

    private HashMap<String,Long> lastSent = new HashMap<String, Long>();

    public DumpClient() {

    }

    public void sendDataToServer(Session session) throws IOException {
        Socket client = null;
        BufferedReader in = null;
        try {
            client = new Socket("192.168.1.175", 30003);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String line = null;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MSG,8") || line.startsWith("MSG,7") || line.startsWith("MSG,6")) {
                    continue;
                }
                List<String> messageList = Arrays.asList(line.split(","));

                String hexId = messageList.get(4);

                Long now = System.currentTimeMillis();

                if (!lastSent.containsKey(hexId) || (lastSent.containsKey(hexId) && lastSent.get(hexId) < now - 200)) {
                    System.out.println("Line: " + line);
                    session.getRemote().sendString(line);
                    lastSent.put(hexId, now);
                }
            }

        } catch (IOException e) {
            System.err.println("socket IO error: "+e.toString());
        } catch (WebSocketException e) {
            System.err.println("websocket error: "+e.toString());
        }
        in.close();
        client.close();
    }
}
