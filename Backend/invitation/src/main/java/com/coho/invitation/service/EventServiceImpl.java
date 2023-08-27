package com.coho.invitation.service;

import com.coho.invitation.dto.Event;
import com.coho.invitation.mapper.EventMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService{
    private final EventMapper eventMapper;

    public EventServiceImpl(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    @Override
    public List<Event> getEventList(String uid){
        return eventMapper.getEventList(uid);
    }
    @Override
    public List<Event> getEventsProgressing(String uid){
        return eventMapper.getEventsProgressing(uid);
    }
    @Override
    public List<Event> getEventsDone(String uid){
        return eventMapper.getEventsDone(uid);
    }
    @Override
    public Event getEvent(String eid){
        return eventMapper.getEvent(eid);
    }
    @Override
    public List<String> checkAuthority(String eid){
        return eventMapper.checkAuthority(eid);
    }
    @Override
    public void insertEvent(Event event){
        eventMapper.insertEvent(event);
    }
    @Override
    public void insertManage(String uid, String eid){
        eventMapper.insertManage(uid,eid);
    }
    @Override
    public void updateEvent(Event event){
        eventMapper.updateEvent(event);
    }
    @Override
    public void deleteEvent(String eid){
        eventMapper.deleteEvent(eid);
    }
    @Override
    public void deleteAuthority(String eid, String muid){
        eventMapper.deleteAuthority(eid,muid);
    }

}
