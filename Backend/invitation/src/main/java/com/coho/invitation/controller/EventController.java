package com.coho.invitation.controller;

import com.coho.invitation.dto.Event;
import com.coho.invitation.service.EventService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @GetMapping("")
    public String getEvents(){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");


        return uid;
    }

//    @GetMapping("/{eid}")
//    public String getEvent(){
//        return "";
//    }

    @PostMapping("")
    public String addEvent(@RequestBody JsonNode params){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");
        Event event = new Event();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        // 실제 event 값 설정
        event.setEid(uid+"_"+formatter.format(LocalDateTime.now()));
        event.setEtype(params.get("type").asText());
        event.setEdate(LocalDateTime.parse(params.get("datetime").asText()),formatter);
        event.setLocation(params.get("location").asText());
        event.setEhost(params.get("host").toString());

        eventService.insertEvent(event);
        eventService.insertManage(uid,event.getEid());

        return event.getEid();
    }

}
