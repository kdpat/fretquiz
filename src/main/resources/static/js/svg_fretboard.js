const DEFAULT_OPTS = {
  width: 200, // px
  height: 300, // px
  startFret: 0,
  endFret: 4,
  stringNames: ["E", "B", "G", "D", "A", "E"],
  dots: [],
  dotColor: "white",
  hoverDotColor: "white",
  showFretNums: true,
  showStringNames: true,
  drawDotOnHover: false,
  onClick: null,
  onHover: null,
}

export class SvgFretboard {
  constructor(parentEl, opts = {}) {
    this.parentEl = parentEl;
    this.opts = {...DEFAULT_OPTS, ...opts};

    this.xMargin = this.opts.width / this.numStrings;
    this.yMargin = this.opts.height / 8;
    this.neckWidth = this.opts.width - (this.xMargin * 2);
    this.neckHeight = this.opts.height - (this.yMargin * 2);
    this.fretHeight = this.neckHeight / this.numFrets;
    this.stringMargin = this.neckWidth / (this.numStrings - 1);
    this.dotRadius = this.fretHeight / 6;
    this.fretNumOffset = this.neckWidth / 6;

    this.hoverDot = null;
    this.hoverCoord = null;

    this.svg = makeSvgElement(this.opts.width, this.opts.height);
    this.dots = [];

    this.addStrings();
    this.addFrets();
    this.addDots(this.opts.dots);

    if (this.opts.onClick) {
      this.svg.onclick = this.onClick.bind(this);
    }

    if (this.opts.drawDotOnHover) {
      this.svg.onmousemove = this.onMouseMove.bind(this);
    }

    this.svg.onmouseleave = () => {
      this.removeHoverDot();
    }

    if (this.opts.showFretNums) {
      this.drawFretNums();
    }

    if (this.opts.showStringNames) {
      this.drawStringNames();
    }

    this.parentEl.appendChild(this.svg);
  }


  addStrings() {
    for (let i = 0; i < this.numStrings; i++) {
      const x = (i * this.stringMargin) + this.xMargin;
      const y1 = this.yMargin;
      const y2 = this.yMargin + this.neckHeight;
      const line = makeLine(x, y1, x, y2);
      this.svg.appendChild(line);
    }
  }

  addFrets() {
    for (let i = 0; i <= this.numFrets; i++) {
      const x1 = this.xMargin;
      const x2 = this.opts.width - this.xMargin;
      const y = (i * this.fretHeight) + this.yMargin;
      const line = makeLine(x1, y, x2, y);
      this.svg.appendChild(line);
    }
  }

  addDots(dots) {
    dots.forEach(dot => this.addDot(dot));
  }

  addDot(dot) {
    const elem = this.makeDotElem(dot.string, dot.fret, dot.color);
    this.dots.push(elem);
    this.svg.appendChild(elem)
  }

  makeDotElem(string, fret, color) {
    const coord = this.fretCoord(string, fret);

    let radius = this.dotRadius;
    if (fret === 0) {
      radius -= radius / 4;
    }

    return makeCircle(coord.x, coord.y, radius, color);
  }

  remove() {
    this.svg.remove();
  }

  removeDots() {
    this.removeHoverDot();

    for (const elem of this.dots) {
      elem.remove();
    }
    this.dots = [];
  }

  removeHoverDot() {
    if (this.hoverDot) {
      this.hoverDot.remove();
      this.hoverDot = null;
    }
  }

  drawFretNums() {
    const fontSize = 16; // TODO: adjust this for different diagram sizes

    for (let fret = this.opts.startFret; fret <= this.opts.endFret; fret++) {
      const point = this.fretCoord(this.numStrings, fret);
      const x = point.x - this.fretNumOffset;
      const y = point.y + (this.fretHeight * 0.1);
      const textEl = makeText(x, y, fret.toString(), fontSize);
      textEl.setAttribute("pointer-events", "none");
      this.svg.appendChild(textEl);
    }
  }

  drawStringNames() {
    const fontSize = 16;
    const fret = this.numFrets + 1;

    this.opts.stringNames.forEach((name, i) => {
      const {x, y} = this.fretCoord(i+1, fret);
      const textEl = makeText(x, y, name, fontSize);
      textEl.setAttribute("pointer-events", "none");
      this.svg.appendChild(textEl);
    });
  }

  get numStrings() {
    return this.opts.stringNames.length;
  }

