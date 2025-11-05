package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreAdjustReq {
  private UUID attackClubId;
  private UUID defenseClubId;
  private int delta;
  private boolean confirm;
}
