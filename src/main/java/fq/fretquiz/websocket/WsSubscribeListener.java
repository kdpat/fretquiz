package fq.fretquiz.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Objects;

@Component
public class WsSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    private static final Logger log = LoggerFactory.getLogger(WsSubscribeListener.class);

    @Override
    public void onApplicationEvent(@NonNull SessionSubscribeEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        String destination = (String) Objects.requireNonNull(headers.get("simpDestination"));
        String[] parts = destination.split("/");

        // handle subs to /user/topic/game/{gameId}
        if (destination.startsWith("/user") && parts.length == 5) {
            String gameId = Objects.requireNonNull(parts[4]);
            Principal user = Objects.requireNonNull(event.getUser());
            log.info("{} subbed to game {}", user, gameId);
        }
    }
}
