package ru.labs.cards.service;

import org.springframework.web.client.HttpClientErrorException;
import ru.labs.cards.data.Card;
import ru.labs.cards.data.Game;
import ru.labs.cards.data.Player;
import ru.labs.cards.data.PlayerStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Service class (singleton, static).
 * Manages sessions.
 */
public class SessionManager {
    private static final Map<String, SessionData> SESSION_DATA_MAP = new ConcurrentHashMap<>();
    private static final Map<Integer, Game> GAMES_LIST_MAP = new ConcurrentHashMap<>();

    /**
     * Registers new session
     *
     * @return UUID of created session
     */
    public static String register(String firstName, String lastName) {
        String sessionId = UUID.randomUUID().toString();
        Player player = new Player(firstName, lastName);
        player.setStatus(PlayerStatus.CONNECTED);
        SESSION_DATA_MAP.put(sessionId, new SessionData(System.currentTimeMillis(), true, player));
        return sessionId;
    }

    /**
     * Checks if session exists and alive
     *
     * @param sessionId UUID of session to check
     * @return true if session alive, false otherwise
     */
    public static boolean checkSession(String sessionId) {
        SessionData data = SESSION_DATA_MAP.get(sessionId);
        if (data == null || !data.isAlive) {
            return false;
        }

        long currentsTime = System.currentTimeMillis();
        data.isAlive = (currentsTime - data.lastUpdate) <= 2 * 2000;
        data.lastUpdate = currentsTime;
        // update session data
        SESSION_DATA_MAP.put(sessionId, data);

        return data.isAlive;
    }

    /**
     * Servise method to see session data
     * @return current session data
     */
    /*public static List<String> getStat() {
        List<String> stat = new ArrayList<>();
        for (var item : SESSION_DATA_MAP.keySet()) {
            stat.add("sess=" + item + ", data=" + SESSION_DATA_MAP.get(item));
        }
        return stat;
    }*/

    public static GameInfo getInfo(String sessionId) {
        Player player = SESSION_DATA_MAP.get(sessionId).player;
        Player opponent = null;
        Collection<Card> opponentCollection = Collections.emptyList();
        if (GAMES_LIST_MAP.get(player.getGameIndex()) != null) {
            for (var item : GAMES_LIST_MAP.get(player.getGameIndex()).getActivePlayers()) {
                if (item != player) {
                    opponent = item;
                    opponentCollection = opponent.getMyCards();
                }
            }
        }
        return new GameInfo(player.getMyCards().stream().toList(), opponentCollection.stream().toList(), player.getStatus(), null);
    }

    public static int join(String sessionId, int gameId){
        if (GAMES_LIST_MAP.get(gameId) == null){
            //gameId = createNewGame();
            throw new HttpClientErrorException(NOT_FOUND,"Game id="+gameId+" not found");
        }
        if (GAMES_LIST_MAP.get(gameId).getActivePlayers().size() == 2){
            throw new HttpClientErrorException(CONFLICT,"Game id="+gameId+" too many players");
        }
        SESSION_DATA_MAP.get(sessionId).player.setGameIndex(gameId);
        GAMES_LIST_MAP.get(gameId).getActivePlayers().add(SESSION_DATA_MAP.get(sessionId).player);
        SESSION_DATA_MAP.get(sessionId).player.setStatus(PlayerStatus.WAIT_OPPONENT_CONNECTION);
        GAMES_LIST_MAP.get(gameId).setName(GAMES_LIST_MAP.get(gameId).getName() + " " + SESSION_DATA_MAP.get(sessionId).player.getName() + "_" + SESSION_DATA_MAP.get(sessionId).player.getSurname());
        boolean isFirst = true;
        if (GAMES_LIST_MAP.get(gameId).getActivePlayers().size() == 2){
            GAMES_LIST_MAP.get(gameId).startGame();
            for (var player : GAMES_LIST_MAP.get(gameId).getActivePlayers()){
                if (isFirst){
                    player.setStatus(PlayerStatus.PLAYER_MOVE);
                    isFirst = false;
                }
                else{
                    player.setStatus(PlayerStatus.OPPONENT_MOVE);
                }
            }
        }
        return gameId;
    }

    public static String startGame(String sessionId){
        int gameId = createNewGame();
        join(sessionId, gameId);
        return Integer.toString(SESSION_DATA_MAP.get(sessionId).player.getGameIndex());
    }

    public static int createNewGame(){
        int gameId = GAMES_LIST_MAP.size();
        Game game = new Game("Game_" + gameId);
        GAMES_LIST_MAP.put(gameId, game);
        return gameId;
    }

