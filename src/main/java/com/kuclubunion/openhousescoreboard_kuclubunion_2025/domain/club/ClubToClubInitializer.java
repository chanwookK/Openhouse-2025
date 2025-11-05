package com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.club;

import com.kuclubunion.openhousescoreboard_kuclubunion_2025.domain.team.TeamRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClubToClubInitializer {

  private final ClubRepository clubRepository;
  private final ClubToClubManagerRepository clubToClubManagerRepository;
  private final TeamRepository teamRepository;

  @PostConstruct
  @Transactional
  public void initializeClubRelations() {
    log.info("[ClubToClubInitializer] Initializing ClubToClubManager relations...");

    List<Club> clubs = clubRepository.findAll();
    if (clubs.isEmpty()) {
      log.warn("No clubs found in DB — skipping ClubToClubManager initialization.");
      return;
    }

    // DB에서 기존 관계를 모두 가져와서 빠르게 조회
    Set<String> existingPairs = new HashSet<>();
    clubToClubManagerRepository.findAll().forEach(rel -> {
      existingPairs.add(pairKey(rel.getAttackClub().getId(), rel.getDefenseClub().getId()));
    });

    List<ClubToClubManager> newRelations = new ArrayList<>();

    for (Club attack : clubs) {
      for (Club defense : clubs) {
        // 자기 자신은 제외
        if (attack.getId().equals(defense.getId())) {
          continue;
        }

        // 동일 팀은 제외
        if (attack.getTeam().equals(defense.getTeam())) {
          continue;
        }

        String key = pairKey(attack.getId(), defense.getId());

        // 이미 존재하면 유효성 검증
        if (existingPairs.contains(key)) {
          log.debug("Valid relation exists: {} -> {}", attack.getName(), defense.getName());
          continue;
        }

        // 신규 생성
        ClubToClubManager relation = ClubToClubManager.builder()
            .attackClub(attack)
            .defenseClub(defense)
            .lockedUntil(LocalDateTime.now())
            .build();
        newRelations.add(relation);
        log.info("Created new ClubToClubManager: {} -> {}", attack.getName(), defense.getName());
      }
    }

    // 한번에 저장 (batch insert)
    if (!newRelations.isEmpty()) {
      clubToClubManagerRepository.saveAll(newRelations);
      log.info("{} new ClubToClubManager relations inserted.", newRelations.size());
    } else {
      log.info("All ClubToClubManager relations already valid.");
    }
    long teamCount = teamRepository.count();
    long expectedCount = (long) clubs.size() * (clubs.size()* (teamCount - 1)/teamCount);
    long actualCount = clubToClubManagerRepository.count();
    if (expectedCount != actualCount) {
      log.error("Validation failed: expected {} relations, found {}.", expectedCount, actualCount);
      throw new IllegalStateException("ClubToClubManager validation failed: mismatch in relation count.");
    }
  }

  private String pairKey(UUID a, UUID b) {
    return a.toString() + "_" + b.toString();
  }
}
