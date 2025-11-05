package com.kuclubunion.openhousescoreboard_kuclubunion_2025.service;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.AllBoardDataRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.AllBoardDataRes.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

  private final LeaderboardService leaderboardService;
  private final HistoryService historyService;

  public AllBoardDataRes getAllBoardData(AllBoardDataRes.Type type) {
    if (type.equals(Type.LEADERBOARD)) {
      return AllBoardDataRes.builder()
          .type(Type.LEADERBOARD)
          .clubLeaderboardRes(leaderboardService.getClubLeaderboard())
          .teamLeaderboardRes(leaderboardService.getTeamLeaderboard())
          .scoreHistoryRes(historyService.getAllHistory())
          .build();
    } else {
      return AllBoardDataRes.builder()
          .type(Type.META)
          .competitionMetaRes(leaderboardService.getCompetitionMeta())
          .build();
    }
  }
}
