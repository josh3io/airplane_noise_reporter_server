package com.threeio.airplanenoise.server;

/**
 * Created by jgoldberg on 7/29/15.
 */
public class AirplaneMessage {
    private String hex;
    private String squawk;
    private String flight;
    private Double lat;
    private Double lon;
    private Integer validposition;
    private Long altitude;
    private Long vert_rate;
    private Integer track;
    private Integer validtrack;
    private Integer speed;
    private Long messages;
    private Long seen;
    private Boolean mlat;

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getSquawk() {
        return squawk;
    }

    public void setSquawk(String squawk) {
        this.squawk = squawk;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
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

    public Integer getValidposition() {
        return validposition;
    }

    public void setValidposition(Integer validposition) {
        this.validposition = validposition;
    }

    public Long getAltitude() {
        return altitude;
    }

    public void setAltitude(Long altitude) {
        this.altitude = altitude;
    }

    public Long getVert_rate() {
        return vert_rate;
    }

    public void setVert_rate(Long vert_rate) {
        this.vert_rate = vert_rate;
    }

    public Integer getTrack() {
        return track;
    }

    public void setTrack(Integer track) {
        this.track = track;
    }

    public Integer getValidtrack() {
        return validtrack;
    }

    public void setValidtrack(Integer validtrack) {
        this.validtrack = validtrack;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Long getMessages() {
        return messages;
    }

    public void setMessages(Long messages) {
        this.messages = messages;
    }

    public Long getSeen() {
        return seen;
    }

    public void setSeen(Long seen) {
        this.seen = seen;
    }

    public Boolean getMlat() {
        return mlat;
    }

    public void setMlat(Boolean mlat) {
        this.mlat = mlat;
    }
}
