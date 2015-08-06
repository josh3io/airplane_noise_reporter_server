package com.threeio.airplanenoise.server;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.util.resource.FileResource;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jgoldberg on 7/25/15.
 */
public class AirplaneNoiseServer {

    private static String HOST = "0.0.0.0";
    private static Integer SSL_PORT = 8080;
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
        Thread webHtmlThread = new Thread() {
            public void run() {
                try {
                    startHtmlWebServer(m);
                } catch (InterruptedException e) {
                    System.err.println("web html server thread interrupted: " + e.toString());
                } catch (Exception e){ }
            }
        };
        Thread webSocketThread = new Thread() {
            public void run() {
                try {
                    startWebSocketServer(m);
                } catch (InterruptedException e) {
                    System.err.println("web server thread interrupted: "+e.toString());
                } catch (Exception e) { e.printStackTrace();}
            }
        };
        Thread webScraperThread = new Thread() {
            private Gson gson = new GsonBuilder()
                    .excludeFieldsWithModifiers(Modifier.STATIC)
                    .create();
            public void run() {

                URL url = null;
                try {
                    url = new URL("http://josh3io.ddns.net:8008/data.json");
                } catch (MalformedURLException e) {
                    e.printStackTrace(System.err);
                }
                while (true) {
                    try {
                        URLConnection c = url.openConnection();
                        InputStream response = c.getInputStream();
                        String contentType = c.getHeaderField("Content-Type");
                        String charset = null;

                        for (String param : contentType.replace(" ", "").split(";")) {
                            if (param.startsWith("charset=")) {
                                charset = param.split("=", 2)[1];
                                break;
                            }
                        }
                        if (charset != null) {
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(response, charset));

                                StringBuilder b = new StringBuilder();
                                for (String line; (line = reader.readLine()) != null; ) {
                                    b.append(line);
                                }
                                String s = b.toString();
                                //System.out.println("parse "+s);
                                List<AirplaneMessage> messages = gson.fromJson(s, new TypeToken<List<AirplaneMessage>>() {
                                }.getType());
                                m.updatePlanesFromMessageObjectList(messages);

                            } catch (UnsupportedEncodingException e) {

                            }

                        }
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace(System.err);
                        }
                    } catch (IOException e) {
                        e.printStackTrace(System.err);

                    }

                }
            }
        };
        webThread.start();
        webHtmlThread.start();
        //webSocketThread.start();
        webScraperThread.start();



        webThread.join();
        webHtmlThread.join();
        //webSocketThread.join();
        webScraperThread.join();
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
    private static void startHtmlWebServer(AirplaneManager m) throws Exception {
        AirplaneNoiseWebHtmlServer webServer = new AirplaneNoiseWebHtmlServer();

        AirplaneNoiseWebHtml.setAirplaneManager(m);

        webServer.initData(m);
        webServer.setHost(HOST);
        webServer.setPort(80);
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
