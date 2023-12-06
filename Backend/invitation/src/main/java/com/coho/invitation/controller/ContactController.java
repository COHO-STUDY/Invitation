package com.coho.invitation.controller;

import com.coho.invitation.dto.Contact;
import com.coho.invitation.security.UserAuthorize;
import com.coho.invitation.service.ContactService;
import com.coho.invitation.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Tag(name="4) Contact API",description = "연락처 목록 API")
@UserAuthorize
@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactService contactService;
    @Autowired
    private MemberService memberService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /* 연락처 조회 */
    @Operation(summary = "로그인한 사용자의 특정 행사의 연락처 목록 조회", description = "파라미터로 받은 event id의 연락처 목록을 조회하여 반환")
    @Parameter(name="event id", description = "선택한 행사의 event id를 전송")
    @Parameter(name ="condition", description = "검색 조건을 전송")
    @GetMapping("/{eid}")
    public ResponseEntity<List<Contact>> getContactList(@AuthenticationPrincipal User user, @PathVariable("eid")String eid, @RequestParam("condition")String con){
        String uid = user.getUsername();

        System.out.println(con);

        List<Contact> contactList = contactService.getContactList(uid,eid,con);

        return ResponseEntity.ok().body(contactList);
    }

    /* 연락처 카카오톡 친구불러오기로 추가 */
    @Operation(summary = "로그인한 사용자의 카카오톡 친구 목록 불러오기", description = "카카오톡 친구 목록 가져오기 API를 사용하여 현재 사용자의 친구 목록을 가져옴")
    @Parameter(name="event id", description = "선택한 행사의 event id를 전송")
    @PostMapping("/{eid}/kakao")
    public ResponseEntity<String> addContactList(@AuthenticationPrincipal User user, @PathVariable("eid")String eid) {
        String uid = user.getUsername();

        // refresh token으로 access token 다시 불러오기
        String accessToken = memberService.refreshKakaoToken(uid,memberService.getRefreshToken(uid)).getAccess_token();
        System.out.println(accessToken);

        // 카카오 친구목록 불러오기 API 호출
        List<Contact> contacts = contactService.getKakaoFriends(accessToken,uid,eid);
        System.out.println(contacts);

        // 카카오 친구목록 DB에 저장
        contactService.addKakaoContacts(contacts);

        return ResponseEntity.ok().body("추가 성공");
    }

    /* 연락처 직접 추가 */
    @Operation(summary = "연락처 직접 추가", description = "파라미터로 받은 event id와 연락처 정보를 가지고 연락처를 저장하고 반환")
    @Parameter(name="event id", description = "선택한 행사의 event id를 전송")
    @PostMapping("/{eid}")
    public ResponseEntity<Contact> addContact(@AuthenticationPrincipal User user, @PathVariable("eid")String eid, @RequestBody JsonNode params){
        String uid = user.getUsername();

        Contact contact = new Contact();

        contact.setContactId(UUID.randomUUID().toString());
        contact.setMuid(uid);
        contact.setEid(eid);
        contact.setName(params.get("name").asText());
        contact.setPhoneNum(params.get("phoneNumber").asText());
        contact.setIs_kakao_used(false);

        contactService.addContact(contact);

        return ResponseEntity.ok().body(contact);
    }

    /* 카카오톡 메시지 전송 */
    @Operation(summary = "카카오톡 메시지 전송", description = "모바일 초대장 링크를 카카오톡 메시지 전송 api를 사용해 전송")
    @Parameter(name="event id", description = "선택한 행사의 event id를 전송, 메세지 전송할 친구리스트를 [\"친구1 uuid\",\"친구2 uuid\",...] 형태로 전송")
    @PostMapping("/{eid}/kakao/message")
    public ResponseEntity<?> sendKakaoMessages(@AuthenticationPrincipal User user,@PathVariable("eid") String eid, @RequestBody String[] friends){
        String uid = user.getUsername();

        // refresh token으로 access token 다시 불러오기
        String accessToken = memberService.refreshKakaoToken(uid,memberService.getRefreshToken(uid)).getAccess_token();

        // 카카오톡 메시지 api 호출 (API당 5개씩만 전송 가능)
        String uuids;
        // uuid 5개씩 묶어서 처리
        for (int i=0; i < friends.length; i+=5){
            // 0,5,10,15,20
            uuids = String.join("\",\"",Arrays.copyOfRange(friends,i,Math.min(i+5,friends.length)));
            uuids = "[\"" + uuids + "\"]";
            System.out.println(uuids);
            // access_token, uuid 배열, template_object를 사용하여 호출
            contactService.sendKakaoMessages(accessToken,uuids,uid,eid);
        }

        return ResponseEntity.ok().body("모두 전송 성공");
    }

    /* 초대장 전송 여부 체크 */
    @Operation(summary = "특정 연락처의 초대장 전송 여부 조회", description = "파라미터로 받은 event id와 contact를 받아 전송여부를 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @PutMapping("/{eid}/status")
    public ResponseEntity<?> checkIsSent(@AuthenticationPrincipal User user, @PathVariable("eid")String eid, @RequestBody JsonNode params){
        String uid = user.getUsername();

        Boolean isSent = params.get("status").asBoolean();
        String cid = params.get("contactId").asText();

        contactService.checkSent(isSent,cid,uid,eid);

        return ResponseEntity.ok().body(isSent);
    }

    /* 연락처 정보 수정 */
    @Operation(summary = "특정 연락처의 정보 수정", description = "파라미터로 받은 event id와 연락처 정보를 수정하고 연락처 정보를 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @PutMapping("/{eid}")
    public ResponseEntity<Contact> updateContact(@AuthenticationPrincipal User user, @PathVariable("eid")String eid, @RequestBody JsonNode params){
        String uid = user.getUsername();

        Contact contact = new Contact();

        contact.setMuid(uid);
        contact.setEid(eid);
        contact.setContactId(params.get("uuid").asText());
        contact.setName(params.get("name").asText());
        contact.setPhoneNum(params.get("phoneNumber").asText());

        contactService.updateContact(contact);

        return ResponseEntity.ok().body(contact);
    }

    /* 연락처 삭제 */
    @Operation(summary = "특정 연락처 삭제", description = "파라미터로 받은 event id와 연락처를 삭제하고 삭제한 연락처 id 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @DeleteMapping("/{eid}")
    public ResponseEntity<String> deleteContact(@AuthenticationPrincipal User user, @PathVariable("eid")String eid, @RequestBody JsonNode params){
        String uid = user.getUsername();

        String contactId = params.get("contactId").asText();

        System.out.println(contactId);

        contactService.deleteContact(contactId,uid,eid);

        return ResponseEntity.ok().body(contactId);
    }


}
