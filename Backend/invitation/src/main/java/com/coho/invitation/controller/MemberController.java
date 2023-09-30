package com.coho.invitation.controller;

import com.coho.invitation.dto.KakaoOAuthToken;
import com.coho.invitation.dto.Member;
import com.coho.invitation.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.Optional;

@Tag(name="1) Member API",description = "사용자 API")
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

    /* 로그인한 사용자의 정보 조회 */
    @GetMapping("")
    public ResponseEntity<Member> getMember(){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        Member member = memberService.getMember(uid).get();

        return ResponseEntity.ok().body(member);
    }

    /* 카카오 로그인 - 웹 */
    @Operation(summary = "웹 카카오 로그인", description = "파라미터로 받은 인가코드로 로그인하고 사용자의 정보 반환")
    @PostMapping("/kakao/web")
    public ResponseEntity<Member> kakaoCallback(@RequestBody JsonNode params){
//        HttpSession session = request.getSession();
        HttpHeaders headers = new HttpHeaders();

        String code = params.get("code").asText();

        // 토큰 발급 요청
        KakaoOAuthToken authToken = memberService.getKakaoAccessToken(code);

        // 사용자 정보 가져오기
        Member member = memberService.getUserInfo(authToken.getAccess_token());

        // 회원 정보 확인하여 처음 로그인한 회원이면 회원가입
        Optional<Member> savedMember = memberService.getMember(member.getUid());
        if(savedMember.isEmpty())
            memberService.insertMember(member);
        // 이미 회원가입한 사용자이면 DB의 사용자 정보 저장
        else
            member = savedMember.get();

        System.out.println(member.getUid()+" "+authToken.getAccess_token());

//        // session에 uid 추가
//        session.setAttribute("uid",member.getUid());
//        // access_token, refresh_token도 session에 저장
//        session.setAttribute("access_token",authToken.getAccess_token());
//        session.setAttribute("refresh_token",authToken.getRefresh_token());

        return ResponseEntity.ok().headers(headers).body(member);
    }

    /* 카카오 로그인 - 안드로이드 */
    @Operation(summary = "안드로이드 카카오 로그인", description = "파라미터로 받은 사용자id와 token을 저장")
    @PostMapping("/kakao/android/")
    public ResponseEntity<Member> kakaoLoginAndroid(@RequestBody JsonNode params){
        Member member;

        String uid = "K" + params.get("userId").asText();
        String access_token = params.get("accessToken").asText();

        Optional<Member> savedMember= memberService.getMember(uid);

        // 처음 로그인한 사용자 : 회원가입
        if(savedMember.isEmpty()){
            // 사용자 정보 가져오기
            member = memberService.getUserInfo(access_token);
            memberService.insertMember(member);
        }
        // 로그인
        else
            member = savedMember.get();

        return ResponseEntity.ok().body(member);
    }

    /* 카카오 이메일 정보 추가로 가져오기 */
//    @GetMapping("/kakao/email")
//    public String getKakaoEmail(@RequestParam String code){
//
//        return "";
//    }

    /* 회원 정보 수정 */

    @PutMapping("")
    public ResponseEntity<String> updateUserInfo(@RequestBody JsonNode params){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");
        Member member = new Member();
        member.setUid(uid);
        member.setName(params.get("name").asText());
        member.setEmail(params.get("email").asText());

        memberService.updateMember(member);

        return ResponseEntity.ok().body(uid);
    }

    /* 회원 탈퇴 */
    @DeleteMapping("")
    public ResponseEntity<String> deleteUser(){
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute("uid");

        // 사용자 권한 'N' 처리 + manage 삭제
        memberService.deleteMember(uid);
        // 권한자가 모두 탈퇴한 행사 삭제
//        memberService.deleteEventByUid

        return ResponseEntity.ok().body(uid);
    }
}
