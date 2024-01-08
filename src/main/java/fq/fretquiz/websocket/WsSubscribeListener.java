package fq.fretquiz.websocket;

import fq.fretquiz.IdCodec;
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

import java.util.Objects;

@Component
public class WsSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    public static final String USER_GAME_TOPIC = "^/user/topic/game/.+";
    private static final Logger log = LoggerFactory.getLogger(WsSubscribeListener.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    public WsSubscribeListener(SimpMessagingTemplate messagingTemplate,
                               GameService gameService) {
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService;
    }

    @Override
    public void onApplicationEvent(@NonNull SessionSubscribeEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        String simpDest = (String) headers.get("simpDestination");
        Objects.requireNonNull(simpDest);

        if (simpDest.matches(USER_GAME_TOPIC)) {
            String[] parts = simpDest.split("/");
            String encodedGameId = Objects.requireNonNull(parts[4]);
            Long gameId = IdCodec.decodeId(encodedGameId);

            WsPrincipal principal = (WsPrincipal) event.getUser();
            Objects.requireNonNull(principal);
            log.info("{} subbed to game {}", principal, gameId);

            GameMessage message = gameService.findGame(gameId)
                    .<GameMessage>map(game -> {
                        Long playerId = game.findPlayerByUserId(principal.id())
                                .map(Player::id)
                                .orElse(null);

                        return new GameMessage.FoundGame(game, playerId);
                    })
                    .orElse(GameMessage.GAME_NOT_FOUND);

            String respDest = "/topic/game/" + encodedGameId;
            messagingTemplate.convertAndSendToUser(principal.getName(), respDest, message);
        }
    }
}
