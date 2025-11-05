package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.Competition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
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
public class Team {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private int score;

  /**
   * 연관(설계 다이어그램: Competition 1 -> * Team)
   */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Competition competition;

  // ===== 도메인 로직 =====
  public void addScore(int delta) {
    this.score += delta;
  }
}