package fq.fretquiz.theory.fretboard;

import fq.fretquiz.App;
import fq.fretquiz.theory.music.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public record NoteFretboard(List<Note> openStrings,
                            FretSpan fretSpan,
                            Map<FretCoord, Note> fretCoordNotes) implements Fretboard {

    public static final List<Note> STANDARD_GUITAR_STRINGS =
            List.of(
                    new Note(WhiteKey.E, Accidental.NONE, Octave.FIVE),
                    new Note(WhiteKey.B, Accidental.NONE, Octave.FOUR),
                    new Note(WhiteKey.G, Accidental.NONE, Octave.FOUR),
                    new Note(WhiteKey.D, Accidental.NONE, Octave.FOUR),
                    new Note(WhiteKey.A, Accidental.NONE, Octave.THREE),
                    new Note(WhiteKey.E, Accidental.NONE, Octave.THREE)
            );

    public static NoteFretboard create(List<Note> openStrings, FretSpan fretSpan) {
        Map<FretCoord, Note> notes = calculateNotes(openStrings, fretSpan);
        return new NoteFretboard(openStrings, fretSpan, notes);
    }

    public static Map<FretCoord, Note> calculateNotes(List<Note> openStrings, FretSpan fretSpan) {
        Map<FretCoord, Note> notes = new HashMap<>();
        int stringCount = openStrings.size();

        for (int string = 0; string < stringCount; string++) {
            for (int fret = fretSpan.startFret(); fret <= fretSpan.endFret(); fret++) {
                var fretCoord = new FretCoord(string+1, fret);
                Note openString = openStrings.get(string);
                Note transposed = openString.transpose(fret);
                notes.put(fretCoord, transposed);
            }
        }
        return notes;
    }

    /**
     * @return the Note at the given Fretboard.Coord (string & fret)
     */
    public Optional<Note> findNote(FretCoord coord) {
        Note note = fretCoordNotes.get(coord);
        return Optional.ofNullable(note);
    }

    /**
     * @return the Fretboard.Coords where a given Note can be played.
     */
    public List<FretCoord> findFretCoords(Note note) {
        return fretCoordNotes.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isEnharmonicWith(note))
                .map(Map.Entry::getKey)
                .toList();
    }

    public int fretCount() {
        return fretSpan.size();
    }

    public int stringCount() {
        return openStrings().size();
    }

    /**
     * @return a random note on that can be played on the fretboard.
     */
    public Note randomNote() {
        int midiLow = openStrings.getLast().midiNum();
        int midiHigh = openStrings.getFirst().transpose(fretCount()).midiNum();

        Random random = ThreadLocalRandom.current();
        int midiKey = random.nextInt(midiLow, midiHigh + 1);
        List<Note> notes = Midi.notesAt(midiKey);

        return App.randomElem(random, notes);
    }

    public List<Note> notesOnString(int string) {
        if (string < 1 || string > stringCount() + 1) {
            throw new IllegalArgumentException();
        }

        List<Note> notes = new ArrayList<>();

        for (int fret = fretSpan.startFret(); fret <= fretSpan.endFret(); fret++) {
            var fretCoord = new FretCoord(string, fret);
            Note note = findNote(fretCoord).orElseThrow();
            notes.add(note);
        }
        return notes;
    }
}
