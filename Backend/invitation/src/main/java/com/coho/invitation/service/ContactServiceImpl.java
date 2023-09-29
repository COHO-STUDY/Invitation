package com.coho.invitation.service;

import com.coho.invitation.dto.Contact;
import com.coho.invitation.mapper.ContactMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService{

    private final ContactMapper contactMapper;

    public ContactServiceImpl(ContactMapper contactMapper) {
        this.contactMapper = contactMapper;
    }

    public List<Contact> getContactList(String uid, String eid, String condition){
        return contactMapper.getContactList(uid,eid,condition);
    }
    public void addContact(Contact contact){
        contactMapper.addContacts(contact);
    }
    public void addContacts(List<Contact> contacts){
        for (Contact contact:contacts) {
            contactMapper.addContacts(contact);
        }
    }
    public void checkSent(Boolean is_sent,String contactId, String uid,String eid){
        contactMapper.checkSent(is_sent,contactId,uid,eid);
    }
    public void updateContact(Contact contact){
        contactMapper.updateContact(contact);
    }
    public void deleteContact(String contactId, String uid,String eid){
        contactMapper.deleteContact(contactId,uid,eid);
    }
}
