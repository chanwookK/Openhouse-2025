package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(indexes = {
    @Index(name = "idx_club_name", columnList = "name")
})
public class Club {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false, length = 120, unique = true)
  private String name;

  @Column(nullable = false)
  private int visit;

  @Column(nullable = false)
  private int score;

  /** 연관(설계 다이어그램: Team 1 -> * Club) */
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Team team;

  // ===== 도메인 로직 =====
  public void addScore(int delta) {
    this.score += delta;
  }
  public void addVisit() {
    this.visit++;
  }
}
