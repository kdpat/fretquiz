package fq.fretquiz.theory.music;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MidiNoteMapperTest {

    Logger log = LoggerFactory.getLogger(MidiNoteMapperTest.class);

    @Test
    void naturalAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, MidiNoteMapper.naturalAt(i));
        }
    }

    @Test
    void sharpsAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, MidiNoteMapper.sharpsAt(i));
        }
    }

    @Test
    void flatAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, MidiNoteMapper.flatAt(i));
        }
    }

    @Test
    void doubleFlatAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, MidiNoteMapper.doubleFlatAt(i));
        }
    }

    @Test
    void doubleSharpAt() {
        for (int i = 21; i <= 60; i++) {
            log.info("{}: {}", i, MidiNoteMapper.doubleSharpAt(i));
        }
    }
}