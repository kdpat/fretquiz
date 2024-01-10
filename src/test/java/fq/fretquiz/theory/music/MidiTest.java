package fq.fretquiz.theory.music;

import fq.fretquiz.theory.fretboard.Fretboard;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MidiTest {

    Logger log = LoggerFactory.getLogger(MidiTest.class);

    @Test
    void naturalAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, Midi.naturalAt(i));
        }
    }

    @Test
    void sharpsAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, Midi.sharpAt(i));
        }
    }

    @Test
    void flatAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, Midi.flatAt(i));
        }
    }

    @Test
    void doubleFlatAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, Midi.doubleFlatAt(i));
        }
    }

    @Test
    void doubleSharpAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, Midi.doubleSharpAt(i));
        }
    }

    @Test
    void notesAt() {
        for (int i = Midi.MIDI_LOW; i <= Midi.MIDI_HIGH; i++) {
            log.info("{}: {}", i, Midi.notesAt(i));
        }
    }

    @Test
    void midiStringNums() {
        for (Note openString : Fretboard.STANDARD_GUITAR_STRINGS) {
            log.info("{}: {}", openString, openString.midiNum());
        }
    }

    @Test
    void findNotes() {
        for (int i = Midi.MIDI_LOW; i <= Midi.MIDI_HIGH; i++) {
            log.info("{}: {}", i, Midi.findNoteSharps(i));
            log.info("{}: {}\n", i, Midi.findNoteFlats(i));
        }
    }
}
