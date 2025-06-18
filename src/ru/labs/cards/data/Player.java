package ru.labs.cards.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Player {
    private String name;
    private String surname;
    private PlayerStatus status;
    private List<Card> myCards = new ArrayList<>();
    private int gameIndex = -1;
    private boolean isPassed = false;
    private boolean drawACard = false;
    public Player(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isDrawACard() {
        return drawACard;
    }

    public void setDrawACard(boolean drawACard) {
        this.drawACard = drawACard;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public boolean getPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    public Collection<Card> getMyCards() {
        return myCards;
    }

    public String getName() {
        return name;
    }

    public int getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(int gameIndex) {
        this.gameIndex = gameIndex;
    }

    public void addCard(Card card) {
        myCards.add(card);
    }

    public int turnDecision() {
        System.out.println("\nYour cards: ");
        for (var card : myCards) {
            System.out.println(card.value() + "; ");
        }
        Scanner scr = new Scanner(System.in);
        System.out.println("\n1.Draw a card\n2.Pass\n");
        int res = scr.nextInt();
        if (res == 1 || res == 2) {
            return res;
        } else {
            while (res != 1 && res != 2) {
                System.out.println("\nSomething went wrong, try again");
                res = scr.nextInt();
            }
            return res;
        }
    }
}
