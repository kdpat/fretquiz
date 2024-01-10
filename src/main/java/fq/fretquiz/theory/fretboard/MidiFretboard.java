package fq.fretquiz.theory.fretboard;

import fq.fretquiz.theory.music.Note;

import java.util.List;
import java.util.Optional;

public class MidiFretboard {

    private final List<Integer> stringMidiNums;
    private final FretSpan fretSpan;

    public static final List<Integer> STANDARD_GUITAR_STRINGS_MIDI_NUMS = List.of(76, 71, 67, 62, 57, 52);

    public MidiFretboard(List<Integer> stringMidiNums, FretSpan fretSpan) {
        this.stringMidiNums = stringMidiNums;
        this.fretSpan = fretSpan;
    }

    public Optional<Note> findNote(FretCoord fretCoord) {
        return Optional.empty();
    }

    public List<FretCoord> findFretCoords(Note note) {
        return List.of();
    }

    public Note randomNote() {
        return null;
    }
}
