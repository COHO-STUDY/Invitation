package com.coho.invitation.mapper;

import com.coho.invitation.dto.Card;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CardMapper {
    Card getCard(String eid);
//    void addCardImage(Card card);
    void addCardTemplate(Card card);
    List<String> checkCardAuthority(String cid);
    void updateCard(Card card);
    void deleteCard(String cid);
}
