package com.kuclubunion.openhousescoreboard_kuclubunion_2025.websocket;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.global.AllowedCrossOrigins;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final AllowedCrossOrigins allowedCrossOrigins;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // 클라이언트가 구독할 수 있는 prefix
    config.enableSimpleBroker("/topic");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws/leaderboard")
        .setAllowedOrigins(allowedCrossOrigins.getOrigins().toArray(new String[0]))
        .withSockJS();

    registry.addEndpoint("/ws/history")
        .setAllowedOrigins(allowedCrossOrigins.getOrigins().toArray(new String[0]))
        .withSockJS();
  }
}
