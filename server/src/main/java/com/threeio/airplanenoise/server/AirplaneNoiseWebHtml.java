package com.threeio.airplanenoise.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jgoldberg on 7/25/15.
 */
public class AirplaneNoiseWebHtml extends HttpServlet {

    private static String jdbcConnectionString = "jdbc:sqlserver://airplanenoise.database.windows.net:1433;database=airplanenoise;user=jgoldberg@airplanenoise;password=ivirFlept!6fF^l@WZd;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    private static AirplaneManager manager;

    public static void setAirplaneManager(AirplaneManager m) {
        manager = m;
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        //System.err.println("POST uri "+request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_OK);
        ResponseWriter writer = new ResponseWriter(response.getWriter());

        writer.write("OK 200");

    }

    private class UserResponse {
        private User user;
        public UserResponse(User u) { user = u; }
    }


    @Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
            IOException
    {

        //System.out.println("GET uri "+request.getRequestURI());
        //response.setStatus(HttpServletResponse.SC_OK);
        ResponseWriter writer = new ResponseWriter(response.getWriter());


            response.setContentType("text/html");
            writer.write("<h1>Hello!</h1>");
            writer.write("<p>If you want support, email <a href='mailto:josh+airplanenoisereport@3io.com'>The Author</a>.</p>");
            writer.write("<h2>Privacy policy:</h2><p>We don't keep any personal information, that is all on your phone.");
            writer.write("<br>When you send a noise complaint, we log only information about the aircraft and the time at which it was reported.");

    }


    private class ResponseWriter {
        private final PrintWriter writer;

        public ResponseWriter(PrintWriter p) {
            writer = p;
        }
        public void write(String s) {
            writer.println(s);
        }
        public void writeRowStart() {
            write("<tr>");
        }
        public void writeRowEnd() {
            write("</tr>");
        }
        public void writeCell(String s) {
            write("<td>"+s+"</td>");
        }
    }

}
