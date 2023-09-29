package com.coho.invitation.service;

import com.coho.invitation.dto.Contact;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContactService {
    List<Contact> getContactList(String uid, String eid, String condition);
    void addContact(Contact contact);
    void addContacts(List<Contact> contacts);
    void checkSent(Boolean is_sent,String contactId, String uid,String eid);
    void updateContact(Contact contact);
    void deleteContact(String contactId, String uid,String eid);
}
