package com.coho.invitation.service;

import com.coho.invitation.dto.Contact;
import com.coho.invitation.mapper.ContactMapper;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService{

    private final ContactMapper contactMapper;
    private static final String KAKAO_API_BASE_URL = "https://kapi.kakao.com";
    @Value("${KAKAO_API_KEY}")
    private String KAKAO_API_KEY;
    @Value("${REDIRECT_URI}")
    private String REDIRECT_URI;

    public ContactServiceImpl(ContactMapper contactMapper) {
        this.contactMapper = contactMapper;
    }

    @Override
    public List<Contact> getKakaoFriends(String accessToken, String uid, String eid){

        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_API_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();

        JsonNode response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/api/talk/friends")
                        .queryParam("limit",50)
                        .queryParam("order","asc")
                        .queryParam("friend_order","favorite")
                        .build())
                .retrieve().bodyToMono(JsonNode.class).block();

        System.out.println(response.get("elements"));

        List<Contact> friendList = new ArrayList<>();
        Contact contact = new Contact();
        contact.setIs_kakao_used(true);
        contact.setMuid(uid);
        contact.setEid(eid);
        for(JsonNode friend:response.get("elements")){
            contact.setContactId(friend.get("uuid").asText());
            contact.setName(friend.has("profile_nickname") ? friend.get("profile_nickname").asText() : "unknown");
            friendList.add(contact);
        }

        return friendList;
    }
    @Override
    public List<Contact> getContactList(String uid, String eid, String condition){
        return contactMapper.getContactList(uid,eid,condition);
    }
    @Override
    public void addContact(Contact contact){
        contactMapper.addContacts(contact);
    }
    @Override
    public void addKakaoContacts(List<Contact> contacts){
        contactMapper.addKakaoContacts(contacts);
    }
    @Override
    public void checkSent(Boolean is_sent,String contactId, String uid,String eid){
        contactMapper.checkSent(is_sent,contactId,uid,eid);
    }
    @Override
    public void updateContact(Contact contact){
        contactMapper.updateContact(contact);
    }
    @Override
    public void deleteContact(String contactId, String uid,String eid){
        contactMapper.deleteContact(contactId,uid,eid);
    }
}
