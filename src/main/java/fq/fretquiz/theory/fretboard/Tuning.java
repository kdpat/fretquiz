package fq.fretquiz.theory.fretboard;

import fq.fretquiz.theory.music.Note;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

import java.util.List;

/**
 * This class represents the tuning of a stringed instrument.
 * It contains a list of notes that each string is tuned to
 * starting from the highest-pitched string (string 1) to the lowest.
 */
@Embeddable
public final class Tuning {

    @ElementCollection
    private List<Note> notes;

    public static final Tuning STANDARD_GUITAR = new Tuning(
            List.of(
                    Note.from("E/5"),
                    Note.from("B/4"),
                    Note.from("G/4"),
                    Note.from("D/4"),
                    Note.from("A/3"),
                    Note.from("E/3")
            ));

    public Tuning() {
    }

    public Tuning(List<Note> notes) {
        this.notes = notes;
    }

    public int stringCount() {
        return notes.size();
    }

    public List<Note> notes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuning tuning = (Tuning) o;

        return notes.equals(tuning.notes);
    }

    @Override
    public int hashCode() {
        return notes.hashCode();
    }

    @Override
    public String toString() {
        return "Tuning{" +
                "notes=" + notes +
                '}';
    }
}
