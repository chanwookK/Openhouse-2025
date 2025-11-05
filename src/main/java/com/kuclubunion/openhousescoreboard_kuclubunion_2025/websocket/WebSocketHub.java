package com.kuclubunion.openhousescoreboard_kuclubunion_2025.websocket;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.AllBoardDataRes;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketHub {

  private final SimpMessagingTemplate simpMessagingTemplate;

  public void broadcastLeaderboard(AllBoardDataRes data) {
    simpMessagingTemplate.convertAndSend("/topic/leaderboard", data);
  }
}
