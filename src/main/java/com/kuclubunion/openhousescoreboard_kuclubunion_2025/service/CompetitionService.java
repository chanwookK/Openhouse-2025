package com.kuclubunion.openhousescoreboard_kuclubunion_2025.service;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.ClubRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.ClubToClubManagerRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.Competition;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.CompetitionRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.CompetitionStatus;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.history.ScoreHistoryRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team.TeamRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req.CompetitionUpdateReq;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.req.StartCompetitionReq;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.AllBoardDataRes.Type;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.websocket.WebSocketHub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompetitionService {

  private final CompetitionRepository competitionRepository;
  private final WebSocketHub webSocketHub;
  private final ClubRepository clubRepository;
  private final TeamRepository teamRepository;
  private final ClubToClubManagerRepository clubToClubManagerRepository;
  private final ScoreHistoryRepository scoreHistoryRepository;
  private final WebSocketService webSocketService;

  @Transactional
  public void updateCompetitionMetadata(CompetitionUpdateReq dto) {
    Competition competition = competitionRepository.findAll()
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("활성 대회가 존재하지 않습니다."));

    competition.updateMetadata(
        dto.getTitle(),
        dto.getAnnouncement(),
        dto.getTotalTime(),
        dto.getVisibility()
    );

    competitionRepository.save(competition);

    // 실시간 메타데이터 변경 이벤트
    webSocketHub.broadcastLeaderboard(webSocketService.getAllBoardData(Type.META));
  }

  @Transactional
  public void startCompetition(StartCompetitionReq dto) {
    boolean isConfirm = dto.isConfirm();
    Competition competition = competitionRepository.findAll()
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("활성 대회가 없습니다."));

    if (isConfirm) {
      competition.restartCompetition();
    }
    else if (competition.getStatus() == CompetitionStatus.CLOSED) {
      competition.startCompetition();
    }
    else {
      log.info("isConfirm=false, status={}", competition.getStatus());
      throw new IllegalStateException("이미 진행 중입니다. 재시작 확인 필요");
    }
    competitionRepository.save(competition);


    clubRepository.resetAllScores();
    teamRepository.resetAllScores();
    clubToClubManagerRepository.clearAllLocks();
    scoreHistoryRepository.deleteAllHistories();


    webSocketHub.broadcastLeaderboard(webSocketService.getAllBoardData(Type.META));
  }
}
