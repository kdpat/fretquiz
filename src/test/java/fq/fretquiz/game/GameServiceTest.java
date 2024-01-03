package fq.fretquiz.game;

import fq.fretquiz.game.model.*;
import fq.fretquiz.theory.fretboard.FretCoord;
import fq.fretquiz.user.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GameServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    GameService gameService;

    Logger log = LoggerFactory.getLogger(GameServiceTest.class);

    @Test
    void createGame() {
        var user0 = userService.createUser();

        var settings = Settings.createDefault();
        settings.setRoundCount(2);

        // create game
        var game0 = gameService.createGame(user0, settings);
        log.info("game: {}", game0);
        assertEquals(game0.status(), Status.INIT);

        // start round 0
        var gameUpdate0 = gameService.startNewRound(game0, user0);
        log.info("round started: {}", gameUpdate0);

        Game game1 = null;
        if (gameUpdate0 instanceof GameUpdate.RoundStarted(var game, var round)) {
            game1 = game;
        }

        assertNotNull(game1);
        log.info("round 0: {}", game1.currentRound().orElseThrow());
        assertEquals(game1.status(), Status.PLAYING);

        // guess 0
        var player0 = game1.findPlayerByUserId(user0.id()).orElseThrow();
        var guess0 = new Guess.Payload(player0.id(), new FretCoord(1, 0));
        var gameUpdate1 = gameService.handleGuess(game1, guess0);

        Game game2 = null;
        if (gameUpdate1 instanceof GameUpdate.GuessHandled(var game, var guess)) {
            log.info("handled: {}", guess);
            game2 = game;
        }

        assertNotNull(game2);

        // player0 already guessed, so the game won't be updated and the returned guess will be null
        var guessToIgnore = new Guess.Payload(player0.id(), new FretCoord(6, 4));
        var gameUpdate2 = gameService.handleGuess(game2, guessToIgnore);
        assertInstanceOf(GameUpdate.None.class, gameUpdate2);

        var gameUpdate3 = gameService.startNewRound(game2, user0);

        Game game3 = null;
        if (gameUpdate3 instanceof GameUpdate.RoundStarted(var game, var round)) {
            game3 = game;
        }

        assertNotNull(game3);
        assertEquals(game3.status(), Status.PLAYING);

        var guess1 = new Guess.Payload(player0.id(), new FretCoord(1, 0));
        var gameUpdate4 = gameService.handleGuess(game3, guess1);

        Game game4 = null;
        if (gameUpdate4 instanceof GameUpdate.GuessHandled(var game, var guess)) {
            game4 = game;
        }
        assertNotNull(game4);
        log.info("guess 1: {}", game4);
        assertEquals(game4.status(), Status.GAME_OVER);

        var foundGame = gameService.findGame(game0.id()).orElseThrow();
        log.info("found game: {}", foundGame);
    }
}