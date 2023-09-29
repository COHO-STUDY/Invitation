package com.coho.invitation.mapper;

import com.coho.invitation.dto.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ContactMapper {
    List<Contact> getContactList(@Param("uid")String uid,@Param("eid")String eid, @Param("con")String con);
    void addContacts(Contact contact);
    void checkSent(@Param("is_sent")Boolean is_sent,@Param("contactId")String contactId, @Param("muid")String uid,@Param("eid")String eid);
    void updateContact(Contact contact);
    void deleteContact(@Param("contactId")String contactId, @Param("muid")String uid,@Param("eid")String eid);
}
