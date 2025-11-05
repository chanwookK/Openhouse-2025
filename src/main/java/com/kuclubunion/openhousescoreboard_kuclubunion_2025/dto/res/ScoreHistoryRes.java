package com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.history.ScoreHistory;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ScoreHistoryRes {

  private String reason;
  private int delta;
  private LocalDateTime timestamp;

  public static ScoreHistoryRes from(ScoreHistory h) {
    return ScoreHistoryRes.builder()
        .reason(h.toString())
        .delta(h.getDelta())
        .timestamp(h.getTimestamp())
        .build();
  }
}
