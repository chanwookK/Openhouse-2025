package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team.Team;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class Competition {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String announcement;

  /**
   * 총 진행 시간(분)
   */
  @Column
  private Integer totalTime;

  @Column
  private Boolean visibility;

  @Column(columnDefinition = "INT DEFAULT 15")
  private Integer lockTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CompetitionStatus status; // CLOSED / RUNNING

  @Column
  private LocalDateTime startTime;

  /**
   * 연관(설계 다이어그램: Competition 1 -> * Team)
   */
  @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Team> teams = new ArrayList<>();

  // ===== 도메인 로직 =====

  public boolean canStart() {
    return this.status != CompetitionStatus.RUNNING;
  }

  /**
   * 최초 시작
   */
  public void startCompetition() {
    if (!canStart()) {
      return;
    }
    this.status = CompetitionStatus.RUNNING;
    this.startTime = LocalDateTime.now();
  }

  /**
   * 강제 재시작
   */
  public void restartCompetition() {
    this.status = CompetitionStatus.RUNNING;
    this.startTime = LocalDateTime.now();
  }

  /**
   * 부분 업데이트: null이 아닌 값만 반영
   */
  public void updateMetadata(String title, String announcement, Integer totalTime,
      Boolean visibility) {
    if (title != null) {
      String t = title.trim();
      if (t.isEmpty()) {
        throw new IllegalArgumentException("title must not be blank");
      }
      this.title = title;
    }
    if (announcement != null) {
      this.announcement = announcement;
    }
    if (totalTime != null) {
      if (totalTime <= 0) {
        throw new IllegalArgumentException("totalTime must be positive");
      }
      this.totalTime = totalTime;
    }
    if (visibility != null) {
      this.visibility = visibility;
    }
  }

  public void updateLockTime(Integer lockTime) {
    if (lockTime != null) {
      if (lockTime <= 0) {
        throw new IllegalArgumentException("lockTime must be positive");
      }
      this.lockTime = lockTime;
    }
  }
}
