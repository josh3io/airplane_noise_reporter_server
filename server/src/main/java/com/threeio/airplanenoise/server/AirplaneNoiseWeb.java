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
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jgoldberg on 7/25/15.
 */
public class AirplaneNoiseWeb extends HttpServlet {

    private static AirplaneManager manager;

    public static void setAirplaneManager(AirplaneManager m) {
        manager = m;
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        System.err.println("POST uri "+request.getRequestURI());

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
        System.out.println("login "+request.getParameter("username")+","+request.getParameter("password"));
        User u = new User();
        u.setUsername(request.getParameter("username"));

        writeJson(writer,new UserResponse(u));
    }
    private void doSignup(HttpServletRequest request, ResponseWriter writer) {
        System.out.println("signup "+request.getParameter("username")+","+request.getParameter("password"));
        User u = new User();
        u.setUsername(request.getParameter("username"));

        writeJson(writer, new UserResponse(u));
    }
    private void doApn(HttpServletRequest request, ResponseWriter writer) {
        System.out.println("APN " + request.getParameter("device"));
    }
    private void doLog(HttpServletRequest request, ResponseWriter writer) {
        System.out.println("log email "+request.getParameter("email"));
    }

    @Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response ) throws ServletException,
            IOException
    {


        System.out.println("GET uri "+request.getRequestURI());
System.out.println("username "+request.getParameter("username"));
        response.setStatus(HttpServletResponse.SC_OK);

        ResponseWriter writer = new ResponseWriter(response.getWriter());

        ArrayList<Airplane> initialList = (ArrayList<Airplane>) manager.getAirplanes();
        ArrayList<Airplane> list = new ArrayList<Airplane>();
        Iterator<Airplane> iterator = initialList.iterator();
        while (iterator.hasNext()) {
            Airplane p = iterator.next();
            System.out.println("airplane "+p.getHexIdent());
            if (p.getLat() != 0 && p.getLon() != 0) {
                list.add(p);
            }
        }
        if (request.getParameter("html") != null && request.getParameter("html").equals("1")) {
            response.setContentType("text/html");
            writer.write("<h1>Hello from HelloServlet</h1>");

            writer.write("<table>");
            Iterator<Airplane> i = list.iterator();
            while (i.hasNext()) {
                Airplane thePlane = i.next();
                writer.writeRowStart();
                writer.writeCell(thePlane.getHexIdent());
                writer.writeCell(String.valueOf(thePlane.getAltitude()));
                writer.writeCell(String.valueOf(thePlane.getLat()));
                writer.writeCell(String.valueOf(thePlane.getLon()));
                writer.writeCell(thePlane.getLastUpdate().toString());
                writer.writeRowEnd();
            }
            writer.write("</table>");
        } else {
            // default json
            writeJson(writer, new PlaneListOutput(list));
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
