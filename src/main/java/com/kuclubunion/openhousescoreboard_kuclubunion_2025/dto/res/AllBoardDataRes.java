package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AllBoardDataRes {

  private Type type;
  private List<ClubLeaderboardRes> clubLeaderboardRes;
  private List<TeamLeaderboardRes> teamLeaderboardRes;
  private List<ScoreHistoryRes> scoreHistoryRes;
  private CompetitionMetaRes competitionMetaRes;

  public enum Type {
    LEADERBOARD, META
  }
}
