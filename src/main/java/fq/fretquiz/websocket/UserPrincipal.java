package fq.fretquiz.websocket;

import fq.fretquiz.user.User;

import java.security.Principal;
import java.time.Instant;

public record UserPrincipal(Long id,
                            String name,
                            Instant createdAt) implements Principal {

    public static UserPrincipal from(User user) {
        return new UserPrincipal(user.id(), user.name(), user.createdAt());
    }

    public User toUser() {
        var user = new User();
        user.setId(id);
        user.setName(name);
        user.setCreatedAt(createdAt);
        return user;
    }

    @Override
    public String getName() {
        return name;
    }
}
