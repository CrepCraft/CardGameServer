package ru.labs.cards.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.labs.cards.data.Game;
import ru.labs.cards.data.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.labs.cards.service.SessionManager.checkSession;

@RestController
public class Server {
    //private static final List<Game> games = new LinkedList<>();
    //private static final Map<Integer, Player> players = new ConcurrentHashMap<>();
    //private static int playersNumber = 0;

    //private Server() {
    //}

    /*public static int getPlayersNumber() {
        return playersNumber;
    }

    public static void setPlayersNumber(int playersNumber) {
        Server.playersNumber = playersNumber;
    }*/

    /*public static void createGame() {
        games.add(new Game("Game_" + games.size()));
    }

    public static List<Game> getGames() {
        return games;
    }

    public static Map<Integer, Player> getPlayers() {
        return players;
    }*/

    @PostMapping("/register")
    public String register(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "lastName") String lastName
    ) {
        String session = SessionManager.register(firstName, lastName);
        System.out.println("Register player: "+firstName+' '+lastName+", session="+session);
        return session;
    }

    @GetMapping("/getInfo")
    public GameInfo getInfo(
            @RequestParam(value = "session") String session
    ) {
        if (!checkSession(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is not valid");
        }
        System.out.println("Get information..." + session);
        return SessionManager.getInfo(session);
    }

    @PostMapping("/join")
    public void join(
            @RequestParam(value = "session") String session,
            @RequestParam(value = "id") String id
    ) {
        if (!SessionManager.checkSession(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is not valid");
        }
        SessionManager.join(session, Integer.parseInt(id));
        System.out.println("Joined..." + session + " id: " + id);
    }

    @GetMapping("/gameList")
    public Collection<GameDto> gameList(
            @RequestParam(value = "session") String session
    ) {
        if (!SessionManager.checkSession(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is not valid");
        }
        System.out.println("List of games..." + session);
        return SessionManager.gameList();
    }

    @PostMapping("/startGame")
    public String startGame(
            @RequestParam(value = "session") String session
    ) {
        if (!SessionManager.checkSession(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is not valid");
        }
        System.out.println("Started..." + session);
        return SessionManager.startGame(session);
    }

    @PostMapping("/stopGame")
    public void stopGame(
            @RequestParam(value = "session") String session
    ) {
        if (!SessionManager.checkSession(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is not valid");
        }
        SessionManager.stopGame(session);
        System.out.println("Stopped..." + session);
    }

    @PostMapping("/takeCard")
    public void takeCard(
            @RequestParam(value = "session") String session
    ){
        if (!SessionManager.checkSession(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is not valid");
        }
        SessionManager.takeCard(session);
        System.out.println("Card..." + session);
    }

    @PostMapping("/passMove")
    public void passMove(
            @RequestParam(value = "session") String session
    ){
        if (!SessionManager.checkSession(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is not valid");
        }
        SessionManager.passMove(session);
        System.out.println("Pass..." + session);
    }
}
