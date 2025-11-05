package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TeamRepository extends JpaRepository<Team, UUID> {
  List<Team> findAllByOrderByScoreDesc();

  /** 전체 팀 점수를 0으로 초기화 */
  @Modifying
  @Transactional
  @Query("UPDATE Team t SET t.score = 0")
  void resetAllScores();
}
