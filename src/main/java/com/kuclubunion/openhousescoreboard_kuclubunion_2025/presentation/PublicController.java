package com.kuclubunion.openhousescoreboard_kuclubunion_2025.presentation;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.ScoreHistoryRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.service.HistoryService;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.service.LeaderboardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

  private final LeaderboardService leaderboardService;
  private final HistoryService historyService;

  /** UC-C1: 리더보드 메타데이터 조회 */
  @GetMapping("/competition/meta")
  public ResponseEntity<?> getMeta() {
    return ResponseEntity.ok(leaderboardService.getCompetitionMeta());
  }

  /** UC-C2: 동아리별 리더보드 조회 */
  @GetMapping("/leaderboard/clubs")
  public ResponseEntity<?> getClubLeaderboard() {
    return ResponseEntity.ok(leaderboardService.getClubLeaderboard());
  }

  /** UC-C3: 팀별 리더보드 조회 */
  @GetMapping("/leaderboard/teams")
  public ResponseEntity<?> getTeamLeaderboard() {
    return ResponseEntity.ok(leaderboardService.getTeamLeaderboard());
  }

  /** UC-C4: 전체 점수 이력 조회 */
  @GetMapping("/history")
  public ResponseEntity<List<ScoreHistoryRes>> getAllHistory() {
    return ResponseEntity.ok(historyService.getAllHistory());
  }
}