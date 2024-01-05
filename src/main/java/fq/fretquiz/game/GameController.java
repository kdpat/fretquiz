package fq.fretquiz.game;

import fq.fretquiz.App;
import fq.fretquiz.game.model.Game;
import fq.fretquiz.game.model.GameUpdate;
import fq.fretquiz.game.model.Guess;
import fq.fretquiz.user.User;
import fq.fretquiz.user.UserService;
import fq.fretquiz.websocket.WsPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
        User user = userService.fetchUserFromRequest(request).orElseThrow();
        Game game = gameService.createWithHost(user);
        log.info("game created: {}", game);

        String encodedId = App.encodeId(game.id());
        return "redirect:/game/" + encodedId;
    }

    @MessageMapping("/game/{encodedGameId}/join")
    @SendTo("/topic/game/{encodedGameId}")
    public GameMessage joinGame(@DestinationVariable String encodedGameId, WsPrincipal principal) {
        Long gameId = App.decodeId(encodedGameId);
        Game game = gameService.findGame(gameId).orElseThrow();
        User user = userService.findUser(principal.id()).orElseThrow();
        GameUpdate update = gameService.addPlayer(game, user);
        return GameMessage.from(update);
    }

    @MessageMapping("/game/{encodedGameId}/start")
    @SendTo("/topic/game/{encodedGameId}")
    public GameMessage startRound(@DestinationVariable String encodedGameId,
                                  WsPrincipal principal) {
        Long gameId = App.decodeId(encodedGameId);
        User user = userService.findUser(principal.id()).orElseThrow();

        return gameService.findGame(gameId)
                .map(game -> gameService.startNewRound(game, user))
                .map(GameMessage::from)
                .orElseThrow();
    }

    @MessageMapping("/game/{encodedGameId}/guess")
    @SendTo("/topic/game/{encodedGameId}")
    public GameMessage handleGuess(@DestinationVariable String encodedGameId,
                                   Guess.Payload payload) {
        Long gameId = App.decodeId(encodedGameId);

        return gameService.findGame(gameId)
                .map(game -> gameService.handleGuess(game, payload))
                .map(GameMessage::from)
                .orElseThrow();
    }
}
