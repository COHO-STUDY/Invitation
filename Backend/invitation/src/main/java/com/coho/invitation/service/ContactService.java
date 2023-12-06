package com.coho.invitation.service;

import com.coho.invitation.dto.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactService {
    List<Contact> getKakaoFriends(String accessToken, String uid, String eid);
    List<Contact> getContactList(String uid, String eid, String condition);
    void addContact(Contact contact);
    void addKakaoContacts(List<Contact> contacts);
    void sendKakaoMessages(String accessToken, String uuids, String uid, String eid);
    void checkSent(Boolean is_sent,String contactId, String uid,String eid);
    void updateContact(Contact contact);
    void deleteContact(String contactId, String uid,String eid);
}
