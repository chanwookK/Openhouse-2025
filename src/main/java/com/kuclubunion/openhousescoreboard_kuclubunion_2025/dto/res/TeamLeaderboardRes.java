package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TeamLeaderboardRes {
  private String name;
  private int score;

  public static TeamLeaderboardRes from(Team t) {
    return TeamLeaderboardRes.builder()
        .name(t.getName())
        .score(t.getScore())
        .build();
  }
}
