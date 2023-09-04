package com.coho.invitation.service;

import com.coho.invitation.dto.Member;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    String[] getKakaoAccessToken(String code);
    Member getUserInfo(String accessToken);
    Optional<Member> getMember(String uid);
    List<Member> getMemberList();
    void insertMember(Member member);
    void updateMember(Member member);
    void deleteMember(String uid);
}
