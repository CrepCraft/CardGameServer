package ru.labs.cards.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private String name;
    private List<Player> activePlayers = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private boolean allPlayersPassed = false;

    public Game(String name) {
        this.name = name;
    }

    public List<Player> getActivePlayers() {
        return activePlayers;
    }

    public void addPlayer(Player player) {
        activePlayers.add(player);
    }

    public void createDeck() {
        for (int i = 2; i < 12; i++) {
            if (i != 5) {
                deck.add(new Card(i, Suit.DIAMOND));
                deck.add(new Card(i, Suit.CLUB));
                deck.add(new Card(i, Suit.HEART));
                deck.add(new Card(i, Suit.SPADE));
            }
        }
    }

    public void startGame() {
        createDeck();
        allPlayersPassed = false;
        Random rnd = new Random();
        for (var player : activePlayers) {
            int ind = rnd.nextInt(deck.size());
            Card card = (Card) (deck.toArray()[ind]);
            player.addCard(card);
        }
        gameProcess();
    }

    public void giveCard(Player player) {
        Random rnd = new Random();
        int ind = rnd.nextInt(deck.size());
        Card card = (Card) (deck.toArray()[ind]);
        player.addCard(card);
    }

    public void winCheck(Player player) {
        int totalValue = 0;
        for (var card : player.getMyCards()) {
            totalValue += card.value();
        }
        if (totalValue >= 21) {
            player.setPassed(true);
            allPlayersPassed = true;
        }
    }

    public void endGame() {
        int[] totalValue = new int[2];
        int cnt = 0;
        for (var player : activePlayers) {
            for (var card : player.getMyCards()) {
                totalValue[cnt] += card.value();
            }
            player.setPassed(true);
            cnt++;
        }
        if (totalValue[0] > 21) {
            activePlayers.get(1).setStatus(PlayerStatus.WON);
            activePlayers.get(0).setStatus(PlayerStatus.LOSE);
        } else if (totalValue[1] > 21) {
            activePlayers.get(0).setStatus(PlayerStatus.WON);
            activePlayers.get(1).setStatus(PlayerStatus.LOSE);
        } else if (totalValue[0] > totalValue[1]) {
            activePlayers.get(0).setStatus(PlayerStatus.WON);
            activePlayers.get(1).setStatus(PlayerStatus.LOSE);
        } else if (totalValue[0] < totalValue[1]) {
            activePlayers.get(1).setStatus(PlayerStatus.WON);
            activePlayers.get(0).setStatus(PlayerStatus.LOSE);
        } else {
            activePlayers.get(0).setStatus(PlayerStatus.DRAW);
            activePlayers.get(1).setStatus(PlayerStatus.DRAW);
        }
    }

    public void closeGame() {
        for (var player : activePlayers) {
            player.getMyCards().clear();
            player.setStatus(PlayerStatus.WAITING);
            player.setGameIndex(-1);
            player.setPassed(false);
        }
        deck.clear();
        activePlayers.clear();
        allPlayersPassed = false;
    }

    public void gameProcess() {
        if (!allPlayersPassed) {
            if (activePlayers.get(0).isDrawACard() || activePlayers.get(1).isDrawACard()) {
                for (var player : activePlayers) {
                    if (player.getStatus() == PlayerStatus.YOUR_TURN) {
                        giveCard(player);
                        winCheck(player);
                        player.setDrawACard(false);
                    }
                }
                for (var item : activePlayers) {
                    if (item.getStatus() == PlayerStatus.OPPONENT_TURN) {
                        item.setStatus(PlayerStatus.YOUR_TURN);
                    } else {
                        item.setStatus(PlayerStatus.OPPONENT_TURN);
                    }
                }
            }
            if (activePlayers.get(0).getPassed() && activePlayers.get(1).getPassed()) {
                allPlayersPassed = true;
            }
        } else {
            endGame();
        }
    }
}
