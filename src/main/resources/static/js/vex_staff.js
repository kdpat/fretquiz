const VF = Vex.Flow;

export class VexStaff {
  constructor(parentEl, width, height) {
    this.width = width;
    this.height = height;

    this.renderer = new VF.Renderer(parentEl, VF.Renderer.Backends.SVG);
    this.renderer.resize(width, height);
    this.context = this.renderer.getContext();

    this.stave = new VF.Stave(0, 0, width-1)
      .setContext(this.context)
      .addClef("treble")
      .draw();
  }

  addNotes(noteNames, duration = "w") {
    const staveNote = new VF.StaveNote({
      keys: noteNames,
      duration,
      align_center: true,
    });

    const voice = new VF.Voice({num_beats: 4, beat_value: 4});
    voice.addTickables([staveNote]);

    const voices = [voice];
    VF.Accidental.applyAccidentals(voices);

    new VF.Formatter()
      .joinVoices(voices)
      .format(voices, this.width * 0.75);

    if (this.noteGroup) {
      this.clear();
    }

    this.noteGroup = this.context.openGroup();
    voice.draw(this.context, this.stave);
    this.context.closeGroup();
  }

  clear() {
    if (this.noteGroup) {
      this.context.svg.removeChild(this.noteGroup);
      this.noteGroup = null;
    }
  }

  remove() {
    this.context.svg.remove();
  }
}

// addNote(note, duration = "w") {
//   const staveNote = new VF.StaveNote({
//     keys: [note],
//     duration,
//     // align_center: true,
//   });
//
//   const accidental = parseAccidental(note);
//
//   if (accidental) {
//     staveNote.addModifier(new VF.Accidental(accidental));
//   }
//
//   if (this.noteGroup) {
//     this.clear();
//   }
//
//   this.noteGroup = this.context.openGroup();
//   VF.Formatter.FormatAndDraw(this.context, this.stave, [staveNote]);
//   this.context.closeGroup();
// }

// /**
//  * Matches a white key and accidental e.g. "C", "F#", "Dbb", "En".
//  */
// const NOTE_ACC_REGEX = /[A-G](#{1,2}|b{1,2}|n)?/;
//
// /**
//  * parseAccidental("C/4") === undefined;
//  * parseAccidental("C##/4") === "##";
//  * parseAccidental("Dn/5")=== "n";
//  */
// function parseAccidental(noteName) {
//   const [_, accidental] = noteName.match(NOTE_ACC_REGEX);
//   return accidental;
// }
