package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.Competition;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompetitionMetaRes {
  private String title;
  private String announcement;
  private int totalTime;
  private LocalDateTime startTime;
  private Boolean visibility;
  private Integer lockTime;

  public static CompetitionMetaRes from(Competition c) {
    return CompetitionMetaRes.builder()
        .title(c.getTitle())
        .announcement(c.getAnnouncement())
        .totalTime(c.getTotalTime())
        .startTime(c.getStartTime())
        .visibility(c.getVisibility())
        .lockTime(c.getLockTime())
        .build();
  }
}
