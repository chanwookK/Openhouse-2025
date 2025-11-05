package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.Club;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ClubLeaderboardRes {
  private String name;
  private int score;

  public static ClubLeaderboardRes from(Club c) {
    return ClubLeaderboardRes.builder()
        .name(c.getName())
        .score(c.getScore())
        .build();
  }
}
