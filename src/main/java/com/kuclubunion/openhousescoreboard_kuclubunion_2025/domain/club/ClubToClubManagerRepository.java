package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ClubToClubManagerRepository extends JpaRepository<ClubToClubManager, UUID> {
  Optional<ClubToClubManager> findByAttackClubIdAndDefenseClubId(UUID attackClubId, UUID defenseClubId);

  /** 모든 락 초기화 */
  @Modifying
  @Transactional
  @Query("UPDATE ClubToClubManager m SET m.lockedUntil = null")
  void clearAllLocks();


  @EntityGraph(attributePaths = {"attackClub"})
  List<ClubToClubManager> findByDefenseClub(Club defenseClub);
}
