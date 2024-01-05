package fq.fretquiz.websocket;

import fq.fretquiz.auth.Auth;
import fq.fretquiz.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WsConfig implements WebSocketMessageBrokerConfigurer {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(WsConfig.class);

    public WsConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/queue", "/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor())
                .setHandshakeHandler(handshakeHandler());
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new HandshakeInterceptor() {

            @Override
            public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                           @NonNull ServerHttpResponse response,
                                           @NonNull WebSocketHandler handler,
                                           @NonNull Map<String, Object> attrs) {
                if (request instanceof ServletServerHttpRequest servletReq) {
                    var cookies = servletReq.getServletRequest().getCookies();
                    var userToken = Auth.findUserIdToken(cookies).orElseThrow();
                    attrs.put("userToken", userToken);
                    return true;
                }
                return false;
            }

            @Override
            public void afterHandshake(@NonNull ServerHttpRequest request,
                                       @NonNull ServerHttpResponse response,
                                       @NonNull WebSocketHandler handler,
                                       Exception e) {
            }
        };
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new DefaultHandshakeHandler() {

            @Override
            protected Principal determineUser(@NonNull ServerHttpRequest request,
                                              @NonNull WebSocketHandler handler,
                                              @NonNull Map<String, Object> attrs) {
                var userToken = (String) attrs.get("userToken");
                var userId = Auth.decodeUserIdToken(userToken).orElseThrow();
                var user = userService.findUser(userId).orElseThrow();
                return new WsPrincipal(user.id());
            }
        };
    }
}
