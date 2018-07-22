package com.backbase.assignment.Repository;

import com.backbase.assignment.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findGameByGameId(String gameID);
}
