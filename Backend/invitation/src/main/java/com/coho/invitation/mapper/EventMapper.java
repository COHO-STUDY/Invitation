package com.coho.invitation.mapper;

import com.coho.invitation.dto.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EventMapper {
    List<Event> getEventList(String uid);
    List<Event> getEventsProgressing(String uid);
    List<Event> getEventsDone(String uid);
    Event getEvent(String eid);
    List<String> checkAuthority(String eid);
    void insertEvent(Event event);
    void insertManage(@Param("muid") String uid, @Param("eid") String eid);
    void updateEvent(Event event);
    void deleteEvent(String eid);
    void deleteAuthority(@Param("eid") String eid, @Param("muid") String muid);
    void deleteManage(String muid);
    List<String> findEventId();
}
