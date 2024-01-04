package fq.fretquiz.game;

import fq.fretquiz.game.model.*;
import fq.fretquiz.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepo gameRepo;

    public GameService(GameRepo gameRepo) {
        this.gameRepo = gameRepo;
    }

    public Optional<Game> findGame(Long id) {
        return gameRepo.findById(id);
    }

    @Transactional
    public Game createGame(User host, Settings settings) {
        var game = Game.create(host, settings);
        return gameRepo.save(game);
    }

    @Transactional
    public Game createLobby(User host) {
        var game = Game.createLobby(host);
        return gameRepo.save(game);
    }

    @Transactional
    public Game addPlayer(Game game, User user) {
        var player = Player.from(user);
        game.addPlayer(player);
        return gameRepo.save(game);
    }

    @Transactional
    public GameUpdate startNewRound(Game game, User user) {
        var hostId = game.host().id();
        var userIsHost = Objects.equals(user.id(), hostId);

        if (!userIsHost) {
            return new GameUpdate.None("User is not not host.");
        }

        var round = Round.create(game.settings());
        game.addRound(round);
        game.setStatus(Status.PLAYING);

        game = gameRepo.save(game);
        return new GameUpdate.RoundStarted(game, round);
    }

    @Transactional
    public GameUpdate handleGuess(Game game, Guess.Payload payload) {
        var playerId = payload.playerId();
        var round = game.currentRound().orElseThrow();

        if (round.playerHasGuessed(playerId)) {
            return new GameUpdate.None("Player already guessed.");
        }

        var fretboard = game.settings().fretboard();
        var guessedNote = fretboard.findNote(payload.fretCoord()).orElseThrow();
        var isCorrect = round.noteToGuess().isEnharmonicWith(guessedNote);

        if (isCorrect) {
            var player = game.findPlayer(playerId).orElseThrow();
            player.incrementScore();
        }

        var guess = Guess.create(payload, isCorrect);
        round.addGuess(guess);

        var playerCount = game.players().size();

        if (round.guessesFull(playerCount)) {
            var status = game.roundsFull()
                    ? Status.GAME_OVER
                    : Status.ROUND_OVER;

            game.setStatus(status);
        }

        game = gameRepo.save(game);
        return new GameUpdate.GuessHandled(game, guess);
    }

}
