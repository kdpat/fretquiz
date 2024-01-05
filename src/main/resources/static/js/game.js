import {html, React, ReactDOM} from "./deps.js";
import {makeStompClient} from "./websocket.js";
import {Fretboard, JoinGameButton, Players, Staff, StartRoundButton} from "./components/game_components.js";

// location.pathname will look like "/game/cCR"
const pathParts = location.pathname.split("/");
const gameId = pathParts[2];

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

  const players = game?.players || [];
  const player = findPlayer(players, playerId);

  const isStartStatus = game?.status === "INIT" || game?.status === "ROUND_OVER";
  const canJoinGame = playerId == null && isStartStatus;

  const userIsHost = player && (player.user.id === game?.host.id);
  const canStartRound = userIsHost && isStartStatus;

  const guess = round && playerId && findPlayerGuess(round.guesses, playerId);
  const playerCanGuess = round && playerId && guess == null;

  const dots = dotsToDraw(guess, round?.correctFretCoords);

  React.useEffect(() => {
    wsRef.current = makeGameSocket(props.gameId, {setGame, setPlayerId});
    return () => wsRef.current?.deactivate();
  }, []);

  return html`
    <div className="GameComponent">
      <div>
        <h2>Game ${props.gameId}</h2>
        <p>Status: ${game?.status}</p>
        <${Staff} notes=${notes}/>

          <${Fretboard}
              ws=${wsRef.current}
              gameId=${props.gameId}
              playerId=${playerId}
              dots=${dots}
              clickable=${playerCanGuess}
          />

          ${canStartRound
          && html`<${StartRoundButton} ws=${wsRef.current}
                                       gameId=${props.gameId}
                                       status=${game?.status}/>`}
      </div>

      <div className="game-info">
        <${Players} players=${players}/>

        ${canJoinGame
        && html`<${JoinGameButton} ws=${wsRef.current}
                                   gameId=${props.gameId}/>`}
      </div>
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
}

function onGameMessage(resp, {setGame, setPlayerId}) {
  const message = JSON.parse(resp.body);
  console.log("resp", message);

  switch (message.type) {
    case "GAME_NOT_FOUND":
      console.error("game not found");
      break;
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
    case "PLAYER_JOINED":
      setGame(message.game);
      break;
    case "NO_UPDATE":
      break;
    default:
      throw new Error(`unknown game message type: ${message.type}`);
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

function findPlayer(players, playerId) {
  return players.find(player => player.id === playerId);
}

function findPlayerGuess(guesses, playerId) {
  return guesses.find(guess => guess.payload.playerId === playerId);
}
