package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.history;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, Long> {
  List<ScoreHistory> findAllByOrderByTimestampAsc();

  /** 모든 점수 기록 삭제 */
  @Modifying
  @Transactional
  @Query("DELETE FROM ScoreHistory")
  void deleteAllHistories();
}
