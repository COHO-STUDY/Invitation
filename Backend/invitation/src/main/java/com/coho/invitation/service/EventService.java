package com.coho.invitation.service;

import com.coho.invitation.dto.Event;

import java.util.List;

public interface EventService {
    List<Event> getEventList(String uid);
    List<Event> getEventsProgressing(String uid);
    List<Event> getEventsDone(String uid);
    Event getEvent(String eid);
    void insertEvent(Event event);
    void insertManage(String uid, String eid);
}
