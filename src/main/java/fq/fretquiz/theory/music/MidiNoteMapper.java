package fq.fretquiz.theory.music;

import java.util.Optional;

public class MidiNoteMapper {

    private static final WhiteKey[] NATURAL_NOTES = {
            WhiteKey.A, // 0
            null,       // 1 (A# / Bb)
            WhiteKey.B, // 2
            WhiteKey.C, // 3
            null,       // 4 (C# / Db)
            WhiteKey.D, // 5
            null,       // 6 (D# / Eb)
            WhiteKey.E, // 7
            WhiteKey.F, // 8
            null,       // 9 (F# / Gb)
            WhiteKey.G, // 10
            null        // 11 (G# / Ab)
    };

    public static Optional<Note> naturalAt(int midiKey) {
        if (midiKey < 21 || midiKey > 108) {
            throw new IllegalArgumentException("MIDI key must be between 21 and 108");
        }

        int offset = midiKey - 21;
        int octaveValue = (midiKey >= 24) ? ((offset + 9) / 12) : 0;
        int positionInOctave = offset % 12;

        WhiteKey whiteKey = NATURAL_NOTES[positionInOctave];

        if (whiteKey != null) {
            return Optional.of(new Note(whiteKey, Accidental.NONE, Octave.values()[octaveValue]));
        }

        return Optional.empty();
    }

    private static final WhiteKey[] SHARP_NOTES = {
            null,       // 0
            WhiteKey.A, // 1 (A#)
            null,       // 2
            WhiteKey.B, // 3 (B#)
            WhiteKey.C, // 4 (C#)
            null,       // 5
            WhiteKey.D, // 6 (D#)
            null,       // 7
            WhiteKey.E, // 8 (E#)
            WhiteKey.F, // 9 (F#)
            null,       // 10
            WhiteKey.G, // 11 (G#)
    };

    public static Optional<Note> sharpsAt(int midiKey) {
        if (midiKey < 21 || midiKey > 108) {
            throw new IllegalArgumentException("MIDI key must be between 21 and 108");
        }

        int offset = midiKey - 21;
        // Adjust the octave calculation to change at C# (MIDI key 25)
        int octaveValue = (midiKey >= 25) ? ((offset + 8) / 12) : 0;
        int positionInOctave = offset % 12;

        WhiteKey whiteKey = SHARP_NOTES[positionInOctave];

        if (whiteKey != null) {
            return Optional.of(new Note(whiteKey, Accidental.SHARP, Octave.values()[octaveValue]));
        }

        return Optional.empty();
    }

    private static final WhiteKey[] FLAT_NOTES = {
            null,        // 0
            WhiteKey.B,  // 1 (Bb)
            WhiteKey.C,  // 2 (Cb)
            null,        // 3
            WhiteKey.D,  // 4 (Db)
            null,        // 5
            WhiteKey.E,  // 6 (Eb)
            WhiteKey.F,  // 7 (Fb)
            null,        // 8
            WhiteKey.G,  // 9 (Gb)
            null,        // 10
            WhiteKey.A,  // 11 (Ab)
    };

    public static Optional<Note> flatAt(int midiKey) {
        if (midiKey < 21 || midiKey > 108) {
            throw new IllegalArgumentException("MIDI key must be between 21 and 108");
        }

        int offset = midiKey - 21;
        // Adjust the octave calculation to change at Cb (MIDI key 23)
        int octaveValue = (midiKey >= 23) ? ((offset + 10) / 12) : 0;
        int positionInOctave = offset % 12;

        WhiteKey whiteKey = FLAT_NOTES[positionInOctave];

        if (whiteKey != null) {
            return Optional.of(new Note(whiteKey, Accidental.FLAT, Octave.values()[octaveValue]));
        }

        return Optional.empty();
    }

    private static final WhiteKey[] DOUBLE_FLAT_NOTES = {
            WhiteKey.B,  // 0 (Bbb)
            WhiteKey.C,  // 1 (Cbb)
            null,        // 2
            WhiteKey.D,  // 3 (Dbb)
            null,        // 4
            WhiteKey.E,  // 5 (Ebb)
            WhiteKey.F,  // 6 (Fbb)
            null,        // 7
            WhiteKey.G,  // 8 (Gbb)
            null,        // 9
            WhiteKey.A,  // 10 (Abb)
            null,        // 11
    };

    public static Optional<Note> doubleFlatAt(int midiKey) {
        if (midiKey < 21 || midiKey > 108) {
            throw new IllegalArgumentException("MIDI key must be between 21 and 108");
        }

        int offset = midiKey - 21;
        int octaveValue = (midiKey >= 22) ? ((offset + 11) / 12) : 0;
        int positionInOctave = offset % 12;

        WhiteKey whiteKey = DOUBLE_FLAT_NOTES[positionInOctave];

        if (whiteKey != null) {
            return Optional.of(new Note(whiteKey, Accidental.DOUBLE_FLAT, Octave.values()[octaveValue]));
        }

        return Optional.empty();
    }

    private static final WhiteKey[] DOUBLE_SHARP_NOTES = {
            WhiteKey.G,  // 0 (G##)
            null,        // 1
            WhiteKey.A,  // 2 (A##)
            null,        // 3
            WhiteKey.B,  // 4 (B##)
            WhiteKey.C,  // 5 (C##)
            null,        // 6
            WhiteKey.D,  // 7 (D##)
            null,        // 8
            WhiteKey.E,  // 9 (E##)
            WhiteKey.F,  // 10 (F##)
            null,        // 11
    };

    public static Optional<Note> doubleSharpAt(int midiKey) {
        if (midiKey < 21 || midiKey > 108) {
            throw new IllegalArgumentException("MIDI key must be between 21 and 108");
        }

        int offset = midiKey - 21;
        int octaveValue = (midiKey >= 26) ? ((offset + 7) / 12) : 0;
        int positionInOctave = offset % 12;

        WhiteKey whiteKey = DOUBLE_SHARP_NOTES[positionInOctave];

        if (whiteKey != null) {
            return Optional.of(new Note(whiteKey, Accidental.DOUBLE_SHARP, Octave.values()[octaveValue]));
        }

        return Optional.empty();
    }
}
