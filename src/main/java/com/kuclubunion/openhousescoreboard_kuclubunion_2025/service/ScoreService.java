package com.kuclubunion.openhousescoreboard_kuclubunion_2025.service;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.Club;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.ClubRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.ClubToClubManager;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.ClubToClubManagerRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.Competition;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.CompetitionRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.history.ScoreHistory;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.history.ScoreHistoryRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.AllBoardDataRes.Type;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.ScoreAdjustRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.ScoreboardRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.ScoreboardRes.ScoreboardClubRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.websocket.WebSocketHub;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScoreService {

  private final ClubRepository clubRepository;
  private final ClubToClubManagerRepository lockRepository;
  private final ScoreHistoryRepository scoreHistoryRepository;
  private final WebSocketHub webSocketHub;
  private final CompetitionRepository competitionRepository;
  private final WebSocketService webSocketService;

  @Transactional(readOnly = true)
  public ScoreboardRes getScoreboard(String clubName) {
    Club defenseClub = clubRepository.findFirstByName(clubName)
        .orElseThrow(IllegalArgumentException::new);

    List<ScoreboardClubRes> attackClubRes = new ArrayList<>(
        lockRepository.findByDefenseClub(defenseClub).stream()
            .map(ScoreboardClubRes::by)
            .toList());

    attackClubRes.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
    return new ScoreboardRes(defenseClub.getName(), defenseClub.getId(), defenseClub.getVisit(),
        defenseClub.getScore(), attackClubRes);
  }

  @Transactional
  public void adjustBonusScore(String clubName, int delta, String reason) {
    Competition competition = competitionRepository.findAll()
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("활성 대회가 없습니다."));

    Club club = clubRepository.findFirstByName(clubName)
        .orElseThrow(IllegalArgumentException::new);
    club.addScore(delta);
    club.getTeam().addScore(delta);
    clubRepository.save(club);

    // 이력 기록
    ScoreHistory history = ScoreHistory.builder()
        .attackClubName(club.getName())
        .defenseClubName(club.getName())
        .reason(reason)
        .delta(delta)
        .timestamp(LocalDateTime.now())
        .build();

    scoreHistoryRepository.save(history);

    // 브로드캐스트
    webSocketHub.broadcastLeaderboard(webSocketService.getAllBoardData(Type.LEADERBOARD));
  }

  @Transactional
  public ScoreAdjustRes adjustTeamScore(UUID attackId, UUID defenseId, int delta,
      boolean isConfirm) {

    Competition competition = competitionRepository.findAll()
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("활성 대회가 없습니다."));

    // 락 조회
    ClubToClubManager lock = lockRepository
        .findByAttackClubIdAndDefenseClubId(attackId, defenseId)
        .orElseThrow(() -> new IllegalArgumentException("Club Id 가 잘못되었습니다."));

    // 락 확인
    if (lock.isLocked() && !isConfirm) {
      throw new IllegalStateException(lock.getLockedUntil() + " 까지 점수 조정이 불가합니다.");
    }

    // 팀 조회
    Club attack = clubRepository.findById(attackId)
        .orElseThrow(() -> new IllegalArgumentException("공격 클럽을 찾을 수 없습니다."));
    Club defense = clubRepository.findById(defenseId)
        .orElseThrow(() -> new IllegalArgumentException("방어 클럽을 찾을 수 없습니다."));

    // 점수 반영
    attack.addScore(delta);
    defense.addScore(-delta);
    attack.getTeam().addScore(delta);
    defense.getTeam().addScore(-delta);
    defense.addVisit();
    clubRepository.save(attack);
    clubRepository.save(defense);

    // 락 갱신
    if (competition.getLockTime() == null) {
      competition.updateLockTime(15);
    }
    lock.renewLock(competition.getLockTime());
    lockRepository.save(lock);

    // 이력 기록
    ScoreHistory history = ScoreHistory.builder()
        .attackClubName(attack.getName())
        .defenseClubName(defense.getName())
        .delta(delta)
        .timestamp(LocalDateTime.now())
        .build();
    scoreHistoryRepository.save(history);

    // 브로드캐스트
    webSocketHub.broadcastLeaderboard(webSocketService.getAllBoardData(Type.LEADERBOARD));

    return new ScoreAdjustRes(attack.getScore(), defense.getScore());
  }
}
