package com.coho.invitation.controller;

import com.coho.invitation.dto.Card;
import com.coho.invitation.dto.Event;
import com.coho.invitation.service.CardService;
import com.coho.invitation.service.EventService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name="3) Card API",description = "초대장(청첩장,초대장) API")
@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardController {

    private CardService cardService;
    private EventService eventService;

    public CardController(CardService cardService, EventService eventService) {
        this.cardService = cardService;
        this.eventService = eventService;
    }

    /* 카드 정보 조회 */
    @Operation(summary = "특정 행사의 초대장(카드) 조회", description = "파라미터로 받은 event id의 초대장 카드 정보를 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @GetMapping("/{eid}")
    public ResponseEntity<?> getCard(@PathVariable("eid") String eid){
        Map<String,Object> map = new HashMap<>();

        Event event = eventService.getEvent(eid);
        Card card = cardService.getCard(eid);

        map.put("event",event);
        map.put("card",card);

        return ResponseEntity.ok().body(map);
    }

    /* 초대장 이미지 조회하기 */

    /* 초대장 이미지 파일로 추가하기 */
//    @PostMapping("/files/{eid}")
//    public ResponseEntity<?> UploadCardImage(@PathVariable("eid") String eid, @RequestBody JsonNode params) throws IOException {
//        Card card = new Card();
//
//        /* 행사에 초대장 존재 여부 확인 */
//        if(cardService.checkDuplicateCard(eid).isPresent())
//            return ResponseEntity.ok().body("이미 초대장이 존재합니다.");
//
//        /* 카드 파일로 추가 */
//        card.setEid(eid);
//        card.setCid(UUID.randomUUID().toString());
//        // 이미지를 byte[] 타입으로 변경하기 - 할일
////        card.setImgData(params.get("imgData").binaryValue());
//
//        cardService.addCardImage(card);
//
//        return ResponseEntity.ok().body("성공");
//    }

    /* 초대장 템플릿으로 추가 */
    @Operation(summary = "특정 행사의 초대장 카드 추가", description = "파라미터로 받은 event id의 카드 정보를 사용하여 초대장을 저장하고 정보 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @PostMapping("/templates/{eid}")
    public ResponseEntity<?> addCard(@PathVariable("eid") String eid, @RequestBody JsonNode params){
        Card card = new Card();

        /* 행사에 초대장 존재 여부 확인 */
        if(cardService.checkDuplicateCard(eid).isPresent())
            return ResponseEntity.ok().body("이미 초대장이 존재합니다.");

        /* 초대장 템플릿 생성 */
        card.setEid(eid);
        card.setCid(UUID.randomUUID().toString());
        // 수정하면 외계문자로 작성됨 : 수정필요
        if(params.get("ctype").toString().equals("Template"))
            card.setCtype('T');
        card.setGreeting(params.get("greeting").asText());
        card.setLetter(params.get("letter").asText());
        card.setBankAccount(params.get("bankAccount").asText());
        card.setChost(params.get("chost").toString());
        card.setAdHost(params.get("adHost").toString());
        card.setAddress(params.get("address").asText());
        card.setSequence(params.get("sequence").asText());


        cardService.addCardTemplate(card);

        return ResponseEntity.ok().body(card.getCid());
    }

    /* 초대장 수정하기 */
    @Operation(summary = "특정 초대장 카드의 정보 수정", description = "파라미터로 받은 card id와 초대장 카드 정보를 통해 수정하고 card id 반환")
    @Parameter(name="str", description = "선택한 초대장의 card id를 전송")
    @PutMapping("/{cid}")
    public ResponseEntity<?> updateCard(@PathVariable("cid") String cid, @RequestBody JsonNode params){
        Card card = new Card();

        card.setCid(cid);
        if(params.get("ctype").toString().equals("Template"))
            card.setCtype('T');
        card.setGreeting(params.get("greeting").asText());
        card.setLetter(params.get("letter").asText());
        card.setBankAccount(params.get("bankAccount").asText());
        card.setChost(params.get("chost").toString());
        card.setAdHost(params.get("adHost").toString());
        card.setAddress(params.get("address").asText());
        card.setSequence(params.get("sequence").asText());
//        card.setImgData(params.get("imgData").binaryValue());

        cardService.updateCard(card);

        return ResponseEntity.ok().body(cid);
    }

    @Operation(summary = "특정 초대장 카드 삭제", description = "파라미터로 받은 card id의 초대장 카드 삭제 후 card id 반환")
    @Parameter(name="str", description = "선택한 행사의 event id를 전송")
    @DeleteMapping("/{cid}")
    public ResponseEntity<?> deleteCard(@PathVariable("cid") String cid){

        cardService.deleteCard(cid);

        return ResponseEntity.ok().body(cid);
    }
}
