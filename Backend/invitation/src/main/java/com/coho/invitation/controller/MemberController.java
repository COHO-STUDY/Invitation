package com.coho.invitation.controller;

import com.coho.invitation.dto.Member;
import com.coho.invitation.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /* 카카오 로그인 */
//    @GetMapping("/kakao")               // 백엔드 테스트용
//    public ResponseEntity<?> getKakaoEmail(@RequestParam String code){
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestBody JsonNode params){
        HttpSession session = request.getSession();
        HttpHeaders headers = new HttpHeaders();

        String code = params.get("code").asText();
        System.out.println(code);

        // 토큰 발급 요청
        String access_token = memberService.getKakaoAccessToken(code);

        // 사용자 정보 가져오기
        Member member = memberService.getUserInfo(access_token);

        // 회원 정보 확인하여 처음 로그인한 회원이면 회원가입
        if(memberService.getMember(member.getUid()).isEmpty())
            memberService.insertMember(member);

        // session에 uid 추가
        session.setAttribute("uid",member.getUid());

        headers.setLocation(URI.create("/api/events"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    /* 카카오 이메일 정보 추가로 가져오기 */
//    @GetMapping("/kakao/email")
//    public String getKakaoEmail(@RequestParam String code){
//
//        return "";
//    }

    /* 회원 로그인 */
//    @GetMapping("/{uid}")
//    public String login(){
//        return "login";
//    }

    /* 회원가입(처음 로그인 시) */
//    @PostMapping("/")
//    public String create(){
//        return "create users";
//    }



}
