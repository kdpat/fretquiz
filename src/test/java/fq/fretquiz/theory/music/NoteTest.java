package fq.fretquiz.theory.music;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    static final Logger log = LoggerFactory.getLogger(NoteTest.class);
    static final Note C4 = new Note(WhiteKey.C, Accidental.NONE, Octave.FOUR);

    @Test
    void makeNote() {
        log.info("c4 name: {}", C4.name());
        assertEquals(C4, Note.fromString("C/4"));
        assertEquals("C/4", C4.name());
    }
}
