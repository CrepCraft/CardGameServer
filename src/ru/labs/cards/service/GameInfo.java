package ru.labs.cards.service;

import ru.labs.cards.data.Card;
import ru.labs.cards.data.PlayerStatus;

import java.util.List;

public record GameInfo(List<Card> myCards, List<Card> opponentCards, PlayerStatus status, String exceptionMessage) {
}
