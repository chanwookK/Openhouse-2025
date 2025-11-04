import api from '../utils/api'

export const adminService = {
  // UC-A1~A3~A5: 대회 메타데이터 수정
  updateCompetitionMeta: (data) => api.patch('/admin/competition', data),
  
  // UC-A4: 대회 시작
  startCompetition: (confirm = false) => api.post('/admin/competition/start', { confirm }),
}



