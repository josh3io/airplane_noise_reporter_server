package com.threeio.airplanenoise.server;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import java.util.EnumSet;


/**
 * Created by jgoldberg on 7/25/15.
 */
public class AirplaneNoiseWebServer {

    private String host;
    private int port;
    private Resource keyStoreResource;
    private String keyStorePassword;
    private String keyManagerPassword;
    private Server server;
    private AirplaneManager manager;

    public void initData(AirplaneManager m) {
        manager = m;
    }

    public void initialize() {
        server = new Server(port);
        // connector configuration

        // uncomment when ready for ssl
        /*
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStoreResource(keyStoreResource);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);
        SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(port);
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
        ServerConnector sslConnector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);
        sslConnector.setHost(host);
        sslConnector.setPort(port);
        server.addConnector(sslConnector);
        */



        // handler configuration

        ServletContextHandler scHandler = new ServletContextHandler();
        scHandler.addServlet(AirplaneNoiseWeb.class, "/");
        scHandler.setContextPath("/");


        ServletContextHandler loginContextHandler = new ServletContextHandler();
        loginContextHandler.addServlet(AirplaneNoiseWeb.class, "/login");
        loginContextHandler.setContextPath("/login");
        loginContextHandler.setAllowNullPathInfo(true);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        RequestLogHandler requestLogHandler = new RequestLogHandler();

        contexts.setHandlers(new Handler[]{scHandler, loginContextHandler});
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{contexts,requestLogHandler});
        server.setHandler(handlers);

        NCSARequestLog requestLog = new NCSARequestLog("./logs/api-yyyy_mm_dd.request.log");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLogHandler.setRequestLog(requestLog);


    }

    public void start() throws Exception {
        server.start();
        server.join();
    }
    public void stop() throws Exception {
        server.stop();
        server.join();
    }


    public void setHost(String host) { this.host = host; }
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
