package com.coho.invitation.service;

import com.coho.invitation.dto.Member;
import com.coho.invitation.mapper.MemberMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{
    private final MemberMapper memberMapper;
    private static final String KAKAO_LOGIN_API_BASE_URL="https://kauth.kakao.com";
    private static final String KAKAO_API_BASE_URL = "https://kapi.kakao.com";
    @Value("${KAKAO_API_KEY}")
    private String KAKAO_API_KEY;

    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    /* 카카오 토큰 받기 */
    @Override
    public String getKakaoAccessToken(String code) {
        String access_token = "";

        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_LOGIN_API_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        JsonNode response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id",KAKAO_API_KEY)
                        .queryParam("redirect_uri", "http://localhost:8080/api/members/kakao")                 // 수정 필요
                        .queryParam("code",code)
                        .build())
                .retrieve().bodyToMono(JsonNode.class).block();

        access_token = response.get("access_token").asText();
//        id_token = token.get("id_token").asText();
//        refresh_token = token.get("refresh_token").asText();

        return access_token;
    }

    /* 사용자 정보 가져오기 */
    @Override
    public Member getUserInfo(String accessToken) {
        String[] userInfo = {"","",""};
        Member member = new Member();

        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_API_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        JsonNode response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/user/me")
                        .build())
                .retrieve().bodyToMono(JsonNode.class).block();

        member.setUid("K"+response.get("id").asText());
        member.setName(response.get("properties").get("nickname").asText());
        if(!response.get("kakao_account").get("email_needs_agreement").asBoolean())
            member.setEmail(response.get("kakao_account").get("email").asText());

        return member;
    }

    /* uid로 사용자 찾기 */
    @Override
    public Optional<Member> getMember(String uid){
        return memberMapper.getMember(uid);
    }

    @Override
    public List<Member> getMemberList(){
        return memberMapper.getMemberList();
    }

    @Override
    public void insertMember(Member member){
        memberMapper.insertMember(member);
    }
}
