package ru.labs.cards.service;

import ru.labs.cards.data.Game;
import ru.labs.cards.data.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final List<Game> games = new LinkedList<>();
    private static final Map<Integer, Player> players = new ConcurrentHashMap<>();
    private static int playersNumber = 0;

    private Server() {
    }

    public static int getPlayersNumber() {
        return playersNumber;
    }

    public static void setPlayersNumber(int playersNumber) {
        Server.playersNumber = playersNumber;
    }

    public static void createGame() {
        games.add(new Game("Game_" + games.size()));
    }

    public static List<Game> getGames() {
        return games;
    }

    public static Map<Integer, Player> getPlayers() {
        return players;
    }
}
