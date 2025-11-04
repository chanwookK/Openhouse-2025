import api from '../utils/api'

export const rankingService = {
  // 동아리 개별 점수 조회
  getIndividualRanking: () => api.get('/ranking/individual'),
  
  // 팀별 점수 조회
  getTeamRanking: () => api.get('/ranking/team'),
  
  // 점수 로그 조회
  getScoreLogs: () => api.get('/ranking/logs'),
  
  // 실시간 점수 업데이트 구독
  subscribeToUpdates: (callback) => {
    // WebSocket 연결 로직
    // 실제 구현 시 stomp.js 또는 socket.io 사용
  }
}


