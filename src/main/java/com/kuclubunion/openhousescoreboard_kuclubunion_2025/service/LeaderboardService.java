package com.kuclubunion.openhousescoreboard_kuclubunion_2025.service;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.Club;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club.ClubRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.Competition;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.CompetitionRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team.Team;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team.TeamRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.ClubLeaderboardRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.CompetitionMetaRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.TeamLeaderboardRes;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.global.LeaderboardCache;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

  private final CompetitionRepository competitionRepository;
  private final ClubRepository clubRepository;
  private final TeamRepository teamRepository;
  private final LeaderboardCache cache;

  @Transactional(readOnly = true)
  public CompetitionMetaRes getCompetitionMeta() {
    Competition comp = competitionRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("대회가 존재하지 않습니다."));

    return CompetitionMetaRes.from(comp);
  }

  @Transactional(readOnly = true)
  public List<ClubLeaderboardRes> getClubLeaderboard() {
    Competition comp = competitionRepository.findAll()
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("활성 대회가 없습니다."));

    if (!comp.getVisibility()) {
      throw new IllegalStateException("현재 점수판이 비공개 상태입니다.");
    }

    List<Club> allByOrderByScoreDesc = clubRepository.findAllByOrderByScoreDesc();
    return allByOrderByScoreDesc.stream()
        .map(ClubLeaderboardRes::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<TeamLeaderboardRes> getTeamLeaderboard() {
    Competition comp = competitionRepository.findAll()
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("활성 대회가 없습니다."));

    if (!comp.getVisibility()) {
      throw new IllegalStateException("현재 점수판이 비공개 상태입니다.");
    }

    List<Team> allByOrderByTeamDesc = teamRepository.findAllByOrderByScoreDesc();
    return allByOrderByTeamDesc.stream()
        .map(TeamLeaderboardRes::from)
        .toList();
  }
}