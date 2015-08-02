package com.threeio.airplanenoise.client;

/**
 * Created by jgoldberg on 7/24/15.
 */

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) throws  URISyntaxException, InterruptedException {
        while (true) {
            try {
                new Client().run(new URI("wss://localhost:8442/ws"));
            } catch (IOException e) {

            }
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void run(URI destinationUri) throws IOException, InterruptedException {

        SslContextFactory sslContextFactory = new SslContextFactory();
        Resource keyStoreResource = Resource.newResource(this.getClass().getResource("/cacerts.jks"));
        sslContextFactory.setKeyStoreResource(keyStoreResource);
        sslContextFactory.setKeyStorePassword("asdfasdf");
        sslContextFactory.setKeyManagerPassword("changeit");
        WebSocketClient client = new WebSocketClient(sslContextFactory);
        MyWebSocket socket = new MyWebSocket();
        try {
            while (true) {
                client.start();
                ClientUpgradeRequest request = new ClientUpgradeRequest();
                System.out.println("Connecting to : " + destinationUri);
                client.connect(socket, destinationUri, request);

                socket.awaitClose(10, TimeUnit.SECONDS);
                client.stop();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @WebSocket
    public class MyWebSocket {
        private final CountDownLatch closeLatch = new CountDownLatch(1);

        private final DumpClient dumpClient = new DumpClient();

        @OnWebSocketConnect
        public void onConnect(Session session) {
            System.out.println("WebSocket Opened in client side");
            try {
                dumpClient.sendDataToServer(session);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @OnWebSocketMessage
        public void onMessage(String message) {
            System.out.println("Message from Server: " + message);
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            System.out.println("WebSocket Closed. Code:" + statusCode);
        }

        public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
            return this.closeLatch.await(duration, unit);
        }
    }

}
