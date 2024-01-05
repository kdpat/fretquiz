package fq.fretquiz.websocket;

import fq.fretquiz.App;
import fq.fretquiz.game.GameMessage;
import fq.fretquiz.game.GameService;
import fq.fretquiz.game.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Objects;

@Component
public class WsSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    private static final Logger log = LoggerFactory.getLogger(WsSubscribeListener.class);

    public WsSubscribeListener(SimpMessagingTemplate messagingTemplate,
                               GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    @Override
    public void onApplicationEvent(@NonNull SessionSubscribeEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        String destination = (String) Objects.requireNonNull(headers.get("simpDestination"));
        String[] parts = destination.split("/");

        // handle subs to /user/topic/game/{gameId}
        if (destination.startsWith("/user/topic/game") && parts.length == 5) {
            String encodedGameId = Objects.requireNonNull(parts[4]);
            Long gameId = App.decodeId(encodedGameId);

            Principal principal = Objects.requireNonNull(event.getUser());
            log.info("{} subbed to game {}", principal, gameId);

            String responseDest = "/topic/game/" + encodedGameId;

            gameService.findGame(gameId)
                    .ifPresentOrElse(game -> {
                        Long userId = Long.valueOf(principal.getName());

                        Long playerId = game.findPlayerByUserId(userId)
                                .map(Player::id)
                                .orElse(null);

                        messagingTemplate.convertAndSendToUser(
                                principal.getName(),
                                responseDest,
                                new GameMessage.FoundGame(game, playerId)
                        );
                    }, () -> {
                        messagingTemplate.convertAndSendToUser(
                                principal.getName(),
                                responseDest,
                                GameMessage.GAME_NOT_FOUND);
                    });
        }
    }
}
