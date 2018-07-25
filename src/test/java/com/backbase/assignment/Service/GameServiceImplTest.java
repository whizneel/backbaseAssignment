package com.backbase.assignment.Service;

import com.backbase.assignment.Entity.Game;
import com.backbase.assignment.Enums.GamePlayer;
import com.backbase.assignment.Enums.GameStatus;
import com.backbase.assignment.Repository.GameRepository;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameServiceImplTest {
    private static String URL = "uri";
    private static String ID = "id";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    GameRepository gameRepository;

    @InjectMocks
    GameServiceImpl gameServiceImpl;

    private Game game;

    @org.junit.Before
    public void setUp() throws Exception {
        game = newGame();
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void createNewGame() {
        //
        //given
        //
        doReturn(game).when(gameRepository).save(any(Game.class));

        //
        //when
        //
        ResponseEntity responseEntityActual = gameServiceImpl.createNewGame();

        //
        //then
        //
        assertEquals(200, responseEntityActual.getStatusCode().value());
        assertNotNull(responseEntityActual.toString());

    }

    @org.junit.Test
    public void playGame_player1_normalPlay() {
        //
        //given
        //
        doReturn(game).when(gameRepository).findGameByGameId(game.getGameId());
        String pitJson = createPitJson(Arrays.asList(6, 0, 7, 7, 7, 7, 1, 7, 6, 6, 6, 6, 6, 0));

        //
        //when
        //
        ResponseEntity responseEntityActual = gameServiceImpl.playGame(game.getGameId(), 2);

        //
        //then
        //
        assertEquals(200, responseEntityActual.getStatusCodeValue());

        String responseBody = (String) responseEntityActual.getBody();

        JSONObject jsonObject = new JSONObject(responseBody);
        String pitJsonReceived = jsonObject.getString("status");
        assertEquals(pitJson, pitJsonReceived);
    }

    //Assumption: player 1 has moved with pit2
    @org.junit.Test
    public void playGame_player2_normalPlay() {
        //
        //given
        //
        Game newGame = modifyGame(GameStatus.IN_PROGRESS.toString(),
                createPitJson(Arrays.asList(6, 0, 7, 7, 7, 7, 1, 7, 6, 6, 6, 6, 6, 0)), GamePlayer.UP.getName(), game);
        doReturn(newGame).when(gameRepository).findGameByGameId(newGame.getGameId());
        String pitJson = createPitJson(Arrays.asList(7, 0, 7, 7, 7, 7, 1, 0, 7, 7, 7, 7, 7, 1));

        //
        //when
        //
        ResponseEntity responseEntityActual = gameServiceImpl.playGame(game.getGameId(), 8);

        //
        //then
        //
        assertEquals(200, responseEntityActual.getStatusCodeValue());

        String responseBody = (String) responseEntityActual.getBody();

        JSONObject jsonObject = new JSONObject(responseBody);
        String pitJsonReceived = jsonObject.getString("status");
        assertEquals(pitJson, pitJsonReceived);
    }


    @org.junit.Test
    public void playGame_player1_skipOpponentHouse() {
        //
        //given
        //
        Game newGame = modifyGame(GameStatus.IN_PROGRESS.toString(),
                createPitJson(Arrays.asList(4, 3, 3, 4, 3, 10, 4, 5, 3, 6, 7, 8, 6, 6)), GamePlayer.DOWN.getName(), game);
        doReturn(newGame).when(gameRepository).findGameByGameId(newGame.getGameId());
        String pitJson = createPitJson(Arrays.asList(5, 4, 4, 4, 3, 0, 5, 6, 4, 7, 8, 9, 7, 6));

        //
        //when
        //
        ResponseEntity responseEntityActual = gameServiceImpl.playGame(game.getGameId(), 6);

        //
        //then
        //
        assertEquals(200, responseEntityActual.getStatusCodeValue());

        String responseBody = (String) responseEntityActual.getBody();

        JSONObject jsonObject = new JSONObject(responseBody);
        String pitJsonReceived = jsonObject.getString("status");
        assertEquals(pitJson, pitJsonReceived);
    }

    @org.junit.Test
    public void playGame_player1_captureOtherPlayerMarbles() {
        //
        //given
        //
        Game newGame = modifyGame(GameStatus.IN_PROGRESS.toString(),
                createPitJson(Arrays.asList(2, 4, 0, 8, 8, 8, 10, 3, 4, 3, 4, 4, 7, 7)), GamePlayer.DOWN.getName(), game);
        doReturn(newGame).when(gameRepository).findGameByGameId(newGame.getGameId());
        String pitJson = createPitJson(Arrays.asList(0, 5, 0, 8, 8, 8, 14, 3, 4, 0, 4, 4, 7, 7));

        //
        //when
        //
        ResponseEntity responseEntityActual = gameServiceImpl.playGame(game.getGameId(), 1);

        //
        //then
        //
        assertEquals(200, responseEntityActual.getStatusCodeValue());

        String responseBody = (String) responseEntityActual.getBody();

        JSONObject jsonObject = new JSONObject(responseBody);
        String pitJsonReceived = jsonObject.getString("status");
        assertEquals(pitJson, pitJsonReceived);
    }

    @org.junit.Test
    public void playGame_player1_gameEnd() {
        //
        //given
        //
        Game newGame = modifyGame(GameStatus.IN_PROGRESS.toString(),
                createPitJson(Arrays.asList(0, 0, 0, 0, 0, 1, 39, 0, 0, 8, 2, 3, 7, 12)), GamePlayer.DOWN.getName(), game);
        doReturn(newGame).when(gameRepository).findGameByGameId(newGame.getGameId());
        String pitJson = createPitJson(Arrays.asList(0, 0, 0, 0, 0, 0, 40, 0, 0, 0, 0, 0, 0, 32));

        //
        //when
        //
        ResponseEntity responseEntityActual = gameServiceImpl.playGame(game.getGameId(), 6);

        //
        //then
        //
        assertEquals(200, responseEntityActual.getStatusCodeValue());

        String responseBody = (String) responseEntityActual.getBody();

        JSONObject jsonObject = new JSONObject(responseBody);
        String pitJsonReceived = jsonObject.getString("status");
        assertEquals(pitJson, pitJsonReceived);
    }

    private Game newGame() {
        Map<String, String> map = new LinkedHashMap<>();
        IntStream.rangeClosed(1, 14).forEach(
                x -> {
                    if (x == 7 || x == 14) map.put(String.valueOf(x), "0");
                    else map.put(String.valueOf(x), String.valueOf(6));
                });
        Gson gson = new Gson();

        Game game = new Game("kalah-8613296350260447313",
                GameStatus.CREATED.getName(), gson.toJson(map), GamePlayer.DOWN.getName());

        return game;
    }

    private String createPitJson(List<Integer> list) {
        int i = 1;
        Map<String, String> map = new LinkedHashMap<>();
        for (Integer x : list) {
            map.put(String.valueOf(i), String.valueOf(x));
            i++;
        }
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    private Game modifyGame(String gameStatus, String pitJson, String currentPlayer, Game gameToModify) {
        gameToModify.setStatus(gameStatus);
        gameToModify.setPitJson(pitJson);
        gameToModify.setCurrentPlayer(currentPlayer);
        return gameToModify;
    }
}