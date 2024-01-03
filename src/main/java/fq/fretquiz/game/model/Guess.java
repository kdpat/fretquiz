package fq.fretquiz.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fq.fretquiz.theory.fretboard.FretCoord;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

import java.time.Instant;

import static fq.fretquiz.App.nowMillis;

@Embeddable
public class Guess {

    @Embedded
    private Payload payload;

    private boolean isCorrect;
    private Instant createdAt;

    public static Guess create(Payload payload, boolean isCorrect) {
        var guess = new Guess();
        guess.payload = payload;
        guess.isCorrect = isCorrect;
        guess.createdAt = nowMillis();
        return guess;
    }

    @JsonProperty
    public Payload payload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @JsonProperty
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

    @Embeddable
    public record Payload(Long playerId,
                          FretCoord fretCoord) {
    }
}