    public static List<GameDto> gameList(){
        List<GameDto> games = new ArrayList<>();
        for (var item : GAMES_LIST_MAP.keySet()) {
            if (GAMES_LIST_MAP.get(item).getActivePlayers().size() < 2) {
                games.add(new GameDto(GAMES_LIST_MAP.get(item).getName(), Integer.toString(item)));
            }
        }
        return games;
    }

    public static void stopGame(String sessionId){
        int gameIndex = SESSION_DATA_MAP.get(sessionId).player.getGameIndex();
        Player player = SESSION_DATA_MAP.get(sessionId).player;
        player.setStatus(PlayerStatus.CONNECTED);
        player.getMyCards().clear();
        player.setGameIndex(-1);
        player.setPassed(false);
        if (GAMES_LIST_MAP.get(gameIndex).getActivePlayers().size() == 1) {
            GAMES_LIST_MAP.get(gameIndex).getDeck().clear();
            GAMES_LIST_MAP.get(gameIndex).getActivePlayers().clear();
            GAMES_LIST_MAP.get(gameIndex).setAllPlayersPassed(false);
            GAMES_LIST_MAP.remove(gameIndex);
        }
    }

    public static void nextTurn(String sessionId){
        GAMES_LIST_MAP.get(SESSION_DATA_MAP.get(sessionId).player.getGameIndex()).winCheck(SESSION_DATA_MAP.get(sessionId).player);
        if (GAMES_LIST_MAP.get(SESSION_DATA_MAP.get(sessionId).player.getGameIndex()).isAllPlayersPassed()){
            GAMES_LIST_MAP.get(SESSION_DATA_MAP.get(sessionId).player.getGameIndex()).endGame();
        }
        Game game = GAMES_LIST_MAP.get(SESSION_DATA_MAP.get(sessionId).player.getGameIndex());
        Player opponent = null;
        for (var player : game.getActivePlayers()){
            if (player != SESSION_DATA_MAP.get(sessionId).player){
                opponent = player;
            }
        }
        if ((SESSION_DATA_MAP.get(sessionId).player.getStatus() == PlayerStatus.PLAYER_MOVE || SESSION_DATA_MAP.get(sessionId).player.getStatus() == PlayerStatus.OPPONENT_MOVE) && !opponent.getPassed()) {
            for (var player : GAMES_LIST_MAP.get(SESSION_DATA_MAP.get(sessionId).player.getGameIndex()).getActivePlayers()) {
                if (player.getStatus() == PlayerStatus.PLAYER_MOVE) {
                    player.setStatus(PlayerStatus.OPPONENT_MOVE);
                } else {
                    player.setStatus(PlayerStatus.PLAYER_MOVE);
                }
            }
        }
    }

    public static void takeCard(String sessionId){
        if (SESSION_DATA_MAP.get(sessionId).player.getStatus() == PlayerStatus.PLAYER_MOVE || SESSION_DATA_MAP.get(sessionId).player.getStatus() == PlayerStatus.OPPONENT_MOVE) {
            int gameIndex = SESSION_DATA_MAP.get(sessionId).player.getGameIndex();
            GAMES_LIST_MAP.get(gameIndex).giveCard(SESSION_DATA_MAP.get(sessionId).player);
            nextTurn(sessionId);
        }
    }

    public static void passMove(String sessionId){
        if (SESSION_DATA_MAP.get(sessionId).player.getStatus() == PlayerStatus.PLAYER_MOVE || SESSION_DATA_MAP.get(sessionId).player.getStatus() == PlayerStatus.OPPONENT_MOVE) {
        SESSION_DATA_MAP.get(sessionId).player.setPassed(true);
        boolean allPassed = true;
        for (var player : GAMES_LIST_MAP.get(SESSION_DATA_MAP.get(sessionId).player.getGameIndex()).getActivePlayers()){
            if (!player.getPassed()){
                allPassed = false;
            }
        }
        if (allPassed){
            GAMES_LIST_MAP.get(SESSION_DATA_MAP.get(sessionId).player.getGameIndex()).setAllPlayersPassed(true);
        }
        nextTurn(sessionId);
        }
    }

    /**
     * Internal datatype for session information
     */
    private static class SessionData {
        private long lastUpdate;
        private boolean isAlive;
        private Player player;

        public SessionData(long lastUpdate, boolean isAlive, Player player) {
            this.lastUpdate = lastUpdate;
            this.isAlive = isAlive;
            this.player = player;
        }

        @Override
        public String toString() {
            return "SessionData{" +
                    "lastUpdate=" + lastUpdate +
                    ", isAlive=" + isAlive +
                    ", player=" + player +
                    '}';
        }
    }
}

