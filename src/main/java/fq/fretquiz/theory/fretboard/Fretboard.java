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
        var notes = calculateNotes(openStrings, fretSpan);
        return new Fretboard(openStrings, fretSpan, notes);
    }

    public static Map<FretCoord, Note> calculateNotes(List<Note> openStrings, FretSpan fretSpan) {
        var notes = new HashMap<FretCoord, Note>();
        var stringCount = openStrings.size();

        for (int string = 0; string < stringCount; string++) {
            for (int fret = fretSpan.startFret(); fret <= fretSpan.endFret(); fret++) {
                var fretCoord = new FretCoord(string + 1, fret);
                var openStringNote = openStrings.get(string);
                var note = openStringNote.transpose(fret);
                notes.put(fretCoord, note);
            }
        }

        return notes;
    }

    /**
     * @return the Note at the given Fretboard.Coord (string & fret)
     */
    public Optional<Note> findNote(FretCoord coord) {
        var note = fretCoordNotes.get(coord);
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
        var lowNote = openStrings.getLast();
        var highNote = openStrings.getFirst().transpose(fretCount());

        return Note.randomBetween(lowNote, highNote);
    }

    public List<Note> notesOnString(int string) {
        if (string < 1 || string > stringCount() + 1) {
            throw new IllegalArgumentException();
        }

        var notes = new ArrayList<Note>();

        for (var fret = fretSpan.startFret(); fret <= fretSpan.endFret(); fret++) {
            var fretCoord = new FretCoord(string, fret);
            var note = findNote(fretCoord).orElseThrow();
            notes.add(note);
        }

        return notes;
    }
}
