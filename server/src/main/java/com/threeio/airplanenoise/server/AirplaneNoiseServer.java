package com.threeio.airplanenoise.server;


import org.eclipse.jetty.util.resource.FileResource;

/**
 * Created by jgoldberg on 7/25/15.
 */
public class AirplaneNoiseServer {

    private static String HOST = "192.168.1.125";
    private static Integer SSL_PORT = 8441;
    private static Integer WSS_PORT = 8442;
    private static String KEYSTORE_PATH = "/keystore.jks";
    private static String STORE_PASSWORD = "asdfasdf";
    private static String KEY_MANAGER_PASSWORD = "changeit";

    public static void main(String[] args) throws Exception {

        final AirplaneManager m = new AirplaneManager();

        Thread webThread = new Thread() {
            public void run() {
                try {
                    startWebServer(m);
                } catch (InterruptedException e) {
                    System.err.println("web server thread interrupted: "+e.toString());
                } catch (Exception e){ }
            }
        };
        Thread webSocketThread = new Thread() {
            public void run() {
                try {
                    startWebSocketServer(m);
                } catch (InterruptedException e) {
                    System.err.println("web server thread interrupted: "+e.toString());
                } catch (Exception e) { }
            }
        };
        webThread.start();
        webSocketThread.start();
        webThread.join();
        webSocketThread.join();
    }

    private static void startWebServer(AirplaneManager m) throws Exception {
        AirplaneNoiseWebServer webServer = new AirplaneNoiseWebServer();

        AirplaneNoiseWeb.setAirplaneManager(m);

        webServer.initData(m);
        webServer.setHost(HOST);
        webServer.setPort(SSL_PORT);
        webServer.setKeyStoreResource(new FileResource(AirplaneNoiseWebSocketServer.class.getResource(KEYSTORE_PATH)));
        webServer.setKeyStorePassword(STORE_PASSWORD);
        webServer.setKeyManagerPassword(KEY_MANAGER_PASSWORD);
        webServer.initialize();
        webServer.start();
    }

    private static void startWebSocketServer(AirplaneManager m) throws Exception {
        AirplaneNoiseWebSocketServer webSocketServer = new AirplaneNoiseWebSocketServer();

        AirplaneNoiseWebSocket.setAirplaneManager(m);

        webSocketServer.initData(m);
        webSocketServer.setHost(HOST);
        webSocketServer.setPort(WSS_PORT);
        webSocketServer.setKeyStoreResource(new FileResource(AirplaneNoiseWebSocketServer.class.getResource(KEYSTORE_PATH)));
        webSocketServer.setKeyStorePassword(STORE_PASSWORD);
        webSocketServer.setKeyManagerPassword(KEY_MANAGER_PASSWORD);
        webSocketServer.addWebSocket(AirplaneNoiseWebSocket.class, "/ws");
        webSocketServer.initialize();
        webSocketServer.start();
    }

}
