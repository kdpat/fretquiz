package fq.fretquiz.game;

import fq.fretquiz.auth.Auth;
import fq.fretquiz.game.model.GameUpdate;
import fq.fretquiz.game.model.Guess;
import fq.fretquiz.game.model.Player;
import fq.fretquiz.game.model.Settings;
import fq.fretquiz.user.UserService;
import fq.fretquiz.websocket.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.sqids.Sqids;

import java.util.List;

@Controller
public class GameController {

    private final UserService userService;
    private final GameService gameService;
    private final Sqids sqids;

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    public GameController(UserService userService, GameService gameService, Sqids sqids) {
        this.userService = userService;
        this.gameService = gameService;
        this.sqids = sqids;
    }

    @GetMapping(value = "/game/**")
    public String showGame() {
        return "game";
    }

    @PostMapping("/game")
    public String handleCreateGame(HttpServletRequest request) {
        var user = Auth.findUserIdToken(request.getCookies())
                .flatMap(Auth::decodeUserIdToken)
                .flatMap(userService::findUser)
                .orElseThrow();

        var settings = Settings.createDefault();
        var game = gameService.createGame(user, settings);
        log.info("game created: {}", game);

        var encodedId = sqids.encode(List.of(game.id()));
        return "redirect:/game/" + encodedId;
    }

    @MessageMapping("/game/{encodedGameId}")
    @SendToUser("/topic/game/{encodedGameId}")
    public GameMessage fetchGame(@DestinationVariable String encodedGameId,
                                 UserPrincipal principal) {
        var gameId = sqids.decode(encodedGameId).getFirst();
        var game = gameService.findGame(gameId).orElse(null);

        if (game == null) {
            return new GameMessage.GameNotFound();
        }

        var playerId = game.findPlayerByUserId(principal.id())
                .map(Player::id)
                .orElse(null);

        return new GameMessage.FoundGame(game, playerId);
    }

    @MessageMapping("/game/{encodedGameId}/start")
    @SendTo("/topic/game/{encodedGameId}")
    public GameMessage startRound(@DestinationVariable String encodedGameId,
                                  UserPrincipal principal) {
        var gameId = sqids.decode(encodedGameId).getFirst();
        var user = principal.toUser();

        var gameUpdate = gameService.findGame(gameId)
                .map(game -> gameService.startNewRound(game, user))
                .orElseThrow();

        return switch (gameUpdate) {
            case GameUpdate.None(String reason) -> new GameMessage.UpdateRejected(reason);
            case GameUpdate.RoundStarted(var game, var round) -> new GameMessage.RoundStarted(game, round);
            default -> throw new IllegalStateException("Unexpected value: " + gameUpdate);
        };
    }

    @MessageMapping("/game/{encodedGameId}/guess")
    @SendTo("/topic/game/{encodedGameId}")
    public GameMessage handleGuess(@DestinationVariable String encodedGameId,
                                   Guess.Payload payload) {
        var gameId = sqids.decode(encodedGameId).getFirst();

        var gameUpdate = gameService.findGame(gameId)
                .map(game -> gameService.handleGuess(game, payload))
                .orElseThrow();

        return switch (gameUpdate) {
            case GameUpdate.GuessHandled(var game, var guess) -> new GameMessage.GuessHandled(game, guess);
            case GameUpdate.None(String reason) -> new GameMessage.UpdateRejected(reason);
            default -> throw new IllegalStateException("Unexpected value: " + gameUpdate);
        };
    }
}
