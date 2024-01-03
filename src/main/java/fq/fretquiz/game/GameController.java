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

@Controller
public class GameController {

    private final UserService userService;
    private final GameService gameService;

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    public GameController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
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

        return "redirect:/game/" + game.id();
    }

    @MessageMapping("/game/{gameId}")
    @SendToUser("/topic/game/{gameId}")
    public GameMessage fetchGame(@DestinationVariable Long gameId, UserPrincipal principal) {
        var game = gameService.findGame(gameId).orElse(null);

        if (game == null) {
            return new GameMessage.GameNotFound();
        }

        var playerId = game.findPlayerByUserId(principal.id())
                .map(Player::id)
                .orElse(null);

        return new GameMessage.FoundGame(game, playerId);
    }

    @MessageMapping("/game/{gameId}/start")
    @SendTo("/topic/game/{gameId}")
    public GameMessage startRound(@DestinationVariable Long gameId, UserPrincipal principal) {
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

    @MessageMapping("/game/{gameId}/guess")
    @SendTo("/topic/game/{gameId}")
    public GameMessage handleGuess(@DestinationVariable Long gameId, Guess.Payload payload) {
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
