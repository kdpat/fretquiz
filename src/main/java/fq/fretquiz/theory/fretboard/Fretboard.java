package fq.fretquiz.theory.fretboard;

import fq.fretquiz.theory.music.Note;

import java.util.List;
import java.util.Optional;

public interface Fretboard {

    int fretCount();

    int stringCount();

    Optional<Note> findNote(FretCoord fretCoord);

    List<FretCoord> findFretCoords(Note note);

    Note randomNote();
}
