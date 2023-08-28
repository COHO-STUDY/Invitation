package com.coho.invitation.controller;

import com.coho.invitation.dto.Event;
import com.coho.invitation.service.EventService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /* 로그인한 사용자의 전체 행사 목록 가져오기 */
    @GetMapping("")
    public ResponseEntity<List<Event>> getEvents(){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        List<Event> eventList = eventService.getEventList(uid);

        return ResponseEntity.ok().body(eventList);
    }

    /* 로그인한 사용자의 진행중인 행사 목록 가져오기 */
    @GetMapping("/progressing")
    public ResponseEntity<List<Event>> getEventsProgressing(){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        List<Event> eventList = eventService.getEventsProgressing(uid);

        return ResponseEntity.ok().body(eventList);
    }

    /* 로그인한 사용자의 진행 완료된 행사 목록 가져오기 */
    @GetMapping("/done")
    public ResponseEntity<List<Event>> getEventsDone(){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        List<Event> eventList = eventService.getEventsDone(uid);

        return ResponseEntity.ok().body(eventList);
    }

    /* 선택한 행사 조회 */
    @GetMapping("/{eid}")
    public ResponseEntity<Event> getEvent(@PathVariable("eid") String eid){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        /* 권한이 없다면 조회 불가능 - spring interceptor로..? */
        if(!eventService.checkAuthority(eid).contains(uid))
            return null;

        //선택한 행사 조회
        Event event = eventService.getEvent(eid);

        return ResponseEntity.ok().body(event);
    }

    /* 행사 추가하기 */
    @PostMapping("")
    public String addEvent(@RequestBody JsonNode params){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");
        Event event = new Event();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        // 실제 event 값 설정
        event.setEid(UUID.randomUUID().toString());
        event.setEtype(params.get("type").asText());
        event.setEdate(LocalDateTime.parse(params.get("datetime").asText()),formatter);
        event.setLocation(params.get("location").asText());
        event.setEhost(params.get("host").toString());

        eventService.insertEvent(event);
        eventService.insertManage(uid,event.getEid());

        return event.getEid();
    }

    /* 행사 권한자 추가하기
    * 행사 권한자를 어떻게 호출할 것인가(백 or 프론트)*/
    @PostMapping("/auth/{eid}")
    public String addAuthority(@PathVariable("eid") String eid, @RequestBody JsonNode params){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");
        String newUid = params.get("uid").asText();

        // 현재 사용자가 권한자 인지 확인
        if(!eventService.checkAuthority(eid).contains(uid))
            return "권한자 추가 실패";

        // 권한자 추가
        eventService.insertManage(newUid,eid);

        return "권한자 추가 성공";
    }

    /* 행사 정보 수정하기 */
    @PutMapping("/{eid}")
    public String updateEvent(@PathVariable("eid") String eid, @RequestBody Event event){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        event.setEid(eid);

        /* 권한이 있을 경우에만 수정 가능 */
//        if(!eventService.checkAuthority(eid).contains(uid))
//            return "권한이 없습니다.";     // 임의로 넣어놓음

        // 행사 수정
        eventService.updateEvent(event);

        return eid;
    }

    /* 행사 삭제하기 */
    @DeleteMapping("/{eid}")
    public String deleteEvent(@PathVariable("eid") String eid){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        /* 권한이 있을 경우에만 삭제 가능 - 행사 페이지 안에서 삭제시 필요X */
        if(!eventService.checkAuthority(eid).contains(uid))
            return "권한이 없습니다.";     // 임의로 넣어놓음

        // 행사 삭제
        eventService.deleteEvent(eid);

        return eid;
    }

    /* 행사의 권한 삭제 - 자기 자신만 가능 */
    @DeleteMapping("/auth/{eid}")
    public String deleteAuthority(@PathVariable("eid") String eid){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        /* 현재 로그인한 사용자의 행사 권한 삭제 */
        eventService.deleteAuthority(eid,uid);

        /* 권한자가 모두 권한 해제될 경우 행사 삭제 */
        if(eventService.checkAuthority(eid).isEmpty())
            eventService.deleteEvent(eid);

        return eid;
    }

}
