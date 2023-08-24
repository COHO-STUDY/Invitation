package com.coho.invitation.mapper;

import com.coho.invitation.dto.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EventMapper {
    void insertEvent(Event event);
    void insertManage(@Param("muid") String uid, @Param("eid") String eid);
}
