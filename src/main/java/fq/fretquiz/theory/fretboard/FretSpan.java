package fq.fretquiz.theory.fretboard;

import jakarta.persistence.Embeddable;

@Embeddable
public record FretSpan(int startFret,
                       int endFret) {

    public int size() {
        return endFret - startFret;
    }
}
