import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import './ScoreBoard.css'

function ScoreBoard() {
  const [searchParams] = useSearchParams()
  const clubName = searchParams.get('club') || 'Guest'
  const [clubs, setClubs] = useState([])
  const [defenseClubId, setDefenseClubId] = useState(null)
  const [defenseClubVisit, setDefenseClubVisit] = useState(null)
  const [defenseClubScore, setDefenseClubScore] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // 현재 시간 문자열 (HH:mm)
  const getCurrentTime = () => {
    const now = new Date()
    return `${String(now.getHours()).padStart(2, '0')}:${String(
      now.getMinutes()
    ).padStart(2, '0')}`
  }

  // 점수판 불러오기
  useEffect(() => {
    const fetchScoreboard = async () => {
      if (!clubName || clubName === 'Guest') return
      try {
        const res = await fetch(
          `http://localhost:8080/api/v1/judge/score/scoreboard?defenseClubName=${encodeURIComponent(
            clubName
          )}`
        )
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`)
        const data = await res.json()
        setDefenseClubId(data.defenseClubId)
        setDefenseClubVisit(data.defenseClubVisit)
        setDefenseClubScore(data.defenseClubScore)
        setClubs(
          data.scoreboardClubRes.map(c => ({
            id: c.id,
            name: c.name,
            score: c.score,
            visit: c.visit,
            lockTime: new Date(c.lockedUntil),
            lockTimeString: new Date(c.lockedUntil).toLocaleTimeString('ko-KR', {
              hour: '2-digit',
              minute: '2-digit',
            }),
          }))
        )
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchScoreboard()
  }, [clubName])

  // 🔹 점수 조정 API 호출 함수
  const adjustScore = async (attackClubId, delta, confirm = false) => {
    try {
      const res = await fetch('http://localhost:8080/api/v1/judge/score/adjust', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          attackClubId,
          defenseClubId,
          delta,
          confirm,
        }),
      })

      if (!res.ok) throw new Error(`점수 조정 실패: ${res.status}`)
      const result = await res.json()
      console.log('조정 결과:', result)
      alert('점수 조정이 완료되었습니다.')
      // ✅ 갱신
      window.location.reload()
    } catch (err) {
      alert(err.message)
    }
  }

  // 🔒 락 시간 체크
  const isWithinLockTime = (lockTime) => {
    const now = new Date()
    const diffMinutes = (now - lockTime) / (1000 * 60)
    return diffMinutes < 15
  }

  // ⚔️ 공격 성공 처리
  const handleAttackSuccess = (clubId) => {
    const target = clubs.find(c => c.id === clubId)
    if (!target) return

    // 15분 락 내 시도 시 경고
    if (isWithinLockTime(target.lockTime)) {
      const confirmForce = window.confirm(
        '⚠️ 락 제한시간(15분) 내 재시도입니다.\n강제로 조정하시겠습니까?'
      )
      if (confirmForce) {
        adjustScore(clubId, 1, true) // confirm=true
      } else {
        alert('조정이 취소되었습니다.')
      }
      return
    }

    adjustScore(clubId, 1, false)
  }

  // ❌ 공격 실패 처리
  const handleAttackFailure = (clubId) => {
    const target = clubs.find(c => c.id === clubId)
    if (!target) return

    if (isWithinLockTime(target.lockTime)) {
      const confirmForce = window.confirm(
        '⚠️ 락 제한시간(15분) 내 재시도입니다.\n강제로 조정하시겠습니까?'
      )
      if (confirmForce) {
        adjustScore(clubId, -1, true)
      } else {
        alert('조정이 취소되었습니다.')
      }
      return
    }

    adjustScore(clubId, -1, false)
  }

  if (loading) return <p>로딩 중...</p>
  if (error) return <p>오류 발생: {error}</p>

  return (
    <div className="score-board-page">
      <div className="header">
        <p className="login-info">
          동아리 이름: {clubName}에 로그인 되셨습니다.
          <br/>동아리 점수: {defenseClubScore}
          <br/>동아리 방문 횟수: {defenseClubVisit}
        </p>
        <h1>Score Board</h1>
        {defenseClubId && <p className="club-id">Club ID: {defenseClubId}</p>}
      </div>

      <div className="club-grid">
        {clubs.map(club => (
          <div key={club.id} className="club-card">
            <h3 className="club-name">{club.name}</h3>
            <div className="club-info">
              <div className="info-row">
                <span className="info-label">점수:</span>
                <span className="info-value">{club.score}</span>
              </div>
              <div className="info-row">
                <span className="info-label">재도전 가능 시간:</span>
                <span className="info-value">{club.lockTimeString}</span>
              </div>
            </div>
            <div className="button-group">
              <button
                className="btn-success"
                onClick={() => handleAttackSuccess(club.id)}
              >
                공격 성공 ⚔️
              </button>
              <button
                className="btn-failure"
                onClick={() => handleAttackFailure(club.id)}
              >
                공격 실패 💀
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default ScoreBoard
