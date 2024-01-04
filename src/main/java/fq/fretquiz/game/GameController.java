package fq.fretquiz.game;

import fq.fretquiz.App;
import fq.fretquiz.auth.Auth;
import fq.fretquiz.game.model.GameUpdate;
import fq.fretquiz.game.model.Guess;
import fq.fretquiz.game.model.Player;
import fq.fretquiz.user.User;
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

import java.util.Optional;

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
        var user = userService.fetchUserFromRequest(request).orElseThrow();
        var game = gameService.create(user);
        log.info("game created: {}", game);

        var encodedId = App.encodeId(game.id());
        return "redirect:/game/" + encodedId;
    }

    @MessageMapping("/game/{encodedGameId}")
    @SendToUser("/topic/game/{encodedGameId}")
    public GameMessage fetchGame(@DestinationVariable String encodedGameId,
                                 UserPrincipal principal) {
        var gameId = App.decodeId(encodedGameId);
        var game = gameService.findGame(gameId).orElse(null);

        if (game == null) {
            return GameMessage.GAME_NOT_FOUND;
        }

        var playerId = game.findPlayerByUserId(principal.id())
                .map(Player::id)
                .orElse(null);

        return new GameMessage.FoundGame(game, playerId);
    }

    @MessageMapping("/game/{encodedGameId}/join")
    @SendTo("/topic/game/{encodedGameId}")
    public GameMessage joinGame(String encodedGameId, UserPrincipal principal) {
        var gameId = App.decodeId(encodedGameId);
        var game = gameService.findGame(gameId).orElseThrow();
        var user = userService.findUser(principal.id()).orElseThrow();
        var gameUpdate = gameService.addPlayer(game, user);
        return GameMessage.from(gameUpdate);
    }

    @MessageMapping("/game/{encodedGameId}/start")
    @SendTo("/topic/game/{encodedGameId}")
    public GameMessage startRound(@DestinationVariable String encodedGameId,
                                  UserPrincipal principal) {
        var gameId = App.decodeId(encodedGameId);
        var user = principal.toUser();

        return gameService.findGame(gameId)
                .map(game -> gameService.startNewRound(game, user))
                .map(GameMessage::from)
                .orElseThrow();
    }

    @MessageMapping("/game/{encodedGameId}/guess")
    @SendTo("/topic/game/{encodedGameId}")
    public GameMessage handleGuess(@DestinationVariable String encodedGameId,
                                   Guess.Payload payload) {
        var gameId = App.decodeId(encodedGameId);

        return gameService.findGame(gameId)
                .map(game -> gameService.handleGuess(game, payload))
                .map(GameMessage::from)
                .orElseThrow();
    }
}
