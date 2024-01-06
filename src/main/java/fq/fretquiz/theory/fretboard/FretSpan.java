package fq.fretquiz.theory.fretboard;

import jakarta.persistence.Embeddable;

@Embeddable
public record FretSpan(int startFret,
                       int endFret,
                       int size) {

    public FretSpan(int startFret, int endFret) {
        this(startFret, endFret, endFret-startFret);
    }
}
