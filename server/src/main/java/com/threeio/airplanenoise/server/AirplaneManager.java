package com.threeio.airplanenoise.server;

import java.util.*;

/**
 * Created by jgoldberg on 7/25/15.
 */
public class AirplaneManager {
    private HashMap<String,Airplane> airplanes = new HashMap<String, Airplane>();


    public Airplane getAirplaneByHexIdent(String hexIdent) {
        Airplane thePlane = null;
        if (airplanes.containsKey(hexIdent)) {
            // update
            thePlane = airplanes.get(hexIdent);
        } else {
            // insert
            thePlane = new Airplane();
            thePlane.setHexIdent(hexIdent);
        }
        return thePlane;
    }

    public List<Airplane> getAirplanes() {
        SortedSet<Airplane> sortedAirplanes = new TreeSet<Airplane>();
        synchronized (airplanes) {
            sortedAirplanes.addAll(airplanes.values());
        }
        Iterator<Airplane> i = sortedAirplanes.iterator();
        List<Airplane> list = new ArrayList<Airplane>();
        Integer now = (int) (System.currentTimeMillis() / 1000L);
        while (i.hasNext()) {
            Airplane a = i.next();
            if (a.getLastUpdateTimestamp() > now - 60) {
                list.add(a);
            } else {
                synchronized (airplanes) {
                    airplanes.remove(a.getHexIdent());
                }
            }
        }
        return list;
    }

    public String updatePlaneWithMessage(String message) {
        List<String> messageList = Arrays.asList(message.split(","));

        String hexId = messageList.get(4);

        Airplane thePlane = getAirplaneByHexIdent(hexId);

        try {


            switch (Integer.parseInt(messageList.get(1))) {
                case 1:
                    break;
                case 2:
                    thePlane.setGroundSpeed(Integer.parseInt(messageList.get(12)));
                case 3:
                    thePlane.setAltitude(Integer.parseInt(messageList.get(11)));
                    thePlane.setLat(Double.parseDouble(messageList.get(14)));
                    thePlane.setLon(Double.parseDouble(messageList.get(15)));

                    StringBuffer dateTimeBuffer = new StringBuffer();
                    dateTimeBuffer.append(messageList.get(8));
                    dateTimeBuffer.append(" ");
                    dateTimeBuffer.append(messageList.get(9));
                    thePlane.setLastUpdate(dateTimeBuffer.toString());


                    break;
                case 4:
                    thePlane.setGroundSpeed(Integer.parseInt(messageList.get(12)));
                    break;
                case 5:
                    thePlane.setAltitude(Integer.parseInt(messageList.get(11)));
                    break;
                case 6:
                    System.out.println("case 6 convert " + messageList.get(11));
                    thePlane.setAltitude(Integer.parseInt(messageList.get(11)));
                    break;
            }
            synchronized (airplanes) {
                airplanes.put(hexId, thePlane);
            }
        } catch (NumberFormatException e) {
            System.err.println("bad number format from message "+message);
        }
        return hexId;
    }
}
