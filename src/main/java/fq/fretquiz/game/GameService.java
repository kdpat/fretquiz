package fq.fretquiz.game;

import fq.fretquiz.game.model.*;
import fq.fretquiz.theory.fretboard.Fretboard;
import fq.fretquiz.theory.music.Note;
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
    public Game createWithHost(User host) {
        var game = Game.create(host);
        return gameRepo.save(game);
    }

    @Transactional
    public GameUpdate addPlayer(Game game, User user) {
        if (game.userIsPlaying(user.id())) {
            return new GameUpdate.None("User is already playing.");
        }

        var player = Player.from(user);
        game.addPlayer(player);
        game = gameRepo.save(game);

        return new GameUpdate.PlayerJoined(game, player);
    }

    @Transactional
    public GameUpdate startNewRound(Game game, User user) {
        Long hostId = game.host().id();
        boolean userIsHost = Objects.equals(user.id(), hostId);

        if (!userIsHost) {
            return new GameUpdate.None("User must be host to start round.");
        }

        var round = Round.create(game.settings());
        game.addRound(round);
        game.setStatus(Status.PLAYING);

        game = gameRepo.save(game);
        return new GameUpdate.RoundStarted(game, round);
    }

    @Transactional
    public GameUpdate handleGuess(Game game, Guess.Payload payload) {
        Round round = game.currentRound().orElseThrow();
        Player player = game.findPlayer(payload.playerId()).orElseThrow();

        if (round.playerHasGuessed(player.id())) {
            return new GameUpdate.None("Player already guessed.");
        }

        Fretboard fretboard = game.settings().fretboard();
        Note guessedNote = fretboard.findNote(payload.fretCoord()).orElseThrow();
        boolean isCorrect = round.noteToGuess().isEnharmonicWith(guessedNote);

        if (isCorrect) {
            player.incrementScore();
        } else {
            player.decrementScore();
        }

        var guess = Guess.create(payload, isCorrect);
        round.addGuess(guess);

        boolean roundIsOver = round.guessCount() == game.playerCount();
        if (roundIsOver) {
            Status status = game.roundsFull()
                    ? Status.GAME_OVER
                    : Status.ROUND_OVER;

            game.setStatus(status);
        }

        game = gameRepo.save(game);
        return new GameUpdate.GuessHandled(game, guess);
    }
}
