package com.coho.invitation.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event {
    private String eid;
    private String etype;
    private LocalDateTime edate;
    private String location;
    private String ehost;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getEtype() {
        return etype;
    }

    public void setEtype(String etype) {
        this.etype = etype;
    }

    public LocalDateTime getEdate() {
        return edate;
    }

    public void setEdate(LocalDateTime edate, DateTimeFormatter formatter) {
        this.edate = edate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEdate(LocalDateTime edate) {
        this.edate = edate;
    }

    public String getEhost() {
        return ehost;
    }

    public void setEhost(String ehost) {
        this.ehost = ehost;
    }
}
