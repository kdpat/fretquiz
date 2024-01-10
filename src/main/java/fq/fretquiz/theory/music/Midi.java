package fq.fretquiz.theory.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Midi {

    public static final int MIDI_LOW = 21; // A0
    public static final int MIDI_HIGH = 108; // C8

    // these note arrays list where each accidental can be found in the octave starting from A0 (MIDI_LOW)
    // DOUBLE_FLAT_NOTES starts with "B" because A0 is enharmonic with Bbb0
    private static final WhiteKey[] DOUBLE_FLAT_NOTES =
            {WhiteKey.B, WhiteKey.C, null, WhiteKey.D, null, WhiteKey.E, WhiteKey.F, null, WhiteKey.G, null, WhiteKey.A, null,};

    private static final WhiteKey[] FLAT_NOTES =
            {null, WhiteKey.B, WhiteKey.C, null, WhiteKey.D, null, WhiteKey.E, WhiteKey.F, null, WhiteKey.G, null, WhiteKey.A,};

    private static final WhiteKey[] NATURAL_NOTES =
            {WhiteKey.A, null, WhiteKey.B, WhiteKey.C, null, WhiteKey.D, null, WhiteKey.E, WhiteKey.F, null, WhiteKey.G, null,};

    private static final WhiteKey[] SHARP_NOTES =
            {null, WhiteKey.A, null, WhiteKey.B, WhiteKey.C, null, WhiteKey.D, null, WhiteKey.E, WhiteKey.F, null, WhiteKey.G,};

    private static final WhiteKey[] DOUBLE_SHARP_NOTES =
            {WhiteKey.G, null, WhiteKey.A, null, WhiteKey.B, WhiteKey.C, null, WhiteKey.D, null, WhiteKey.E, WhiteKey.F, null,};

    private static WhiteKey[] accidentalNotes(Accidental acc) {
        return switch (acc) {
            case DOUBLE_FLAT -> DOUBLE_FLAT_NOTES;
            case FLAT -> FLAT_NOTES;
            case NONE -> NATURAL_NOTES;
            case SHARP -> SHARP_NOTES;
            case DOUBLE_SHARP -> DOUBLE_SHARP_NOTES;
        };
    }

    /**
     * @param offset the distance from MIDI_LOW
     */
    private static Octave calculateOctave(int midiKey, int offset, Accidental acc) {
        int val = switch (acc) {
            case DOUBLE_FLAT -> midiKey >= 22 ? (offset + 11) / 12 : 0;
            case FLAT -> midiKey >= 23 ? (offset + 10) / 12 : 0;
            case NONE -> midiKey >= 24 ? (midiKey - 12) / 12 : 0;
            case SHARP -> midiKey >= 25 ? (offset + 8) / 12 : 0;
            case DOUBLE_SHARP -> midiKey >= 26 ? (offset + 7) / 12 : 0;
        };
        return Octave.values()[val];
    }

    public static Optional<Note> noteWithAccidental(int midiKey, Accidental acc) {
        if (midiKey < MIDI_LOW || midiKey > MIDI_HIGH) {
            throw new IllegalArgumentException("midi key out of range");
        }

        int midiLowOffset = midiKey - MIDI_LOW;
        int octaveOffset = midiLowOffset % 12;

        WhiteKey whiteKey = accidentalNotes(acc)[octaveOffset];
        if (whiteKey == null) return Optional.empty();

        Octave octave = calculateOctave(midiKey, midiLowOffset, acc);
        Note note = new Note(whiteKey, acc, octave);
        return Optional.of(note);
    }

    public static List<Note> notesAt(int midiKey) {
        List<Note> notes = new ArrayList<>();

        for (var acc : Accidental.values()) {
            noteWithAccidental(midiKey, acc)
                    .ifPresent(notes::add);
        }

        return Collections.unmodifiableList(notes);
    }

    public static Note findNoteSharps(int midiKey) {
        return naturalAt(midiKey)
                .orElseGet(() -> sharpAt(midiKey)
                        .orElseThrow());
    }

    public static Note findNoteFlats(int midiKey) {
        return naturalAt(midiKey)
                .orElseGet(() -> flatAt(midiKey)
                        .orElseThrow());
    }

    public static Optional<Note> naturalAt(int midiKey) {
        return noteWithAccidental(midiKey, Accidental.NONE);
    }

    public static Optional<Note> sharpAt(int midiKey) {
        return noteWithAccidental(midiKey, Accidental.SHARP);
    }

    public static Optional<Note> flatAt(int midiKey) {
        return noteWithAccidental(midiKey, Accidental.FLAT);
    }

    public static Optional<Note> doubleFlatAt(int midiKey) {
        return noteWithAccidental(midiKey, Accidental.DOUBLE_FLAT);
    }

    public static Optional<Note> doubleSharpAt(int midiKey) {
        return noteWithAccidental(midiKey, Accidental.DOUBLE_SHARP);
    }
}
