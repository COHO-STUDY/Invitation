package com.coho.invitation.service;

import com.coho.invitation.dto.KakaoOAuthToken;
import com.coho.invitation.dto.Member;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    KakaoOAuthToken getKakaoAccessToken(String code);
    Member getUserInfo(String accessToken);
    Optional<Member> getMember(String uid);
    List<Member> getMemberList();
    void insertMember(Member member);
    void updateRefreshToken(String uid,String refreshToken);
    void updateMember(Member member);
    void deleteMember(String uid);
}
