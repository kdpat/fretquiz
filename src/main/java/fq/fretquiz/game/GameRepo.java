package fq.fretquiz.game;

import fq.fretquiz.game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepo extends JpaRepository<Game, Long> {
}
