package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.Club;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.ClubToClubManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ScoreboardRes {
  private String defenseClubName;
  private UUID defenseClubId;
  private int defenseClubVisit;
  private int defenseClubScore;

  private List<ScoreboardClubRes> scoreboardClubRes;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  public static class ScoreboardClubRes {
    private UUID id;
    private String name;
    private int score;
    private int visit;
    private LocalDateTime lockedUntil;

    public static ScoreboardClubRes by(ClubToClubManager c) {
      Club attackClub = c.getAttackClub();
      return ScoreboardClubRes.builder()
          .id(attackClub.getId())
          .name(attackClub.getName())
          .score(attackClub.getScore())
          .visit(attackClub.getVisit())
          .lockedUntil(c.getLockedUntil())
          .build();
    }
  }
}
