import api from '../utils/api'

export const leaderboardService = {
  // UC-C1: 리더보드 메타데이터 조회
  getCompetitionMeta: () => api.get('/public/competition/meta'),
  
  // UC-C2: 동아리별 리더보드 조회
  getClubRanking: () => api.get('/public/leaderboard/clubs'),
  
  // UC-C3: 팀별 리더보드 조회
  getTeamRanking: () => api.get('/public/leaderboard/teams'),
  
  // UC-C4: 전체 점수 이력 조회
  getHistory: () => api.get('/public/history'),
}