  get numFrets() {
    // don't count the open string (fret 0) as a fret
    const offset = this.opts.startFret === 0 ? 0 : 1;
    return this.opts.endFret - this.opts.startFret + offset;
  }

  makeClickable(onClick) {
    this.opts.drawDotOnHover = true;
    this.svg.onmousemove = this.onMouseMove.bind(this);

    this.opts.onClick = onClick.bind(this);
    this.svg.onclick = this.onClick.bind(this);
  }

  makeNotClickable() {
    this.opts.drawDotOnHover = false;
    this.opts.onClick = null;
    this.svg.onmousemove = null;
    this.svg.onclick = null;
  }

  onClick(event) {
    const coord = this.closestFretCoord(event) ;
    this.opts.onClick(coord);
  }

  onMouseMove(event) {
    const coord = this.closestFretCoord(event);
    // return if we're on the same fret as the previous mouse event
    if (this.coordEqual(coord, this.hoverCoord)) return;

    this.hoverCoord = coord;

    if (this.opts.onHover) {
      this.opts.onHover(this.hoverCoord);
    }

    if (this.opts.drawDotOnHover) {
      if (this.hoverDot) this.hoverDot.remove();

      const dot = this.makeDotElem(coord.string, coord.fret, this.opts.hoverDotColor);
      this.hoverDot = dot;

      this.svg.appendChild(dot);
    }
  }

  fretCoord(string, fret) {
    const stringOffset = Math.abs(string - this.numStrings);

    const x = (stringOffset * this.stringMargin) + this.xMargin;
    let y = ((fret * this.fretHeight) - (this.fretHeight / 2)) + this.yMargin;

    // place open string dots closer to the top of the fretboard
    if (fret === 0) {
      y += this.fretHeight / 5;
    }

    return {x, y};
  }

  closestFretCoord(mouseEvent) {
    const point = cursorPoint(this.svg, mouseEvent);
    const x = point.x - this.xMargin;
    const y = point.y - this.yMargin + (this.fretHeight / 2);

    let string = Math.abs(Math.round(x / this.stringMargin) - this.numStrings);
    if (string < 1) {
      string = 1;
    } else if (string > this.numStrings) {
      string = this.numStrings;
    }

    let fret = Math.round(y / this.fretHeight);
    if (fret > this.opts.endFret) {
      fret = this.opts.endFret;
    }

    return {string, fret};
  }

  coordEqual(c1, c2) {
    return c1?.string === c2?.string && c1?.fret === c2?.fret;
  }
}

/* svg utils */

const SVG_NS = 'http://www.w3.org/2000/svg';

function makeSvgElement(width, height) {
  const elem = document.createElementNS(SVG_NS, 'svg');
  elem.setAttribute('width', width.toString());
  elem.setAttribute('height', height.toString());
  elem.setAttribute('viewBox', `0 0 ${width} ${height}`);
  return elem;
}

function makeLine(x1, y1, x2, y2, color = 'black') {
  const line = document.createElementNS(SVG_NS, 'line');
  line.setAttribute('x1', x1.toString());
  line.setAttribute('y1', y1.toString());
  line.setAttribute('x2', x2.toString());
  line.setAttribute('y2', y2.toString());
  line.setAttribute('stroke', color);
  return line;
}

function makeCircle(cx, cy, r, color = 'white') {
  const circle = document.createElementNS(SVG_NS, 'circle');
  circle.setAttribute('cx', cx.toString());
  circle.setAttribute('cy', cy.toString());
  circle.setAttribute('r', r.toString());
  circle.setAttribute('stroke', 'black');
  circle.setAttribute('fill', color);
  return circle;
}

function makeText(x, y, text, fontSize = 16) {
  const textEl = document.createElementNS(SVG_NS, 'text');
  textEl.setAttribute('x', x.toString());
  textEl.setAttribute('y', y.toString());
  textEl.setAttribute('text-anchor', 'middle');
  textEl.setAttribute('font-size', fontSize.toString());

  const textNode = document.createTextNode(text);
  textEl.appendChild(textNode);

  return textEl;
}

/*
 * Returns the {x, y} coord of the clicked point relative to `svgElem`.
 */
function cursorPoint(svgElem, mouseEvent) {
  const point = svgElem.createSVGPoint();
  point.x = mouseEvent.clientX;
  point.y = mouseEvent.clientY;

  const screenCTM = svgElem.getScreenCTM();
  if (!screenCTM) throw new Error(`could not get the screen ctm of ${svgElem}`);

  const matrix = screenCTM.inverse();
  return point.matrixTransform(matrix);
}
