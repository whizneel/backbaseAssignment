package com.backbase.assignment.Service;

import org.springframework.http.ResponseEntity;

public interface GameService {

    ResponseEntity createNewGame();

    ResponseEntity playGame(String gameId, Integer pitId);
}
