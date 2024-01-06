package fq.fretquiz.game.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fq.fretquiz.theory.fretboard.FretCoord;
import fq.fretquiz.theory.music.Note;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static fq.fretquiz.App.nowMillis;

@Entity
public class Round {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Note noteToGuess;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<FretCoord> correctFretCoords;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Guess> guesses;

    private Instant createdAt;

    public static Round create(Settings settings) {
        Note noteToGuess = settings.fretboard().randomNote();
        List<FretCoord> correctFretCoords = settings.fretboard().findFretCoords(noteToGuess);

        var round = new Round();
        round.noteToGuess = noteToGuess;
        round.correctFretCoords = new ArrayList<>(correctFretCoords);
        round.guesses = new ArrayList<>();
        round.createdAt = nowMillis();
        return round;
    }

    public void addGuess(Guess guess) {
        var guesses = new ArrayList<>(this.guesses);
        guesses.add(guess);
        this.guesses = guesses;
    }

    public boolean playerHasGuessed(Long playerId) {
        return guesses.stream()
                .anyMatch(guess -> guess.payload().playerId().equals(playerId));
    }

    public int guessCount() {
        return guesses.size();
    }

    public Long id() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty
    public Note noteToGuess() {
        return noteToGuess;
    }

    public void setNoteToGuess(Note noteToGuess) {
        this.noteToGuess = noteToGuess;
    }

    @JsonProperty
    public List<FretCoord> correctFretCoords() {
        return correctFretCoords;
    }

    public void setCorrectFretCoords(List<FretCoord> correctFretCoords) {
        this.correctFretCoords = correctFretCoords;
    }

    @JsonProperty
    public List<Guess> guesses() {
        return guesses;
    }

    public void setGuesses(List<Guess> guesses) {
        this.guesses = guesses;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Round{" +
                "id=" + id +
                ", noteToGuess=" + noteToGuess +
                ", correctFretCoords=" + correctFretCoords +
                ", guesses=" + guesses +
                ", createdAt=" + createdAt +
                '}';
    }
}
