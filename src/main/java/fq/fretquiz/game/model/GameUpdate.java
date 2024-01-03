package fq.fretquiz.game.model;

public sealed interface GameUpdate {

    record RoundStarted(Game game, Round round) implements GameUpdate { }

    record GuessHandled(Game game, Guess guess) implements GameUpdate { }

    record None(String reason) implements GameUpdate { }
}
