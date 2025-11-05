package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.history;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(indexes = {
    @Index(name = "idx_history_ts", columnList = "timestamp")
})
public class ScoreHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String attackClubName;

  @Column(nullable = false, length = 120)
  private String defenseClubName;

  @Column(nullable = true)
  private String reason;

  /**
   * 공격 관점 delta(양수: 공격 +, 방어 - / 음수: 공격 -, 방어 +)
   */
  @Column(nullable = false)
  private int delta;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  public String toString() {
    StringBuilder sb = new StringBuilder();

    if (reason != null) {
      sb.append(attackClubName).append("가(이) ").append(reason);
      return sb.toString();
    }

    sb.append(attackClubName).append("가(이) ").append(defenseClubName).append(" 공격에 ");
    if (delta > 0) {
      sb.append("성공했습니다.");
    } else {
      sb.append("실패했습니다.");
    }
    return sb.toString();
  }
}