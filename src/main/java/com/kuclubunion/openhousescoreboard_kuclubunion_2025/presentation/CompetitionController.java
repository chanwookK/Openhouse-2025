package com.kuclubunion.openhousescoreboard_kuclubunion_2025.presentation;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req.CompetitionUpdateReq;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req.StartCompetitionReq;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/competition")
@RequiredArgsConstructor
public class CompetitionController {
  private final CompetitionService competitionService;

  /** PATCH: 메타데이터(제목, 공지, 시간, 공개 여부) 수정 */
  @PatchMapping
  public ResponseEntity<?> updateMetadata(@RequestBody CompetitionUpdateReq request) {
    competitionService.updateCompetitionMetadata(request);
    return ResponseEntity.ok("대회 정보가 수정되었습니다.");
  }

  /** POST: 대회 시작 (isConfirm=true면 강제 재시작) */
  @PostMapping("/start")
  public ResponseEntity<?> startCompetition(@RequestBody StartCompetitionReq request) {
    try {
      competitionService.startCompetition(request);
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    return ResponseEntity.ok("대회가 시작되었습니다.");
  }
}
