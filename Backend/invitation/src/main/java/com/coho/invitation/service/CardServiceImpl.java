package com.coho.invitation.service;

import com.coho.invitation.dto.Card;
import com.coho.invitation.mapper.CardMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardServiceImpl implements CardService{

    private final CardMapper cardMapper;

    public CardServiceImpl(CardMapper cardMapper) {
        this.cardMapper = cardMapper;
    }

    public Card getCard(String eid){
        return cardMapper.getCard(eid);
    }

    public Optional<Card> checkDuplicateCard(String eid){
        return Optional.ofNullable(cardMapper.getCard(eid));
    }

//    public void addCardImage(Card card){
//        cardMapper.addCardImage(card);
//    }

    public void addCardTemplate(Card card){
        cardMapper.addCardTemplate(card);
    }

    public void updateCard(Card card){
        cardMapper.updateCard(card);
    }

    public void deleteCard(String cid){
        cardMapper.deleteCard(cid);
    }
}
