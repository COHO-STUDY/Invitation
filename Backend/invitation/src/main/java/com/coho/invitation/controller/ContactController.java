package com.coho.invitation.controller;

import com.coho.invitation.dto.Contact;
import com.coho.invitation.security.UserAuthorize;
import com.coho.invitation.service.ContactService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name="4) Contact API",description = "연락처 목록 API")
//@UserAuthorize
@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*")
public class ContactController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /* 연락처 조회 */
    @Operation(summary = "로그인한 사용자의 특정 행사의 연락처 목록 조회", description = "파라미터로 받은 event id의 연락처 목록을 조회하여 반환")
    @Parameter(name="event id", description = "선택한 행사의 event id를 전송")
    @Parameter(name = "condition", description = "검색 조건을 전송")
    @GetMapping("/{eid}")
    public ResponseEntity<List<Contact>> getContactList(@PathVariable("eid")String eid, @RequestParam("condition")String con){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        System.out.println(con);

        List<Contact> contactList = contactService.getContactList(uid,eid,con);

        return ResponseEntity.ok().body(contactList);
    }

    /* 연락처 카카오톡 친구불러오기로 추가 */
//    @PostMapping("/kakao/{eid}")
//    public ResponseEntity<String> addContactList(@PathVariable("eid")String eid, @RequestBody List<Contact> contacts){
//        HttpSession session = request.getSession();
//        String uid = (String) session.getAttribute("uid");
////        List<Contact> contacts =
//
//        // 로직 추가
//        contactService.addContacts(contacts);
//
//        return ResponseEntity.ok().body("추가 성공");
//    }

    /* 연락처 직접 추가 */
    @Operation(summary = "연락처 직접 추가", description = "파라미터로 받은 event id와 연락처 정보를 가지고 연락처를 저장하고 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @PostMapping("/{eid}")
    public ResponseEntity<Contact> addContact(@PathVariable("eid")String eid, @RequestBody JsonNode params){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

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
//    @PostMapping("/kakaoMsg/{eid}")
//    public ResponseEntity<?> sendKakaoMessages(@PathVariable("eid") String eid, @RequestBody String cid){
//        HttpSession session = request.getSession();
//        String uid = (String) session.getAttribute("uid");
//
//        // 카카오톡 메시지 api 호출
//        // access_token, uuid 배열, template_object를 사용하여 호출
//
//
//        // 초대장 전송 여부 체크
//        contactService.checkSent(true,cid,uid,eid);
//
//        return ResponseEntity.ok().body(cid);
//    }

    /* 초대장 전송 여부 체크 */
    @Operation(summary = "특정 연락처의 초대장 전송 여부 조회", description = "파라미터로 받은 event id와 contact를 받아 전송여부를 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @PutMapping("/{eid}/status")
    public ResponseEntity<?> checkIsSent(@PathVariable("eid")String eid, @RequestBody JsonNode params){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");
        Boolean isSent = params.get("status").asBoolean();
        String cid = params.get("contactId").asText();

        contactService.checkSent(isSent,cid,uid,eid);

        return ResponseEntity.ok().body(isSent);
    }

    /* 연락처 정보 수정 */
    @Operation(summary = "특정 연락처의 정보 수정", description = "파라미터로 받은 event id와 연락처 정보를 수정하고 연락처 정보를 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @PutMapping("/{eid}")
    public ResponseEntity<Contact> updateContact(@PathVariable("eid")String eid, @RequestBody JsonNode params){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

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
    public ResponseEntity<String> deleteContact(@PathVariable("eid")String eid, @RequestBody JsonNode params){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        String contactId = params.get("contactId").asText();

        System.out.println(contactId);

        contactService.deleteContact(contactId,uid,eid);

        return ResponseEntity.ok().body(contactId);
    }


}
