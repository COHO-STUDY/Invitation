package com.coho.invitation.service;

import com.coho.invitation.dto.Event;
import com.coho.invitation.mapper.EventMapper;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService{
    private final EventMapper eventMapper;

    public EventServiceImpl(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    @Override
    public void insertEvent(Event event){
        eventMapper.insertEvent(event);
    }

    @Override
    public void insertManage(String uid, String eid){
        eventMapper.insertManage(uid,eid);
    }


}
