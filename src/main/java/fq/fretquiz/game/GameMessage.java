package fq.fretquiz.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import fq.fretquiz.game.model.Game;
import fq.fretquiz.game.model.GameUpdate;
import fq.fretquiz.game.model.Guess;
import fq.fretquiz.game.model.Round;

public sealed interface GameMessage {

    @JsonProperty
    Type type();

    record GameNotFound() implements GameMessage {

        @Override
        public Type type() {
            return Type.GAME_NOT_FOUND;
        }
    }

    record FoundGame(Game game, Long playerId) implements GameMessage {

        @Override
        public Type type() {
            return Type.FOUND_GAME;
        }
    }

    record RoundStarted(Game game, Round round) implements GameMessage {

        @Override
        public Type type() {
            return Type.ROUND_STARTED;
        }
    }

    record GuessHandled(Game game, Guess guess) implements GameMessage {

        @Override
        public Type type() {
            return Type.GUESS_RESULT;
        }
    }

    record NoUpdate(String reason) implements GameMessage {

        @Override
        public Type type() {
           return Type.NO_UPDATE;
        }
    }

    static GameMessage from(GameUpdate gameUpdate) {
        return switch (gameUpdate) {
            case GameUpdate.RoundStarted(var game, var round) -> new RoundStarted(game, round);
            case GameUpdate.GuessHandled(var game, var guess) -> new GuessHandled(game, guess);
            case GameUpdate.None(var reason) -> new NoUpdate(reason);
            default -> throw new IllegalStateException("Unexpected value: " + gameUpdate);
        };
    }

    enum Type {
        GAME_NOT_FOUND,
        FOUND_GAME,
        ROUND_STARTED,
        GUESS_RESULT,
        NO_UPDATE
    }
}
