package fq.fretquiz.theory.fretboard;

import fq.fretquiz.App;
import fq.fretquiz.theory.music.Midi;
import fq.fretquiz.theory.music.Note;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MidiFretboard implements Fretboard {

    private final List<Integer> openStrings; // midi numbers
    private final FretSpan fretSpan;

    public static final List<Integer> STANDARD_GUITAR_STRINGS = List.of(
            76,  // E5
            71,  // B4
            67,  // G4
            62,  // D4
            57,  // A3
            52); // E3

    public MidiFretboard(List<Integer> openStrings, FretSpan fretSpan) {
        this.openStrings = openStrings;
        this.fretSpan = fretSpan;
    }

    public static MidiFretboard STANDARD = new MidiFretboard(STANDARD_GUITAR_STRINGS, new FretSpan(0, 4));

    public Optional<Note> findNote(FretCoord fretCoord) {
        int stringMidi = openStrings.get(fretCoord.string() - 1);
        int fretMidi = stringMidi + fretCoord.fret();
        Note note = Midi.findNoteSharps(fretMidi);
        return Optional.of(note);
    }

    public int midiKeyAt(FretCoord fretCoord) {
        int openString = openStrings.get(fretCoord.string()-1);
        return openString + fretCoord.fret();
    }

    public List<FretCoord> findFretCoords(Note note) {
        int midiNum = note.midiNum();
        List<FretCoord> fretCoords = new ArrayList<>();

        for (int string = 1; string <= stringCount(); string++) {
            int stringMidi = openStrings.get(string-1);
            for (int fret = fretSpan.startFret(); fret <= fretSpan.endFret(); fret++) {
                int fretCoordMidi = stringMidi + fret;
                if (midiNum == fretCoordMidi) {
                    var fretCoord = new FretCoord(string, fret);
                    fretCoords.add(fretCoord);
                }
            }
        }
        return Collections.unmodifiableList(fretCoords);
    }

    public Note randomNote() {
        int midiLow = openStrings.getLast();
        int midiHigh = openStrings.getFirst() + fretCount();

        Random random = ThreadLocalRandom.current();
        int midiKey = random.nextInt(midiLow, midiHigh + 1);
        List<Note> notes = Midi.notesAt(midiKey);

        return App.randomElem(random, notes);
    }

    public int fretCount() {
        return fretSpan.size();
    }

    public int stringCount() {
        return openStrings.size();
    }

    @Override
    public String toString() {
        return "MidiFretboard{" +
                "openStrings=" + openStrings +
                ", fretSpan=" + fretSpan +
                '}';
    }
}
