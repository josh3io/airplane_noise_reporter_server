package com.threeio.airplanenoise.server;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Created by jgoldberg on 7/24/15.
 */
public class Airplane implements Comparable<Airplane> {
    public String getHexIdent() {
        return hexIdent;
    }

    public void setHexIdent(String hexIdent) {
        this.hexIdent = hexIdent;
    }

    public Long getAltitude() {
        return altitude;
    }

    public void setAltitude(Long altitude) {
        this.altitude = altitude;
    }

    public Integer getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(Integer groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdate(String timeStr) {
        lastUpdate = LocalDateTime.parse(timeStr,formatter);
        lastUpdateTimestamp = lastUpdate.toEpochSecond(ZoneOffset.UTC);
    }
    public void setLastUpdateTimestamp(Long timestamp) {
        lastUpdateTimestamp = timestamp;
        lastUpdate = LocalDateTime.ofEpochSecond(timestamp,0,ZoneOffset.UTC);
    }

    private String hexIdent = "";
    private LocalDateTime lastUpdate = null;
    private Long lastUpdateTimestamp = new Long(0);
    private Long altitude = new Long(0);
    private Integer groundSpeed = 0;
    private Double lat = 0.0;
    private Double lon = 0.0;
    private String flight = "";
    private Integer track = 0;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");


    public Airplane() {
    }


    public int compareTo(Airplane o) {
        if (o.getLastUpdateTimestamp() < this.getLastUpdateTimestamp()) {
            return -1;
        } else if (o.getLastUpdateTimestamp() > this.getLastUpdateTimestamp()) {
            return 1;
        } else {
            return 0;
        }
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public Integer getTrack() {
        return track;
    }

    public void setTrack(Integer track) {
        this.track = track;
    }
}
