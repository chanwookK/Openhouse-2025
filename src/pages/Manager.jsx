import { useState, useEffect } from 'react'
import { adminService } from '../services/adminService'
import { leaderboardService } from '../services/leaderboardService'
import './Manager.css'
const API_BASE = import.meta.env.VITE_API_URL

function Manager() {
  const [settings, setSettings] = useState({
    title: '',
    notice: '',
    totalTime: 0,
    showScoreboard: true,
    isStarted: false
  })

  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)

  // 🎁 보너스 입력용 state
  const [bonus, setBonus] = useState({
    clubName: '',
    delta: 0,
    reason: ''
  })
  const [bonusLoading, setBonusLoading] = useState(false)

  // 초기 데이터 로드
  useEffect(() => {
    loadCompetitionData()
  }, [])

  const loadCompetitionData = async () => {
    try {
      setLoading(true)
      const data = await leaderboardService.getCompetitionMeta()
      if (data) {
        setSettings(prev => ({
          ...prev,
          title: data.title || '',
          notice: data.announcement || '',
          totalTime: data.totalTime || 0,
          showScoreboard: data.visibility !== false,
          isStarted: data.startTime !== null
        }))
      }
    } catch (error) {
      console.error('대회 정보 로드 실패:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleInputChange = (field, value) => {
    setSettings(prev => ({
      ...prev,
      [field]: value
    }))
  }

  const handleSave = async () => {
    try {
      setSaving(true)
      await adminService.updateCompetitionMeta({
        title: settings.title,
        announcement: settings.notice,
        totalTime: parseInt(settings.totalTime) || 0,
        visibility: settings.showScoreboard
      })
      alert('설정이 저장되었습니다!')
    } catch (error) {
      console.error('설정 저장 실패:', error)
      alert('설정 저장에 실패했습니다.')
    } finally {
      setSaving(false)
    }
  }

  const toggleScoreboard = async () => {
    const newVisibility = !settings.showScoreboard
    try {
      setSaving(true)
      await adminService.updateCompetitionMeta({
        visibility: newVisibility
      })
      setSettings(prev => ({
        ...prev,
        showScoreboard: newVisibility
      }))
      alert(newVisibility ? '점수판이 표시됩니다.' : '점수판이 숨겨졌습니다.')
    } catch (error) {
      console.error('점수판 표시 설정 실패:', error)
      alert('점수판 표시 설정에 실패했습니다.')
    } finally {
      setSaving(false)
    }
  }

  const startCompetition = async () => {
    if (settings.isStarted) {
      const confirmed = window.confirm(
        '대회가 이미 진행 중입니다. 강제로 재시작하시겠습니까?'
      )
      if (confirmed) {
        try {
          setSaving(true)
          await adminService.startCompetition(true)
          setSettings(prev => ({ ...prev, isStarted: true }))
          alert('대회가 재시작되었습니다!')
        } catch (error) {
          console.error('대회 시작 실패:', error)
          alert('대회 시작에 실패했습니다.')
        } finally {
          setSaving(false)
        }
      }
    } else {
      try {
        setSaving(true)
        await adminService.startCompetition(false)
        setSettings(prev => ({ ...prev, isStarted: true }))
        alert('대회가 시작되었습니다!')
      } catch (error) {
        console.error('대회 시작 실패:', error)
        alert('대회 시작에 실패했습니다.')
      } finally {
        setSaving(false)
      }
    }
  }

  // 🎁 보너스 점수 지급 함수
  const giveBonus = async () => {
    if (!bonus.clubName || !bonus.reason) {
      alert('동아리 이름과 사유를 모두 입력해주세요.')
      return
    }

    try {
      setBonusLoading(true)
      const response = await fetch('${API_BASE}/judge/score/bonus', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          clubName: bonus.clubName,
          delta: parseInt(bonus.delta),
          reason: bonus.reason
        })
      })

      if (!response.ok) {
        throw new Error(`보너스 지급 실패 (${response.status})`)
      }

      alert('보너스 점수가 성공적으로 지급되었습니다!')
      setBonus({ clubName: '', delta: 0, reason: '' })
    } catch (error) {
      console.error('보너스 지급 실패:', error)
      alert('보너스 지급 중 오류가 발생했습니다.')
    } finally {
      setBonusLoading(false)
    }
  }

  return (
    <div className="manager-page">
      <div className="manager-header">
        <h1>⚙️ 관리자 페이지</h1>
        <p className="subtitle">점수판 설정 및 대회 관리</p>
      </div>

      <div className="manager-content">
        {/* 설정 섹션 */}
        <div className="settings-section">
          <h2>📝 기본 설정</h2>
          
          <div className="form-group">
            <label htmlFor="title">제목</label>
            <input
              id="title"
              type="text"
              value={settings.title}
              onChange={(e) => handleInputChange('title', e.target.value)}
              placeholder="점수판 제목을 입력하세요"
            />
          </div>

          <div className="form-group">
            <label htmlFor="notice">공지사항</label>
            <textarea
              id="notice"
              value={settings.notice}
              onChange={(e) => handleInputChange('notice', e.target.value)}
              placeholder="공지사항을 입력하세요"
              rows="3"
            />
          </div>

          <div className="form-group">
            <label htmlFor="totalTime">총 진행 시간 (분)</label>
            <input
              id="totalTime"
              type="number"
              value={settings.totalTime}
              onChange={(e) => handleInputChange('totalTime', e.target.value)}
              placeholder="분 단위로 입력하세요"
              min="0"
            />
          </div>

          <div className="form-actions">
            <button onClick={handleSave} className="btn-save" disabled={saving}>
              {saving ? '저장 중...' : '💾 설정 저장'}
            </button>
          </div>
        </div>

        {/* 상태 표시 */}
        <div className="status-section">
          <h2>📊 현재 상태</h2>
          <div className="status-grid">
            <div className={`status-card ${settings.showScoreboard ? 'active' : 'inactive'}`}>
              <div className="status-icon">👁️</div>
              <div className="status-content">
                <h3>점수판 표시</h3>
                <p>{settings.showScoreboard ? '표시 중' : '숨김'}</p>
              </div>
            </div>
            <div className={`status-card ${settings.isStarted ? 'active' : 'inactive'}`}>
              <div className="status-icon">🏁</div>
              <div className="status-content">
                <h3>대회 진행</h3>
                <p>{settings.isStarted ? '진행 중' : '대기 중'}</p>
              </div>
            </div>
          </div>
        </div>

        {/* 버튼 섹션 */}
        <div className="button-section">
          <h2>🎮 제어</h2>
          <div className="button-grid">
            <button 
              className={`control-btn toggle-btn ${!settings.showScoreboard ? 'active' : ''}`}
              onClick={toggleScoreboard}
            >
              <span className="btn-icon">{settings.showScoreboard ? '👁️' : '🙈'}</span>
              <span className="btn-text">
                {settings.showScoreboard ? '점수판 숨기기' : '점수판 표시하기'}
              </span>
            </button>
            <button 
              className={`control-btn start-btn ${settings.isStarted ? 'started' : ''}`}
              onClick={startCompetition}
              disabled={saving}
            >
              <span className="btn-icon">{settings.isStarted ? '⚠️' : '▶️'}</span>
              <span className="btn-text">
                {settings.isStarted ? '대회 재시작' : '대회 시작'}
              </span>
            </button>
          </div>
        </div>

        {/* 🎁 보너스 점수 지급 섹션 */}
        <div className="bonus-section">
          <h2>🎁 보너스 점수 지급</h2>
          <div className="form-group">
            <label htmlFor="clubName">동아리 이름</label>
            <input
              id="clubName"
              type="text"
              value={bonus.clubName}
              onChange={(e) => setBonus({ ...bonus, clubName: e.target.value })}
              placeholder="예: 우주탐구회"
            />
          </div>
          <div className="form-group">
            <label htmlFor="delta">보너스 점수</label>
            <input
              id="delta"
              type="number"
              value={bonus.delta}
              onChange={(e) => setBonus({ ...bonus, delta: e.target.value })}
              placeholder="예: 10"
            />
          </div>
          <div className="form-group">
            <label htmlFor="reason">사유</label>
            <input
              id="reason"
              type="text"
              value={bonus.reason}
              onChange={(e) => setBonus({ ...bonus, reason: e.target.value })}
              placeholder="예: 미션 완료로 인한 보너스 점수 획득"
            />
          </div>
          <button 
            className="btn-bonus"
            onClick={giveBonus}
            disabled={bonusLoading}
          >
            {bonusLoading ? '지급 중...' : '✨ 보너스 점수 지급'}
          </button>
        </div>

        {/* 미리보기 */}
        <div className="preview-section">
          <h2>👀 설정 미리보기</h2>
          <div className="preview-box">
            <h3>{settings.title || '(제목 없음)'}</h3>
            <p>{settings.notice || '(공지사항 없음)'}</p>
            <div className="time-display">총 진행 시간: {settings.totalTime}분</div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Manager
