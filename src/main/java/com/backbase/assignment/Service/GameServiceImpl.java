package com.backbase.assignment.Service;

import com.backbase.assignment.Entity.Game;
import com.backbase.assignment.Enums.GamePlayer;
import com.backbase.assignment.Enums.GameStatus;
import com.backbase.assignment.Repository.GameRepository;
import com.backbase.assignment.Util.CommonUtil;
import com.backbase.assignment.Util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class GameServiceImpl implements GameService {
    private static String URL = "uri";
    private static String ID = "id";
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);

    @Value("${server.port}")
    private String port;

    @Autowired
    private GameRepository gameRepository;

    public ResponseEntity createNewGame() {
        Game game = new Game(CommonUtil.generateUniqueGameId(), GameStatus.CREATED.getName(),
                initializePits(), GamePlayer.DOWN.getName());
        Game gameSaved = gameRepository.save(game);

        return CommonUtil.createOkResponse(Pair.of(URL, createGameURL(gameSaved.getGameId())),
                Pair.of(ID, gameSaved.getGameId()));
    }

    public ResponseEntity playGame(String gameId, Integer pitId) {
        LOGGER.info("playGame: gameID: {}, pitID: {}", gameId, pitId);
        Game game = gameRepository.findGameByGameId(gameId);
        if (game == null) {
            LOGGER.info("game not found for gameID: " + gameId);
            return CommonUtil.createFailureResponse(new ArrayList<>(),
                    "Game not found with ID: " + gameId, HttpStatus.OK);
        }

        String pitJson = game.getPitJson();
        String player = game.getCurrentPlayer();

        if (StringUtils.isBlank(pitJson) || StringUtils.isBlank(player)) {
            LOGGER.info("either pitJson or payerID is blank");
            return CommonUtil.createFailureResponse(new ArrayList<>(),
                    "Invalid Game gameID: " + gameId, HttpStatus.OK);
        }

        Map<Integer, Integer> pitMap = jsonToMap(pitJson);

        //check if player and pitId combination ok
        if (!checkPlayerAndPitCombinationOk(pitMap, player, pitId)) {
            LOGGER.info("wrong pit chosen or pit has 0 stones. player: {}, pitId: {}", player, pitId);
            return CommonUtil.createFailureResponse(new ArrayList<>(),
                    "Invalid Game, wrong pitID chosen.", HttpStatus.OK);
        }

        JSONObject jsonObject;
        if (player.equalsIgnoreCase(GamePlayer.DOWN.getName())) {
            jsonObject = makeMove(pitMap, pitId, Constants.PLAYER_1_PITS,
                    Constants.PLAYER_1_HOUSE, Constants.PLAYER_2_HOUSE, game);

        } else {
            jsonObject = makeMove(pitMap, pitId, Constants.PLAYER_2_PITS,
                    Constants.PLAYER_2_HOUSE, Constants.PLAYER_1_HOUSE, game);
        }

        if (((String) jsonObject.get(Constants.MESSAGE)).equalsIgnoreCase(Constants.SUCCESS_STRING)) {
            return CommonUtil.createOkResponse(Pair.of(ID, jsonObject.get(ID)),
                    Pair.of("url", jsonObject.get("url")),
                    Pair.of("status", jsonObject.get("status")));
        } else {
            return CommonUtil.createFailureResponse(new ArrayList<String>(),
                    (String) jsonObject.get(Constants.MESSAGE), HttpStatus.OK);
        }

    }

    private JSONObject makeMove(Map<Integer, Integer> pitMap, Integer pitId,
                                List<Integer> playerPits, Integer playerHouse, Integer oppHouse, Game currentGame) {

        Integer stones = pitMap.get(pitId);
        String player = currentGame.getCurrentPlayer();
        LOGGER.info("make move for pitId: {}, player: {}, pitMap: {}", pitId, player, pitMap.toString());

        String gameStatus = GameStatus.IN_PROGRESS.getName();
        JSONObject jsonObject = new JSONObject();

        boolean skipOpponentHouse = false;
        boolean captureMode = isCaptureMode(pitMap, pitId, playerPits);


        try {
            for (int i = 1; i <= stones; i++) {
                if (pitId + i <= Constants.TOTAL_NO_PITS) {
                    if (pitId + i == oppHouse) {
                        skipOpponentHouse = true;
                    }
                    if (skipOpponentHouse) {
                        Integer val = pitMap.get(pitId + i + 1 - Constants.TOTAL_NO_PITS);
                        pitMap.put(pitId + i + 1 - Constants.TOTAL_NO_PITS, (val + 1));

                    } else {
                        Integer val = pitMap.get(pitId + i);
                        pitMap.put(pitId + i, (val + 1));
                    }
                } else {
                    if (pitId + i - Constants.TOTAL_NO_PITS == oppHouse) {
                        skipOpponentHouse = true;
                    }
                    if (skipOpponentHouse) {
                        Integer val = pitMap.get(pitId + i + 1 - Constants.TOTAL_NO_PITS);
                        pitMap.put(pitId + i + 1 - Constants.TOTAL_NO_PITS, val + 1);

                    } else {
                        Integer val = pitMap.get(pitId + i - Constants.TOTAL_NO_PITS);
                        pitMap.put(pitId + i - Constants.TOTAL_NO_PITS, val + 1);
                    }
                }
            }

            //make pitId content = 0
            pitMap.put(pitId, 0);

            if (captureMode) {
                Integer playerHouseValBefore = pitMap.get(playerHouse)
                        + pitMap.get(pitId + stones)
                        + pitMap.get(Constants.TOTAL_NO_PITS - pitId - stones - 1);
                pitMap.put(playerHouse, playerHouseValBefore);
                pitMap.put(pitId + stones, 0);
                pitMap.put(Constants.TOTAL_NO_PITS - pitId - stones - 1, 0);
            }

            String newPlayer;
            //chance again for player
            if (pitId + stones < Constants.TOTAL_NO_PITS) {
                if (pitId + stones == playerHouse) {
                    newPlayer = player;
                } else {
                    newPlayer = player.equalsIgnoreCase(GamePlayer.DOWN.getName()) ?
                            GamePlayer.UP.getName() : GamePlayer.DOWN.getName();
                }
            } else if (pitId + stones - Constants.TOTAL_NO_PITS == playerHouse) {
                newPlayer = player;
            } else {
                newPlayer = player.equalsIgnoreCase(GamePlayer.DOWN.getName()) ?
                        GamePlayer.UP.getName() : GamePlayer.DOWN.getName();
            }

            //game over condition
            if (isGameOver(player, pitMap)) {
                newPlayer = GamePlayer.NO_PLAYER.getName();
                gameStatus = GameStatus.COMPLETED.getName();
                pitMap = makeScoresAtEndOfGame(pitMap);
            }

            currentGame.setStatus(gameStatus);
            currentGame.setPitJson(mapToJson(pitMap));
            currentGame.setCurrentPlayer(newPlayer);
            saveGame(currentGame);
        } catch (Exception e) {
            LOGGER.error("exception in making move: ", e);
            jsonObject.put(Constants.MESSAGE, "Something went wrong");
            return jsonObject;
        }

        jsonObject.put(Constants.MESSAGE, Constants.SUCCESS_STRING);
        jsonObject.put(ID, currentGame.getGameId());
        jsonObject.put("url", createGameURL(currentGame.getGameId()));
        jsonObject.put("status", currentGame.getPitJson());
        return jsonObject;
    }

    private Map<Integer, Integer> makeScoresAtEndOfGame(Map<Integer, Integer> pitMap) {
        Integer player1Score = 0;
        Integer player2Score = 0;
        for (Integer key : pitMap.keySet()) {
            if (key >= 1 && key <= 6) {
                player1Score += pitMap.get(key);
                pitMap.put(key, 0);
            } else if (key >= 8 && key <= 13) {
                player2Score += pitMap.get(key);
                pitMap.put(key, 0);
            }
        }

        player1Score += pitMap.get(7);
        pitMap.put(7, player1Score);

        player2Score += pitMap.get(14);
        pitMap.put(14, player2Score);

        return pitMap;
    }

    private boolean isGameOver(String player, Map<Integer, Integer> pitMap) {
        if (player.equalsIgnoreCase(GamePlayer.DOWN.getName())) {
            for (int i = 1; i <= 6; i++) {
                if (pitMap.get(i) != 0) {
                    return false;
                }
            }
        } else if (player.equalsIgnoreCase(GamePlayer.UP.getName())) {
            for (int i = 8; i <= 13; i++) {
                if (pitMap.get(i) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    private boolean checkPlayerAndPitCombinationOk(Map<Integer, Integer> pit, String player, Integer pitId) {
        if (player.equalsIgnoreCase(GamePlayer.DOWN.getName())) {
            if (pit.get(pitId) == 0 || !Constants.PLAYER_1_PITS.contains(pitId)) {
                return false;
            }
            return true;
        } else if (player.equalsIgnoreCase(GamePlayer.UP.getName())) {
            if (pit.get(pitId) == 0 || !Constants.PLAYER_2_PITS.contains(pitId)) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isCaptureMode(Map<Integer, Integer> pitMap, Integer pitId, List<Integer> playerPits) {
        Integer stones = pitMap.get(pitId);
        if (playerPits.contains(pitId + stones) &&
                pitMap.get(pitId + stones) == 0 &&
                pitMap.get(Constants.TOTAL_NO_PITS - pitId - stones) > 0) {
            return true;
        }
        return false;
    }

    private String initializePits() {
        Map<Integer, Integer> pits = new LinkedHashMap<>();
        IntStream.rangeClosed(1, 14).forEach(
                x -> {
                    if (x == 7 || x == 14) pits.put(x, 0);
                    else pits.put(x, 6);
                });
        return mapToJson(pits);
    }

    private Map<Integer, Integer> jsonToMap(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<LinkedHashMap<Integer, Integer>>() {
        }.getType());
    }

    private String mapToJson(Map<Integer, Integer> map) {
        Map<String, String> stringMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> pair : map.entrySet()) {
            stringMap.put(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()));
        }
        Gson gson = new Gson();
        return gson.toJson(stringMap);
    }

    private String createGameURL(String gameId) {
        String IP = CommonUtil.getLocalIP();
        return "http://" + IP + ":" + port + "/games/" + gameId;
    }
}
