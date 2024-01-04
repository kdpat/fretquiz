package fq.fretquiz.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import fq.fretquiz.game.model.*;

public sealed interface GameMessage {

    @JsonProperty
    Type type();

    enum Type {
        GAME_NOT_FOUND,
        FOUND_GAME,
        PLAYER_JOINED,
        NO_UPDATE,
        ROUND_STARTED,
        GUESS_RESULT
    }

    GameNotFound GAME_NOT_FOUND = new GameNotFound();

    static GameMessage from(GameUpdate gameUpdate) {
        return switch (gameUpdate) {
            case GameUpdate.None(var reason) -> new NoUpdate(reason);
            case GameUpdate.PlayerJoined(var game, var player) -> new PlayerJoined(game, player);
            case GameUpdate.RoundStarted(var game, var round) -> new RoundStarted(game, round);
            case GameUpdate.GuessHandled(var game, var guess) -> new GuessHandled(game, guess);
        };
    }

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

    record PlayerJoined(Game game, Player player) implements GameMessage {

        @Override
        public Type type() {
            return Type.PLAYER_JOINED;
        }
    }

    record NoUpdate(String reason) implements GameMessage {

        @Override
        public Type type() {
            return Type.NO_UPDATE;
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
}
