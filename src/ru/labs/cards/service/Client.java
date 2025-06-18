package ru.labs.cards.service;

import ru.labs.cards.data.Player;
import ru.labs.cards.data.PlayerStatus;

import java.util.Scanner;

public class Client {
    private String id;

    public void registration() {
        Player player = new Player("Player_" + Server.getPlayersNumber(), "abc");
        player.setStatus(PlayerStatus.WAITING);
        Server.getPlayers().put(Server.getPlayersNumber(), player);
        id = "" + Server.getPlayersNumber();
        Server.setPlayersNumber(Server.getPlayersNumber() + 1);
    }

    public void joinGame() {
        Scanner scr = new Scanner(System.in);
        System.out.println("\nGames list: ");
        for (int i = 0; i < Server.getGames().size(); i++) {
            if (Server.getGames().get(i).getActivePlayers().size() < 2) {
                System.out.println("game " + i + ", " + Server.getGames().get(i).getActivePlayers().size() + " players; ");
            }
        }
        System.out.println("\nEnter game number (-1 for new game):\n");
        int gameID = scr.nextInt();
        Player player = Server.getPlayers().get(id);
        if (gameID == -1) {
            Server.createGame();
            player.setGameIndex(Server.getGames().size() - 1);
            gameID = player.getGameIndex();
        } else if (Server.getGames().get(gameID) != null && Server.getGames().get(gameID).getActivePlayers().size() < 2) {
            player.setGameIndex(gameID);
        } else {
            System.out.println("\nWrong game number. New game created\n");
            Server.createGame();
            player.setGameIndex(Server.getGames().size() - 1);
            gameID = player.getGameIndex();
        }
        Server.getGames().get(player.getGameIndex()).addPlayer(player);
        if (Server.getGames().get(gameID).getActivePlayers().size() != 1) {
            for (var item : Server.getGames().get(player.getGameIndex()).getActivePlayers()) {
                if (Server.getGames().get(item.getGameIndex()).getActivePlayers().get(0) == item) {
                    item.setStatus(PlayerStatus.YOUR_TURN);
                } else {
                    item.setStatus(PlayerStatus.OPPONENT_TURN);
                }
            }
            Server.getGames().get(gameID).startGame();
        }
    }

    public void leaveGame() {
        for (var item : Server.getGames().get(Server.getPlayers().get(id).getGameIndex()).getActivePlayers()) {
            item.setStatus(PlayerStatus.WAITING);
        }
        Server.getGames().get(Server.getPlayers().get(id).getGameIndex()).closeGame();
        Server.getPlayers().remove(id);
    }

    public void leaveServer() {
        for (var item : Server.getGames().get(Server.getPlayers().get(id).getGameIndex()).getActivePlayers()) {
            item.setStatus(PlayerStatus.WAITING);
        }
        Server.getGames().get(Server.getPlayers().get(id).getGameIndex()).closeGame();
        Server.getPlayers().remove(id);
    }

    public void drawACard() {
        Player player = Server.getPlayers().get(id);
        player.setDrawACard(true);
        Server.getGames().get(Server.getPlayers().get(id).getGameIndex()).gameProcess();
    }

    public void pass() {
        Player player = Server.getPlayers().get(id);
        player.setPassed(true);
        Server.getGames().get(Server.getPlayers().get(id).getGameIndex()).gameProcess();
    }

    public void getGameInfo() {
        System.out.println("id: " + id);
        for (var player : Server.getGames().get(Server.getPlayers().get(id).getGameIndex()).getActivePlayers()) {
            System.out.println(player.getName() + "'s cards: " + player.getMyCards());
        }
    }
}