package com.kuclubunion.openhousescoreboard_kuclubunion_2025.service;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.Competition;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.competition.CompetitionRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.history.ScoreHistory;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.history.ScoreHistoryRepository;
import com.kuclubunion.openhousescoreboard_kuclubunion_2025.dto.res.ScoreHistoryRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryService {

  private final CompetitionRepository competitionRepository;
  private final ScoreHistoryRepository scoreHistoryRepository;

  @Transactional(readOnly = true)
  public List<ScoreHistoryRes> getAllHistory() {
    Competition comp = competitionRepository.findAll()
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("활성 대회가 없습니다."));

    if (!comp.getVisibility()) {
      throw new IllegalStateException("현재 점수판이 비공개 상태입니다.");
    }

    List<ScoreHistory> histories = scoreHistoryRepository.findAllByOrderByTimestampAsc();

    return histories.stream()
        .map(ScoreHistoryRes::from)
        .toList();
  }
}
