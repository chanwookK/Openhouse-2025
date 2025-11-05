package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
    @Index(name = "idx_lock_pair", columnList = "attackClubId, defenseClubId", unique = true),
    @Index(name = "idx_locked_until", columnList = "lockedUntil")
})
public class ClubToClubManager {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "attack_club_id")
  private Club attackClub;

  @ManyToOne
  @JoinColumn(name = "defense_club_id")
  private Club defenseClub;

  @Column
  private LocalDateTime lockedUntil;

  // ===== 도메인 로직 =====
  public boolean isLocked() {
    return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
  }

  public void renewLock(int minutes) {
    this.lockedUntil = LocalDateTime.now().plusMinutes(minutes);
  }
}
