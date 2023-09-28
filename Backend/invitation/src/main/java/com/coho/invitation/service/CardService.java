package com.coho.invitation.service;

import com.coho.invitation.dto.Card;

import java.util.Optional;

public interface CardService {
    Card getCard(String eid);
    Optional<Card> checkDuplicateCard(String eid);
//    void addCardImage(Card card);
    void addCardTemplate(Card card);
    void updateCard(Card card);
    void deleteCard(String cid);
}
