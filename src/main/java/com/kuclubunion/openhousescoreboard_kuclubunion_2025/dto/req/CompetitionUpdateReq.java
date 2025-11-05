package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetitionUpdateReq {
  private String title;
  private String announcement;
  private Integer totalTime;
  private Boolean visibility;
}
