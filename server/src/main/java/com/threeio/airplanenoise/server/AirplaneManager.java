package com.threeio.airplanenoise.server;

import java.util.*;

/**
 * Created by jgoldberg on 7/25/15.
 */
public class AirplaneManager {
    private HashMap<String,Airplane> airplanes = new HashMap<String, Airplane>();
    public Integer airplaneCount = 0;

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
        List<Airplane> planeList = new ArrayList<Airplane>();
        System.out.println("airplanes "+airplanes.values().toArray().length);
        synchronized (airplanes) {
            planeList.addAll(airplanes.values());
        }
        return planeList;
        /*
        SortedSet<Airplane> sortedAirplanes = new TreeSet<Airplane>();
        synchronized (airplanes) {
            sortedAirplanes.addAll(airplanes.values());
        }
        Iterator<Airplane> i = sortedAirplanes.iterator();
        List<Airplane> list = new ArrayList<Airplane>();
        Integer now = (int) (System.currentTimeMillis() / 1000L);
        while (i.hasNext()) {
            Airplane a = i.next();
            if (a.getLastUpdateTimestamp() > now - 120) {
                list.add(a);
            } else {
                synchronized (airplanes) {
                    airplanes.remove(a.getHexIdent());
                }
            }
        }
        return list;*/
    }

    public void updatePlanesFromMessageObjectList(List<AirplaneMessage> messages) {

        HashMap<String,Airplane> newMap = new HashMap<String, Airplane>();
        Iterator<AirplaneMessage> i = messages.iterator();
        Integer count = 0;
        while (i.hasNext()) {
            count++;
            AirplaneMessage airplaneMessage = i.next();
            //System.out.println("updating airplane "+airplaneMessage.getHex());
            Airplane thePlane = getAndSetAirplaneByMessageObject(airplaneMessage);
            newMap.put(thePlane.getHexIdent(),thePlane);
        }
        synchronized (airplanes) {
            airplanes = newMap;
        }
        return;
    }
    public void updatePlaneWithMessageObject(AirplaneMessage mo) {
        Airplane thePlane = getAndSetAirplaneByMessageObject(mo);

        String hexId = thePlane.getHexIdent();
        synchronized (airplanes) {
            System.out.println("put "+hexId);
            airplanes.put(hexId, thePlane);
            airplaneCount++;
            System.out.println("count "+airplaneCount);
        }
        return;
    }

    private Airplane getAndSetAirplaneByMessageObject(AirplaneMessage mo) {
        String hexId = mo.getHex();
        Airplane thePlane = getAirplaneByHexIdent(hexId);
        if (mo.getAltitude() != null) {
            thePlane.setAltitude(mo.getAltitude());
        }
        if (mo.getFlight() != null) {
            thePlane.setFlight(mo.getFlight());
        }
        if (mo.getLat() != null) {
            thePlane.setLat(mo.getLat());
        }
        if (mo.getLon() != null) {
            thePlane.setLon(mo.getLon());
        }
        if (mo.getSpeed() != null) {
            thePlane.setGroundSpeed(mo.getSpeed());
        }
        if (mo.getTrack() != null) {
            thePlane.setTrack(mo.getTrack());
        }
        Long timestamp = (System.currentTimeMillis() / 1000L);
        thePlane.setLastUpdateTimestamp(timestamp);

        return thePlane;
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
                    thePlane.setAltitude(Long.parseLong(messageList.get(11)));
                    thePlane.setLat(Double.parseDouble(messageList.get(14)));
                    thePlane.setLon(Double.parseDouble(messageList.get(15)));

                    Long timestamp = (System.currentTimeMillis() / 1000L);
                    thePlane.setLastUpdateTimestamp(timestamp);

                    break;
                case 4:
                    thePlane.setGroundSpeed(Integer.parseInt(messageList.get(12)));
                    break;
                case 5:
                    thePlane.setAltitude(Long.parseLong(messageList.get(11)));
                    break;
                case 6:
                    System.out.println("case 6 convert " + messageList.get(11));
                    thePlane.setAltitude(Long.parseLong(messageList.get(11)));
                    break;
            }
            synchronized (airplanes) {
                airplanes.put(hexId, thePlane);
                airplaneCount++;
            }
        } catch (NumberFormatException e) {
            System.err.println("bad number format from message "+message);
        }
        return hexId;
    }
}
