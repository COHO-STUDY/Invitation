package com.coho.invitation.service;

import com.coho.invitation.dto.Event;

public interface EventService {
    void insertEvent(Event event);
    void insertManage(String uid, String eid);
}
