package fq.fretquiz.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fq.fretquiz.App;
import fq.fretquiz.theory.fretboard.FretCoord;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.time.Instant;

@Embeddable
public class Guess {

    @Embedded
    private Payload payload;
    private boolean isCorrect;
    private Instant createdAt;

    /**
     * Represents the data sent from the client when a user guesses (clicks the fretboard).
     *
     * @param playerId  the player who guessed
     * @param fretCoord the string and fret that they clicked
     */
    @Embeddable
    public record Payload(Long playerId,
                          FretCoord fretCoord) {
    }

    public static Guess create(Payload payload, boolean isCorrect) {
        var guess = new Guess();
        guess.payload = payload;
        guess.isCorrect = isCorrect;
        guess.createdAt = App.nowMillis();
        return guess;
    }

    @JsonProperty
    public Payload payload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @JsonProperty(value = "isCorrect")
    public boolean isCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Guess{" +
                "clientGuess=" + payload +
                ", isCorrect=" + isCorrect +
                ", createdAt=" + createdAt +
                '}';
    }
}
