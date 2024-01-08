package fq.fretquiz.theory.music;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WhiteKeyTest {

    @Test
    void previous() {
        assertEquals(WhiteKey.D.previous(), WhiteKey.C);
        assertEquals(WhiteKey.C.previous(), WhiteKey.B);
    }
}