package fq.fretquiz.theory.fretboard;

import fq.fretquiz.theory.music.Note;

import java.util.*;

public record Fretboard(List<Note> openStrings,
                        FretSpan fretSpan,
                        Map<FretCoord, Note> fretCoordNotes) {

    public static final List<Note> STANDARD_GUITAR_STRINGS =
            List.of(
                    Note.from("E/5"),
                    Note.from("B/4"),
                    Note.from("G/4"),
                    Note.from("D/4"),
                    Note.from("A/3"),
                    Note.from("E/3")
            );

    public static Fretboard create(List<Note> openStrings, FretSpan fretSpan) {
        Map<FretCoord, Note> notes = calculateNotes(openStrings, fretSpan);
        return new Fretboard(openStrings, fretSpan, notes);
    }

    public static Map<FretCoord, Note> calculateNotes(List<Note> openStrings, FretSpan fretSpan) {
        var notes = new HashMap<FretCoord, Note>();
        int stringCount = openStrings.size();

        for (int string = 0; string < stringCount; string++) {
            for (int fret = fretSpan.startFret(); fret <= fretSpan.endFret(); fret++) {
                var fretCoord = new FretCoord(string + 1, fret);
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
        return Optional.of(note);
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
        Note low = openStrings.getLast();
        Note high = openStrings.getFirst().transpose(fretCount());
        return Note.randomBetween(low, high);
    }

    public List<Note> notesOnString(int string) {
        if (string < 1 || string > stringCount() + 1) {
            throw new IllegalArgumentException();
        }
        var notes = new ArrayList<Note>();

        for (int fret = fretSpan.startFret(); fret <= fretSpan.endFret(); fret++) {
            var fretCoord = new FretCoord(string, fret);
            Note note = findNote(fretCoord).orElseThrow();
            notes.add(note);
        }
        return notes;
    }
}
