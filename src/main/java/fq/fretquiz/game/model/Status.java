package fq.fretquiz.game.model;

public enum Status {
    INIT, // the game has been created, no rounds started
    PLAYING, // round has started, players are guessing
    ROUND_OVER, // all players have guessed, waiting on next round
    GAME_OVER, // all rounds have been played
    NO_PLAYERS // all players have left
}
