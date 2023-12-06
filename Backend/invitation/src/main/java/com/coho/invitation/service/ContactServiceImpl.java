package com.coho.invitation.service;

import com.coho.invitation.dto.Contact;
import com.coho.invitation.mapper.ContactMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService{

    private final ContactMapper contactMapper;
    private static final String KAKAO_API_BASE_URL = "https://kapi.kakao.com";
    @Value("${CARD_URI}")
    private String CARD_URI;

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
    public void sendKakaoMessages(String accessToken, String uuids, String uid, String eid){
        // 카카오 메세지 1회에 5개 전송 가능 => 수십~수백개 전송
        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_API_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();

        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/api/talk/friends/message/scrap/send")
                        .build())
                .body(BodyInserters.fromFormData("receiver_uuids",uuids)
                        .with("request_url",CARD_URI))
                .retrieve().bodyToMono(JsonNode.class)
                .subscribe(e -> {
                    if (e.has("successful_receiver_uuids")){
                        for(JsonNode uuid:e.get("successful_receiver_uuids")){
                            // 초대장 전송 여부 체크
                            checkSent(true,uuid.asText(),uid,eid);
                        }
                    }
                });

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
