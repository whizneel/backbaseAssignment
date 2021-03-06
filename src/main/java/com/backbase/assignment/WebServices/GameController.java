package com.backbase.assignment.WebServices;

import com.backbase.assignment.Service.GameService;
import com.backbase.assignment.Util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@RestController
public class GameController {

    @Autowired
    private GameService gameService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    @RequestMapping(value = "/ping", method = RequestMethod.GET, headers = "Accept=application/json")
    public String sayHello() {
        return "pong";
    }

    //API to create new game
    @RequestMapping(value = "/games", method = RequestMethod.POST, headers = "Accept=application/json")
    public @ResponseBody
    ResponseEntity createGame() {
        LOGGER.info("request to create new game");
        return gameService.createNewGame();
    }

    //API to play game
    //takes gameID and pitID
    @RequestMapping(value = "/games/{gameId}/pits/{pitId}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public @ResponseBody
    ResponseEntity playGame(@PathVariable Map<String, String> pathVariablesMap) {
        String gameId = Optional.ofNullable(pathVariablesMap.get("gameId")).orElse(null);
        String pitId = Optional.ofNullable(pathVariablesMap.get("pitId")).orElse(null);

        if (StringUtils.isBlank(gameId) || StringUtils.isBlank(pitId)) {
            LOGGER.info("either gameId or pitId is blank");
            return CommonUtil.createFailureResponse(new ArrayList<>(), "Invalid input", HttpStatus.OK);
        }
        try {
            LOGGER.info("request to play game: " + gameId + "for pit ID: " + pitId);
            Integer pitIdNumeric = Integer.valueOf(pitId);
            return gameService.playGame(gameId, pitIdNumeric);
        } catch (NumberFormatException e) {
            return CommonUtil.createFailureResponse(new ArrayList<>(), "Invalid pitId", HttpStatus.OK);
        }

    }
}