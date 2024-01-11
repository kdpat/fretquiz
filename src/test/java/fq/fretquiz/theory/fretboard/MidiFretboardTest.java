package fq.fretquiz.theory.fretboard;

import fq.fretquiz.theory.music.Accidental;
import fq.fretquiz.theory.music.Note;
import fq.fretquiz.theory.music.Octave;
import fq.fretquiz.theory.music.WhiteKey;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MidiFretboardTest {

    static Logger log = LoggerFactory.getLogger(MidiFretboard.class);

    MidiFretboard standard = new MidiFretboard(
            MidiFretboard.STANDARD_GUITAR_STRINGS,
            new FretSpan(0, 4));

    Note C4 = new Note(WhiteKey.C, Accidental.NONE, Octave.FOUR);

    @Test
    void findNote() {
        var fretCoord = new FretCoord(2, 1);
        var note = standard.findNote(fretCoord).orElseThrow();
        assertEquals(note, new Note(WhiteKey.C, Accidental.NONE, Octave.FIVE));
    }

    @Test
    void findFretCoord() {
        var fretCoords = standard.findFretCoords(C4);
        assertEquals(List.of(new FretCoord(5, 3)), fretCoords);

        fretCoords = standard.findFretCoords(Note.fromString("B/2"));
        log.info("B/2: {}", fretCoords);
    }

    @Test
    void randomNote() {
        var fretboard = new MidiFretboard(List.of(62), new FretSpan(0, 2));
        for (int i = 0; i < 50; i++) {
            log.info("{}: {}", i, fretboard.randomNote());
        }
    }

    @Test
    void midiKeyAt() {
        var fretCoord = new FretCoord(2, 1);
        assertEquals(72, standard.midiKeyAt(fretCoord));
        log.info("{}: {}", fretCoord, standard.midiKeyAt(fretCoord));
    }
}