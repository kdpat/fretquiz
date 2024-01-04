import {html, React, ReactDOM} from "./deps.js";
import {makeStompClient} from "./websocket.js";
import {Fretboard, Players, Staff, StartRoundButton} from "./components/game_components.js";

const gameId = parseGameIdFromPath(location.pathname);

if (gameId == null) {
  throw new Error("could not extract game id from path");
}

ReactDOM.render(
  html`<${GameComponent} gameId=${gameId}/>`,
  document.getElementById("game")
);

// fretboard dot colors
const CORRECT_COLOR = "limegreen";
const INCORRECT_COLOR = "tomato";

function GameComponent(props) {
  const wsRef = React.useRef(null);

  const [game, setGame] = React.useState(null);
  const [playerId, setPlayerId] = React.useState(null);

  const round = game?.currentRound;
  const noteToGuess = round?.noteToGuess;

  // wrap note in a list, so we can pass it to the Staff component
  const notes = noteToGuess ? [noteToGuess] : [];

  const player = game && playerId && findPlayer(game, playerId);
  const canGuess = round && playerId && playerCanGuess(round, playerId);

  const userIsHost = player?.userId === game?.hostId;
  const isStartStatus = game?.status === "INIT" || game?.status === "ROUND_OVER";
  const canStartRound = userIsHost && isStartStatus;

  const guess = round && playerId && findPlayerGuess(round.guesses, playerId);
  const dots = dotsToDraw(guess, round?.correctFretCoords);

  React.useEffect(() => {
    wsRef.current = makeGameSocket(props.gameId, {setGame, setPlayerId});
    return () => wsRef.current?.deactivate();
  }, [])

  return html`
    <div>
      <h2>Game ${props.gameId}</h2>
      <p>Status: ${game?.status}</p>
      <${Players} players=${game?.players || []} />
      <${Staff} notes=${notes}/>

      <${Fretboard}
              ws=${wsRef.current}
              gameId=${props.gameId}
              playerId=${playerId}
              dots=${dots}
              clickable=${canGuess}
      />

      ${canStartRound 
        && html`<${StartRoundButton} ws=${wsRef.current} 
                                     gameId=${props.gameId}
                                     status=${game?.status}/>`}
    </div>
  `;
}

function makeGameSocket(gameId, setters) {
  const socket = makeStompClient();
  socket.onConnect = () => onStompConnect(socket, gameId, setters);
  socket.activate();
  return socket;
}

function onStompConnect(client, gameId, setters) {
  console.log("stomp client connected", client);

  const onMessage = resp => onGameMessage(resp, setters);
  client.subscribe(`/topic/game/${gameId}`, onMessage);
  client.subscribe(`/user/topic/game/${gameId}`, onMessage);

  client.publish({destination: `/app/game/${gameId}`});
}

function onGameMessage(resp, {setGame, setPlayerId}) {
  const message = JSON.parse(resp.body);
  console.log("resp", message);

  switch (message.type) {
    case "FOUND_GAME":
      setGame(message.game);
      setPlayerId(message.playerId);
      break;
    case "ROUND_STARTED":
      setGame(message.game);
      break;
    case "GUESS_RESULT":
      setGame(message.game);
      break;
    // default:
    //   throw new Error(`unknown game message type: ${message.type}`);
  }
}

function dotsToDraw(guess, correctFretCoords) {
  const dots = [];

  if (guess) {
    // draw the user's guess if they were wrong
    if (!guess.isCorrect) {
      const guessDot = {...guess.payload.fretCoord, color: INCORRECT_COLOR};
      dots.push(guessDot);
    }

    // draw the correct frets
    for (const fretCoord of correctFretCoords) {
      const correctDot = {...fretCoord, color: CORRECT_COLOR};
      dots.push(correctDot);
    }
  }

  return dots;
}

function parseGameIdFromPath(pathname) {
  const parts = pathname.split("/");
  return parts[2];
}

function findPlayer(game, playerId) {
  return game.players.find(player => player.id === playerId);
}

function playerCanGuess(round, playerId) {
  return !round.guesses.some(guess => guess.payload.playerId === playerId);
}

function findPlayerGuess(guesses, playerId) {
  return guesses.find(guess => guess.payload.playerId === playerId);
}
