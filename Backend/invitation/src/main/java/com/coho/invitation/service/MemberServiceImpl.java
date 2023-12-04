package com.coho.invitation.service;

import com.coho.invitation.dto.KakaoOAuthToken;
import com.coho.invitation.dto.Member;
import com.coho.invitation.mapper.EventMapper;
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
    private final EventMapper eventMapper;
    private static final String KAKAO_LOGIN_API_BASE_URL="https://kauth.kakao.com";
    private static final String KAKAO_API_BASE_URL = "https://kapi.kakao.com";
    @Value("${KAKAO_API_KEY}")
    private String KAKAO_API_KEY;
    @Value("${REDIRECT_URI}")
    private String REDIRECT_URI;


    public MemberServiceImpl(MemberMapper memberMapper, EventMapper eventMapper) {
        this.memberMapper = memberMapper;
        this.eventMapper = eventMapper;
    }

    /* 카카오 토큰 받기 */
    @Override
    public KakaoOAuthToken getKakaoAccessToken(String code) {
        KakaoOAuthToken authToken = new KakaoOAuthToken();

        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_LOGIN_API_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        JsonNode response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id",KAKAO_API_KEY)
                        .queryParam("redirect_uri", REDIRECT_URI)
                        .queryParam("code",code)
                        .build())
                .retrieve().bodyToMono(JsonNode.class).block();

        authToken.setToken_type(response.get("token_type").asText());
        authToken.setAccess_token(response.get("access_token").asText());
        authToken.setExpires_in(response.get("expires_in").asInt());
        authToken.setRefresh_token(response.get("refresh_token").asText());
        authToken.setRefresh_token_expires_in(response.get("refresh_token_expires_in").asInt());

        return authToken;
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

        System.out.println(response);

        member.setUid("K"+response.get("id").asText());
        if (response.has("properties"))
            member.setName(response.get("properties").get("nickname").asText());
        else
            member.setName("unknown user");
        if(!response.get("kakao_account").get("email_needs_agreement").asBoolean())
            member.setEmail(response.get("kakao_account").get("email").asText());

        return member;
    }

    /* kakao token 갱신하기 : refresh token으로 access token 발급 */
    @Override
    public KakaoOAuthToken refreshKakaoToken(String uid,String refreshToken){
        KakaoOAuthToken token = new KakaoOAuthToken();
        WebClient webClient = WebClient.builder()
                .baseUrl(KAKAO_LOGIN_API_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        JsonNode response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type","refresh_token")
                        .queryParam("client_id",KAKAO_API_KEY)
                        .queryParam("refresh_token",refreshToken)
                        .build())
                .retrieve().bodyToMono(JsonNode.class).block();

        if(response.has("refresh_token")){
            token.setRefresh_token(response.get("refresh_token").asText());
            token.setRefresh_token_expires_in(response.get("refresh_token_expires_in").asInt());
            memberMapper.updateRefreshToken(uid,token.getRefresh_token());
        }

        token.setAccess_token(response.get("access_token").asText());
        token.setExpires_in(response.get("expires_in").asInt());

        return token;
    }

    public String getRefreshToken(String uid){
        return memberMapper.getRefreshToken(uid);
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

    @Override
    public void updateRefreshToken(String uid,String refreshToken){
        memberMapper.updateRefreshToken(uid,refreshToken);
    }

    @Override
    public void updateMember(Member member){
        memberMapper.updateMember(member);
    }

    @Override
    public void deleteMember(String uid){
        memberMapper.deleteMember(uid);
        eventMapper.deleteManage(uid);
    }


}
