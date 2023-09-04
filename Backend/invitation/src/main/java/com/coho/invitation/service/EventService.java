package com.coho.invitation.service;

import com.coho.invitation.dto.Event;

import java.util.List;

public interface EventService {
    List<Event> getEventList(String uid);
    List<Event> getEventsProgressing(String uid);
    List<Event> getEventsDone(String uid);
    Event getEvent(String eid);
    List<String> checkAuthority(String eid);
    void insertEvent(Event event);
    void insertManage(String uid, String eid);
    void updateEvent(Event event);
    void deleteEvent(String eid);
    void deleteAuthority(String eid, String muid);
}
