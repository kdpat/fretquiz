package fq.fretquiz.websocket;

import java.security.Principal;

public record WsPrincipal(Long id) implements Principal {

    @Override
    public String getName() {
        return id.toString();
    }
}
