package com.threeio.airplanenoise.server;

/**
 * Created by jgoldberg on 7/24/15.
 */
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.util.ArrayList;
import java.util.List;

public class AirplaneNoiseWebSocketServer {
    private Server server;
    private String host;
    private int port;
    private Resource keyStoreResource;
    private String keyStorePassword;
    private String keyManagerPassword;
    private List<Handler> webSocketHandlerList;
    private AirplaneManager manager;


    public void initData(AirplaneManager m) {

        manager = m;
        webSocketHandlerList = new ArrayList<Handler>();
    }
    public void initialize() {
        server = new Server();
        // connector configuration
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreResource(keyStoreResource);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);
        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(new HttpConfiguration());
        ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
        sslConnector.setHost(host);
        sslConnector.setPort(port);
        server.addConnector(sslConnector);
        // handler configuration

        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(webSocketHandlerList.toArray(new Handler[0]));
        server.setHandler(handlerCollection);
    }

    public void addWebSocket(final Class<?> webSocket, String pathSpec) {
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory webSocketServletFactory) {
                webSocketServletFactory.register(webSocket);
            }
        };
        ContextHandler wsContextHandler = new ContextHandler();
        wsContextHandler.setHandler(wsHandler);
        wsContextHandler.setContextPath(pathSpec);  // this context path doesn't work ftm
        webSocketHandlerList.add(wsHandler);
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }
    public void stop() throws Exception {
        server.stop();
        server.join();
    }

    public void setHost(String host) {
        this.host = host;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setKeyStoreResource(Resource keyStoreResource) {
        this.keyStoreResource = keyStoreResource;
    }
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }
    public void setKeyManagerPassword(String keyManagerPassword) {
        this.keyManagerPassword = keyManagerPassword;
    }

}
