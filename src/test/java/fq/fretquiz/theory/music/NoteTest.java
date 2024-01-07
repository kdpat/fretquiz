package fq.fretquiz.theory.music;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    Logger log = LoggerFactory.getLogger(NoteTest.class);

//    @Test
//    void noteFromMidi() {
//        var c4 = Note.fromString("C/4");
//        log.info("C4: {}", c4);
//
//        var bSharp3 = Note.fromMidiNum(60, Accidental.SHARP);
//        log.info("B#3: {}", bSharp3);
//        assertTrue(c4.isEnharmonicWith(bSharp3));
//
//        var dDoubleFlat4 = Note.fromMidiNum(60, Accidental.DOUBLE_FLAT);
//        log.info("{}", dDoubleFlat4);
//        assertTrue(c4.isEnharmonicWith(dDoubleFlat4));
//    }
}