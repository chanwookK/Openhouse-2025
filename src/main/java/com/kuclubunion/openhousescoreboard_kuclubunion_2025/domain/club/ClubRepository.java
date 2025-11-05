package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ClubRepository extends JpaRepository<Club, UUID> {
  List<Club> findAllByOrderByScoreDesc();

  /** 전체 클럽 점수를 0으로 초기화 */
  @Modifying
  @Transactional
  @Query("UPDATE Club c SET c.score = 0")
  void resetAllScores();

  Optional<Club> findFirstByName(String name);
}
