package com.kuclubunion.openhousescoreboard_kuclubunion_2025.presentation;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req.BonusScoreReq;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req.ScoreAdjustReq;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.ScoreAdjustRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.ScoreboardRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/judge/score")
@RequiredArgsConstructor
public class JudgeController {
  private final ScoreService scoreService;

  @GetMapping("/scoreboard")
  public ResponseEntity<?> getScoreboard(@RequestParam String defenseClubName) {
    ScoreboardRes result;
    try {
      result = scoreService.getScoreboard(defenseClubName);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    return ResponseEntity.ok(result);
  }

  /** POST: 팀 점수 조정 (락 및 confirm 처리 포함) */
  @PostMapping("/adjust")
  public ResponseEntity<?> adjustScore(@RequestBody ScoreAdjustReq req) {
    ScoreAdjustRes result;
    try {
      result = scoreService.adjustTeamScore(
          req.getAttackClubId(),
          req.getDefenseClubId(),
          req.getDelta(),
          req.isConfirm()
      );
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/bonus")
  public ResponseEntity<?> bonusScore(@RequestBody BonusScoreReq req) {
    scoreService.adjustBonusScore(req.getClubName(), req.getDelta(), req.getReason());
    return ResponseEntity.ok().build();
  }
}
