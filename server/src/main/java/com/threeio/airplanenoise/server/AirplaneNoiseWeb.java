package com.threeio.airplanenoise.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.*;
import java.sql.*;

import com.microsoft.sqlserver.jdbc.*;

/**
 * Created by jgoldberg on 7/25/15.
 */
public class AirplaneNoiseWeb extends HttpServlet {

    private static String jdbcConnectionString = "jdbc:sqlserver://airplanenoise.database.windows.net:1433;database=airplanenoise;user=jgoldberg@airplanenoise;password=ivirFlept!6fF^l@WZd;encrypt=true;trustServerCertificate=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

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


        if (request.getRequestURI().equals("/login")) {
            doLogin(request,writer);
        } else if (request.getRequestURI().equals("/local-reg")) {
            doSignup(request, writer);
        } else if (request.getRequestURI().equals("/apn")) {
            doApn(request,writer);
        } else if (request.getRequestURI().equals("/log")) {
            doLog(request,writer);
        }

    }

    private class UserResponse {
        private User user;
        public UserResponse(User u) { user = u; }
    }
    private void doLogin(HttpServletRequest request, ResponseWriter writer) {
        //System.out.println("login "+request.getParameter("username")+","+request.getParameter("password"));
        User u = new User();
        u.setUsername(request.getParameter("username"));

        writeJson(writer,new UserResponse(u));
    }
    private void doSignup(HttpServletRequest request, ResponseWriter writer) {
        //System.out.println("signup "+request.getParameter("username")+","+request.getParameter("password"));
        User u = new User();
        u.setUsername(request.getParameter("username"));

        writeJson(writer, new UserResponse(u));
    }
    private void doApn(HttpServletRequest request, ResponseWriter writer) {
        //System.out.println("APN " + request.getParameter("device"));
    }
    private void doLog(HttpServletRequest request, ResponseWriter writer) {
        //System.out.println("log email "+request.getParameter("hexIdent"));
        Connection connection = null;
        PreparedStatement statement = null;


        try
        {
            // Ensure the SQL Server driver class is available.
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Establish the connection.
            connection = DriverManager.getConnection(jdbcConnectionString);

            // Define the SQL string.
            String sqlString = "INSERT INTO report_log (hexid,altitude,groundspeed,heading,lat,lon,report_dt,flight) " +
                    "VALUES (?,?,?,?,?,?,CURRENT_TIMESTAMP,?)";

            // Use the connection to create the SQL statement.

            statement = connection.prepareStatement(sqlString);

            statement.setString(1, request.getParameter("hexIdent"));
            statement.setInt(2, Integer.parseInt(request.getParameter("altitude")));
            statement.setInt(3, Integer.parseInt(request.getParameter("groundspeed")));
            statement.setInt(4, Double.valueOf(request.getParameter("heading")).intValue());
            statement.setDouble(5, Double.parseDouble(request.getParameter("lat")));
            statement.setDouble(6, Double.parseDouble(request.getParameter("lon")));
            if (request.getParameter("flight") != null) {
                statement.setString(7, request.getParameter("flight"));
            } else {
                statement.setString(7,"");
            }

            System.out.println("try to log");
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String s = paramNames.nextElement();
                System.out.println(s+": "+request.getParameter(s));
            }

            // Execute the statement.
            statement.executeUpdate();

            // Provide a message when processing is complete.
            //System.out.println("Processing complete.");

        }
        // Exception handling
        catch (ClassNotFoundException cnfe)
        {

            System.out.println("ClassNotFoundException " +
                    cnfe.getMessage());
        }
        catch (Exception e)
        {
            System.out.println("Exception " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                // Close resources.
                if (null != connection) connection.close();
                if (null != statement) statement.close();
            }
            catch (SQLException sqlException)
            {
                // No additional action if close() statements fail.
            }
        }
    }

    @Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
            IOException
    {

        //System.out.println("GET uri "+request.getRequestURI());
        //response.setStatus(HttpServletResponse.SC_OK);
        ResponseWriter writer = new ResponseWriter(response.getWriter());

        if (request.getRequestURI().equals("/log")) {
            doLog(request,writer);
            return;
        }

        ArrayList<Airplane> initialList = (ArrayList<Airplane>) manager.getAirplanes();
        ArrayList<Airplane> list = new ArrayList<Airplane>();
        Iterator<Airplane> iterator = initialList.iterator();
        while (iterator.hasNext()) {
            Airplane p = iterator.next();
            //System.out.println("airplane "+p.getHexIdent());
            if (p.getLat() != 0 && p.getLon() != 0 && p.getSeen() < 60) {
                list.add(p);
            }
        }

        if (request.getParameter("planefeed") != null && request.getParameter("planefeed").equals("1")) {
            // default json
            writeJson(writer, new PlaneListOutput(list));
        } else {
            response.setContentType("text/html");
            writer.write("OK 200");
        }
    }

    private void writeJson(ResponseWriter w, Object o) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.STATIC)
                .create();
        String json = gson.toJson(o);
        w.write(json);
    }

    private class PlaneListOutput {
        private ArrayList<Airplane> list = null;
        public PlaneListOutput(ArrayList<Airplane> a) {
            list = a;
        }
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
