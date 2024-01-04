import {React, html} from "../deps.js";
import {VexStaff} from "./vex_staff.js";
import {SvgFretboard} from "./svg_fretboard.js";

export function StartRoundButton(props) {
  const onClick = props.ws?.connected
    ? sendStartRound(props.ws, props.gameId)
    : null;

  const text = props.status === "INIT"
    ? "Start Game"
    : "Next Round";

  return html`
      <button class="StartRoundButton" onClick=${onClick}>${text}</button>
  `;
}

function sendStartRound(ws, gameId) {
  return () => ws.publish({destination: `/app/game/${gameId}/start`});
}

export function Staff(props) {
  const width = 200;
  const height = 130;

  const divRef = React.useRef(null);
  const svgRef = React.useRef(null);

  // create svg
  React.useEffect(() => {
    svgRef.current = new VexStaff(divRef.current, width, height);
    return () => svgRef.current.remove();
  }, []);

  // draw notes
  React.useEffect(() => {
    if (props.notes.length) {
      svgRef.current.addNotes(props.notes);
    }
    return () => svgRef.current.clear();
  }, [props.notes]);

  return html`<div ref=${divRef}/>`;
}

export function Fretboard(props) {
  const divRef = React.useRef(null); // the parent div
  const svgRef = React.useRef(null); // the svg fretboard

  const onClick = onFretboardClick(props.ws, props.gameId, props.playerId);
  const opts = props.clickable ? clickableOpts(onClick) : {};

  // create svg
  React.useEffect(() => {
    svgRef.current = new SvgFretboard(divRef.current, opts);
    return () => svgRef.current.remove();
  }, [opts]);

  // make clickable
  React.useEffect(() => {
    if (props.clickable) {
      svgRef.current.makeClickable(onClick);
    }
    return () => svgRef.current.makeNotClickable();
  }, [props.clickable]);

  // draw dots
  React.useEffect(() => {
    if (props.dots?.length) {
      svgRef.current.addDots(props.dots);
    }
    return () => svgRef.current.removeDots();
  }, [props.dots]);

  return html`<div ref=${divRef}/>`;
}

function clickableOpts(onClick) {
  return {onClick, drawDotOnHover: true};
}

function onFretboardClick(ws, gameId, playerId) {
  return fretCoord => {
    const destination = `/app/game/${gameId}/guess`;
    const body = JSON.stringify({fretCoord, playerId});
    ws.publish({destination, body});
  }
}

export function Players(props) {
  return html`
    <div>
      <h4>Players</h4>
      <ul>
        ${props.players.map(player => html`
          <li key=${player.id}>
            <${Player} player=${player} />
          </li>
        `)}
      </ul>
    </div>
  `;
}

export function Player(props) {
  return html`
    <div>Player: ${props.player.user.name}(${props.player.id}): ${props.player.score} pts</div>
  `;
}

function StringCheckbox(props) {
  const name = `string-${props.num}`;

  return html`
      <label>
          <input type="checkbox" name=${name}/>
          ${props.num}
      </label>
  `;
}
