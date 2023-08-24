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
    void insertEvent(Event event);
    void insertManage(@Param("muid") String uid, @Param("eid") String eid);
}
