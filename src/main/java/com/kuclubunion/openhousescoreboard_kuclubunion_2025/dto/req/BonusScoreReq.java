package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BonusScoreReq {
  private String clubName;
  private int delta;
  private String reason;
}
