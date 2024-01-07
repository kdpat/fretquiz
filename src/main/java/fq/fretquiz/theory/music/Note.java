package fq.fretquiz.theory.music;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Embeddable;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fq.fretquiz.App.randomElem;

@Embeddable
@JsonSerialize(using = Note.Serializer.class)
public record Note(WhiteKey whiteKey,
                   Accidental accidental,
                   Octave octave) {
    /**
     * Matches a note name like "C4", "Gbb6", or "E#2".
     */
    public static final Pattern regexPattern = Pattern.compile("([A-Z])(#{1,2}|b{1,2})?/(\\d)");

    /**
     * Parses a note name like "Cb4" into a note record, like Note[whiteKey=C, accidental=FLAT, octave=FOUR]
     *
     * @param name a string consisting of a capital letter A-G, an optional accidental ('b' or '#'), and an octave number
     * @return a Note
     */
    public static Note fromString(String name) {
        Matcher matcher = regexPattern.matcher(name);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Note name must be in the form Cbb/4");
        }

        // for the note 'E##3': matcher.group(1) == 'E', matcher.group(2) == '##', matcher.group(3) == '3'
        var whiteKey = WhiteKey.valueOf(matcher.group(1));

        // find the accidental, if it exists
        String match2 = matcher.group(2);

        // if there was no accidental, set it to be an empty string instead of null so it can be parsed correctly
        var accidental = Accidental.from(match2 == null ? "" : match2);
        var octave = Octave.from(matcher.group(3));

        return new Note(whiteKey, accidental, octave);
    }

    public static Note random() {
        WhiteKey whiteKey = randomElem(List.of(WhiteKey.values()));
        Accidental accidental = randomElem(List.of(Accidental.values()));
        Octave octave = randomElem(List.of(Octave.values()));
        return new Note(whiteKey, accidental, octave);
    }

    public static Note randomBetween(Note low, Note high) {
        int lowMidi = low.midiNum();
        int highMidi = high.midiNum();

        Note note;
        int midi;

        do {
            note = Note.random();
            midi = note.midiNum();
        } while (midi < lowMidi || midi > highMidi);

        return note;
    }

    /**
     * A number representing the pitch (without the octave) as the number of half steps from 'C'.
     * e.g. C -> 0, C# -> 1, etc.
     */
    public int pitchClass() {
        return whiteKey.halfStepsFromC() + accidental.halfStepOffset();
    }

    /**
     * The note's midi number. C4 is 60, C#4 is 61, etc.
     */
    public int midiNum() {
        return pitchClass() + (12 * (octave.value + 1));
    }

    /**
     * Returns true if `this` is enharmonic with the given note.
     * E##4, F#4, & Gb4 would all be considered enharmonic.
     */
    public boolean isEnharmonicWith(Note note) {
        return midiNum() == note.midiNum();
    }

    /**
     * Returns a note a half step higher.
     */
    public Note next() {
        // If we're at pitchClass == 11 (the notes "B", "A##", "Cb"), increment the octave.
        // Otherwise, the octave stays the same.
        Octave oct = pitchClass() == 11 ? octave.next() : octave;
        WhiteKey key = whiteKey;
        Accidental acc = accidental;

        if (accidental == Accidental.NONE) {
            if (whiteKey == WhiteKey.B || whiteKey == WhiteKey.E) {
                key = whiteKey.next();
            } else {
                acc = Accidental.SHARP;
            }
        } else if (accidental == Accidental.SHARP) {
            key = whiteKey.next();
            acc = (whiteKey == WhiteKey.B || whiteKey == WhiteKey.E)
                    ? Accidental.SHARP
                    : Accidental.NONE;
        } else if (accidental == Accidental.FLAT) {
            acc = Accidental.NONE;
        }

        return new Note(key, acc, oct);
    }

    /**
     * Returns a note that is the given number of half-steps higher.
     *
     * @param halfSteps must be a positive number
     */
    public Note transpose(int halfSteps) {
        if (halfSteps < 0) {
            throw new IllegalArgumentException("halfSteps must be a positive number");
        }
        var note = new Note(whiteKey, accidental, octave);

        while (halfSteps > 0) {
            note = note.next();
            halfSteps--;
        }
        return note;
    }

    public String name() {
        return whiteKey.value + accidental.value + "/" + octave.value;
    }

    public static class Serializer extends JsonSerializer<Note> {

        @Override
        public void serialize(Note note, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(note.name());
        }
    }
}
