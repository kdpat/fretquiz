package fq.fretquiz.theory.fretboard;

import jakarta.persistence.Embeddable;

@Embeddable
public record FretCoord(int string,
                        int fret) {
}
