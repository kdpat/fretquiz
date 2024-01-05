package fq.fretquiz.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Objects;

@Component
public class WsSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    private static final Logger log = LoggerFactory.getLogger(WsSubscribeListener.class);

    @Override
    public void onApplicationEvent(@NonNull SessionSubscribeEvent event) {
        var headers = event.getMessage().getHeaders();
        var destination = (String) Objects.requireNonNull(headers.get("simpDestination"));
        var parts = destination.split("/");

        // handle subs to /user/topic/game/{gameId}
        if (destination.startsWith("/user") && parts.length == 5) {
            var gameId = Objects.requireNonNull(parts[4]);
            var user = Objects.requireNonNull(event.getUser());
            log.info("{} subbed to game {}", user, gameId);
        }
    }
}
