package com.coho.invitation.mapper;

import com.coho.invitation.dto.Member;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Mapper
@Repository
public interface MemberMapper {
    Optional<Member> getMember(String uid);

    List<Member> getMemberList();

    void insertMember(Member member);

    void updateMember(Member member);

    void deleteMember(String uid);

}