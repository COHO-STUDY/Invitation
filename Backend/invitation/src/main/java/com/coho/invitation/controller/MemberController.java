package com.coho.invitation.controller;

import com.coho.invitation.dto.KakaoOAuthToken;
import com.coho.invitation.dto.Member;
import com.coho.invitation.security.TokenProvider;
import com.coho.invitation.security.UserAuthorize;
import com.coho.invitation.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private final TokenProvider tokenProvider;

    public MemberController(MemberService memberService, TokenProvider tokenProvider) {
        this.memberService = memberService;
        this.tokenProvider = tokenProvider;
    }

    /* 로그인한 사용자의 정보 조회 */
    @UserAuthorize
    @GetMapping("")
    public ResponseEntity<Member> getMember(){
        String uid = (String) request.getAttribute("uid");

        Member member = memberService.getMember(uid).get();

        return ResponseEntity.ok().body(member);
    }

    /* 카카오 로그인 - 웹 */
    @Operation(summary = "웹 카카오 로그인", description = "파라미터로 받은 인가코드로 로그인하고 사용자의 정보 반환")
    @PostMapping("/kakao/web")
    public ResponseEntity<Member> kakaoCallback(@RequestBody JsonNode params){
        HttpHeaders headers = new HttpHeaders();

        String code = params.get("code").asText();

        // 카카오 토큰 발급 요청
        KakaoOAuthToken authToken = memberService.getKakaoAccessToken(code);

        // 사용자 정보 가져오기
        Member member = memberService.getUserInfo(authToken.getAccess_token());

        // 회원 체크
        Optional<Member> savedMember = memberService.getMember(member.getUid());
        // 회원가입 - 처음 로그인
        if(savedMember.isEmpty()){
            member.setRefreshToken(authToken.getRefresh_token());
            memberService.insertMember(member);             // refresh token을 포함한 사용자 정보 저장
        }
        // 로그인 - 이미 가입한 사용자
        else {
            member = savedMember.get();
            memberService.updateRefreshToken(member.getUid(), authToken.getRefresh_token());        // DB의 refresh token 수정
        }
        System.out.println(member.getUid() +" "+member.getName());

        // refresh_token값 초기화
        member.setRefreshToken("");

        // 서버 토큰 발급
        String jwt = tokenProvider.createToken(String.format("%s:%s",member.getUid(),"USER"));
        headers.add("Authorization","Bearer "+jwt);

        return ResponseEntity.ok().headers(headers).body(member);
    }

    /* 카카오 로그인 - 안드로이드 */
    @Operation(summary = "안드로이드 카카오 로그인", description = "파라미터로 받은 사용자id와 token을 저장")
    @PostMapping("/kakao/android/")
    public ResponseEntity<Member> kakaoLoginAndroid(@RequestBody JsonNode params){
        HttpHeaders headers = new HttpHeaders();
        Member member;

        String uid = "K" + params.get("userId").asText();
        String access_token = params.get("accessToken").asText();
        String refresh_token = "";

        // 회원 체크
        Optional<Member> savedMember = memberService.getMember(uid);
        // 회원가입 - 처음 로그인한 사용자
        if(savedMember.isEmpty()){
            member = memberService.getUserInfo(access_token);       // 사용자 정보 가져오기
            member.setRefreshToken(refresh_token);
            memberService.insertMember(member);                     // member 저장
        }
        // 로그인
        else {
            member = savedMember.get();
            memberService.updateRefreshToken(member.getUid(), refresh_token);
        }

        // refresh_token값 초기화
        member.setRefreshToken("");

        // 서버 토큰 발급
        String jwt = tokenProvider.createToken(String.format("%s:%s",member.getUid(),"USER"));
        headers.add("Authorization","Bearer "+jwt);

        return ResponseEntity.ok().headers(headers).body(member);
    }

    /* 회원 정보 수정 */
    @UserAuthorize
    @PutMapping("")
    public ResponseEntity<String> updateUserInfo(@RequestBody JsonNode params){
        String uid = (String) request.getAttribute("uid");

        Member member = new Member();
        member.setUid(uid);
        member.setName(params.get("name").asText());
        member.setEmail(params.get("email").asText());

        memberService.updateMember(member);

        return ResponseEntity.ok().body(uid);
    }

    /* 회원 탈퇴 */
    @UserAuthorize
    @DeleteMapping("")
    public ResponseEntity<String> deleteUser(){
        String uid = (String) request.getAttribute("uid");

        // 사용자 권한 'N' 처리 + manage 삭제
        memberService.deleteMember(uid);
        // 권한자가 모두 탈퇴한 행사 삭제
//        memberService.deleteEventByUid

        return ResponseEntity.ok().body(uid);
    }
}
