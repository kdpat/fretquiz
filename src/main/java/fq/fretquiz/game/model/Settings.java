package fq.fretquiz.game.model;

import fq.fretquiz.theory.fretboard.FretSpan;
import fq.fretquiz.theory.fretboard.Fretboard;
import fq.fretquiz.theory.music.Accidental;
import fq.fretquiz.theory.music.Note;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
public class Settings {

    private int roundCount;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> stringsToUse;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Accidental> accidentalsToUse;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Note> openStringNotes;

    @Embedded
    private FretSpan fretSpan;

    @Transient
    private Fretboard fretboard;

    public static Settings createDefault() {
        var stringsToUse = Set.of(1, 2, 3, 4, 5, 6);
        var accidentalsToUse = Set.of(Accidental.values());

        var settings = new Settings();
        settings.roundCount = 4;
        settings.stringsToUse = new HashSet<>(stringsToUse);
        settings.accidentalsToUse = new HashSet<>(accidentalsToUse);
        settings.openStringNotes = new ArrayList<>(Fretboard.STANDARD_GUITAR_STRINGS);
        settings.fretSpan = new FretSpan(0, 4);

        return settings;
    }

    public Fretboard fretboard() {
        if (fretboard == null) {
            fretboard = Fretboard.create(openStringNotes, fretSpan);
        }
        return fretboard;
    }

    public int roundCount() {
        return roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }

    public FretSpan fretSpan() {
        return fretSpan;
    }

    public void setFretSpan(FretSpan fretSpan) {
        this.fretSpan = fretSpan;
    }

    public Set<Integer> stringsToUse() {
        return stringsToUse;
    }

    public void setStringsToUse(Set<Integer> stringsToUse) {
        this.stringsToUse = stringsToUse;
    }

    public Set<Accidental> accidentalsToUse() {
        return accidentalsToUse;
    }

    public void setAccidentalsToUse(Set<Accidental> accidentalsToUse) {
        this.accidentalsToUse = accidentalsToUse;
    }

    public List<Note> openStringNotes() {
        return openStringNotes;
    }

    public void setOpenStringNotes(List<Note> openStringNotes) {
        this.openStringNotes = openStringNotes;
    }

    @Override
    public String toString() {
        return "Settings[" +
                "roundCount=" + roundCount + ", " +
                "stringsToUse=" + stringsToUse + ", " +
                "accidentalsToUse=" + accidentalsToUse + ", " +
                "tuning=" + openStringNotes + ", " +
                "fretSpan=" + fretSpan + ']';
    }
}
