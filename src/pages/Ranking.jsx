import { useState, useEffect, useRef } from "react"
import { leaderboardService } from "../services/leaderboardService"
import SockJS from "sockjs-client"
import Stomp from "stompjs"
import "./Ranking.css"

function Ranking() {
  const [viewMode, setViewMode] = useState("individual") // 'individual', 'team', 'log'
  const [meta, setMeta] = useState({
    title: '오픈하우스 "모여봐요 동아리의 숲" 점수판',
    announcement:
      "행사 관련 자세한 정보는 동아리연합회 인스타그램에서 확인 부탁드립니다.",
    totalTime: 0, // 전체 시간 (분)
    startTime: null, // 시작 시각
    elapsedTime: "00:00", // 경과 시간
    remainingTime: null, // 남은 시간
  })

  const [individualData, setIndividualData] = useState([])
  const [teamData, setTeamData] = useState([])
  const [logData, setLogData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [visibility, setVisibility] = useState(true)

  const hasRealtimeRef = useRef(false)
  const stompClientRef = useRef(null)

  // ======================
  // ✅ WebSocket 연결
  // ======================
  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws/leaderboard")
    const client = Stomp.over(socket)
    client.debug = null

    client.connect({}, () => {
      console.log("✅ WebSocket 연결 성공")
      stompClientRef.current = client

      client.subscribe("/topic/leaderboard", (msg) => {
        try {
          const payload = JSON.parse(msg.body)
          hasRealtimeRef.current = true
          console.log("🏆 실시간 업데이트 수신:", payload)

          // ✅ META 데이터 수신
          if (payload.type === "META" && payload.competitionMetaRes) {
            const metaRes = payload.competitionMetaRes
            setMeta((prev) => ({
              ...prev,
              title: metaRes.title || prev.title,
              announcement: metaRes.announcement || prev.announcement,
              totalTime: metaRes.totalTime || prev.totalTime,
              startTime: metaRes.startTime || prev.startTime,
            }))
            if (metaRes.visibility !== undefined) {
              setVisibility(metaRes.visibility)
            }
            console.log("🧭 메타데이터 갱신됨:", metaRes)
            return
          }

          // 🔹 클럽 랭킹
          if (payload.clubLeaderboardRes) {
            const sorted = [...payload.clubLeaderboardRes].sort(
              (a, b) => b.score - a.score
            )
            setIndividualData(
              sorted.map((club, index) => ({
                rank: index + 1,
                name: club.name,
                score: club.score,
                medal:
                  index < 3 ? ["gold", "silver", "bronze"][index] : null,
              }))
            )
          }

          // 🔹 팀 랭킹
          if (payload.teamLeaderboardRes) {
            const sorted = [...payload.teamLeaderboardRes].sort(
              (a, b) => b.score - a.score
            )
            setTeamData(
              sorted.map((team, index) => ({
                rank: index + 1,
                name: team.name,
                score: team.score,
                medal:
                  index < 3 ? ["gold", "silver", "bronze"][index] : null,
              }))
            )
          }

          // 🔹 로그 내역 (timestamp + delta 반영)
          if (payload.scoreHistoryRes) {
            const sorted = [...payload.scoreHistoryRes].sort(
              (a, b) => new Date(b.timestamp) - new Date(a.timestamp)
            )
          
            setLogData(
              sorted.map((item) => ({
                time: new Date(item.timestamp).toLocaleTimeString("ko-KR", {
                  hour: "2-digit",
                  minute: "2-digit",
                  second: "2-digit",
                }),
                name: item.reason?.split("가(이)")[0] || "알 수 없음",
                action: item.reason || "기록 없음",
                points:
                  item.delta > 0
                    ? `+${item.delta}`
                    : item.delta < 0
                    ? `${item.delta}`
                    : "+0",
              }))
            )
          }
        } catch (err) {
          console.error("❌ WebSocket 메시지 파싱 실패:", err)
        }
      })
    })

    return () => {
      if (client && client.connected) {
        client.disconnect(() => console.log("🔌 WebSocket 연결 해제"))
      }
    }
  }, [])

  // ======================
  // ✅ 경과 시간 계산 (1초마다)
  // ======================
  useEffect(() => {
    if (!meta.startTime) return
    const start = new Date(meta.startTime)

    const timer = setInterval(() => {
      const now = new Date()
      const diffMs = now - start
      const totalSeconds = Math.floor(diffMs / 1000)
      const minutes = Math.floor(totalSeconds / 60)
      const seconds = totalSeconds % 60

      const remaining = meta.totalTime
        ? Math.max(0, meta.totalTime * 60 - totalSeconds)
        : null
      const remainingMin = remaining ? Math.floor(remaining / 60) : 0
      const remainingSec = remaining ? remaining % 60 : 0

      setMeta((prev) => ({
        ...prev,
        elapsedTime: `${String(minutes).padStart(2, "0")}:${String(
          seconds
        ).padStart(2, "0")}`,
        remainingTime: remaining
          ? `${String(remainingMin).padStart(2, "0")}:${String(
              remainingSec
            ).padStart(2, "0")}`
          : null,
      }))
    }, 1000)

    return () => clearInterval(timer)
  }, [meta.startTime, meta.totalTime])

  // ======================
  // ✅ 초기 데이터 로드
  // ======================
  useEffect(() => {
    loadData()
  }, [viewMode])

  const loadData = async () => {
    try {
      setLoading(true)

      // 🔹 메타데이터 로드
      const metaResponse = await leaderboardService.getCompetitionMeta()
      if (metaResponse) {
        setMeta((prev) => ({
          ...prev,
          title: metaResponse.title || prev.title,
          announcement: metaResponse.announcement || prev.announcement,
          totalTime: metaResponse.totalTime || prev.totalTime,
          startTime: metaResponse.startTime || prev.startTime,
        }))
        setVisibility(metaResponse.visibility !== false)
      }

      // 🔹 REST 데이터 (실시간 전까지만)
      if (!hasRealtimeRef.current) {
        if (viewMode === "individual") {
          const clubs = await leaderboardService.getClubRanking()
          const sorted = clubs.sort((a, b) => b.score - a.score)
          setIndividualData(
            sorted.map((club, index) => ({
              rank: index + 1,
              name: club.name,
              score: club.score,
              medal:
                index < 3 ? ["gold", "silver", "bronze"][index] : null,
            }))
          )
        } else if (viewMode === "team") {
          const teams = await leaderboardService.getTeamRanking()
          const sorted = teams.sort((a, b) => b.score - a.score)
          setTeamData(
            sorted.map((team, index) => ({
              rank: index + 1,
              name: team.name,
              score: team.score,
              medal:
                index < 3 ? ["gold", "silver", "bronze"][index] : null,
            }))
          )
        } else if (viewMode === "log") {
          const history = await leaderboardService.getHistory()
          const sorted = [...history].sort(
            (a, b) => new Date(b.timestamp) - new Date(a.timestamp)
          )
        
          setLogData(
            sorted.map((item) => ({
              time: new Date(item.timestamp).toLocaleTimeString("ko-KR", {
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
              }),
              name: item.reason?.split("가(이)")[0] || "알 수 없음",
              action: item.reason || "기록 없음",
              points:
                item.delta > 0
                  ? `+${item.delta}`
                  : item.delta < 0
                  ? `${item.delta}`
                  : "+0",
            }))
          )
        }        
      }

      setError(null)
    } catch (err) {
      console.error("데이터 로드 실패:", err)
      setError("데이터를 불러오는데 실패했습니다.")
    } finally {
      setLoading(false)
    }
  }

  // ======================
  // ✅ 점수판 가시성 주기적 확인
  // ======================
  useEffect(() => {
    const visibilityCheck = setInterval(async () => {
      try {
        const meta = await leaderboardService.getCompetitionMeta()
        if (meta && meta.visibility !== undefined) {
          setVisibility(meta.visibility === true)
        }
      } catch (err) {
        console.error("Visibility check failed:", err)
      }
    }, 5000)

    return () => clearInterval(visibilityCheck)
  }, [])

  // ======================
  // 렌더링 함수
  // ======================
  const renderMedal = (medal) => {
    if (medal === "gold") return <div className="medal medal-gold"></div>
    if (medal === "silver") return <div className="medal medal-silver"></div>
    if (medal === "bronze") return <div className="medal medal-bronze"></div>
    return null
  }

  const getRowClass = (index, medal) => {
    if (index === 0 && medal) return "ranking-row-leader-1"
    if (index === 1 && medal) return "ranking-row-leader-2"
    if (index === 2 && medal) return "ranking-row-leader-3"
    return "ranking-row"
  }

  // ======================
  // ✅ UI 렌더링
  // ======================
  return (
    <div className="ranking-page">
      <div className="ranking-hero">
        <div className="trophy-icon">
          <svg
            viewBox="0 0 100 100"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M50 20L40 40H60L50 20Z"
              fill={visibility ? "#FFD700" : "#9E9E9E"}
            />
            <path
              d="M30 40H70L65 80H35L30 40Z"
              fill={visibility ? "#FFD700" : "#9E9E9E"}
            />
          </svg>
        </div>
        <h1>{meta.title}</h1>
        <p>{meta.announcement}</p>

        {meta.startTime && (
          <p style={{ marginTop: "0.5rem", color: "#4ade80" }}>
            ⏱ {meta.remainingTime && ` 남은 시간: ${meta.remainingTime}`}
          </p>
        )}

        {!visibility && (
          <p style={{ color: "#999", fontSize: "0.9rem", marginTop: "1rem" }}>
            점수판이 숨겨져 있습니다.
          </p>
        )}
      </div>

      {visibility && (
        <>
          <div className="ranking-toggle-container">
            <div className="toggle-buttons">
              <button
                className={`toggle-btn ${
                  viewMode === "individual" ? "active" : ""
                }`}
                onClick={() => setViewMode("individual")}
              >
                동아리 개별점수
              </button>
              <button
                className={`toggle-btn ${
                  viewMode === "team" ? "active" : ""
                }`}
                onClick={() => setViewMode("team")}
              >
                팀별 점수
              </button>
              <button
                className={`toggle-btn ${viewMode === "log" ? "active" : ""}`}
                onClick={() => setViewMode("log")}
              >
                점수 로그
              </button>
            </div>
          </div>

          <div className="ranking-content">
            {loading && (
              <div className="loading-message">데이터를 불러오는 중...</div>
            )}
            {error && <div className="error-message">{error}</div>}

            {!loading && !error && viewMode === "individual" && (
              <div className="ranking-table">
                <div className="ranking-header">
                  <div className="header-cell">Rank</div>
                  <div className="header-cell">Name</div>
                  <div className="header-cell">Score</div>
                </div>
                {individualData.map((item, index) => (
                  <div
                    key={`${item.name}-${item.rank}`}
                    className={getRowClass(index, item.medal)}
                  >
                    <div className="table-cell">
                      {renderMedal(item.medal)}
                      {!item.medal && <span>{item.rank}</span>}
                    </div>
                    <div className="table-cell">{item.name}</div>
                    <div className="table-cell">
                      {item.score.toLocaleString()}
                    </div>
                  </div>
                ))}
              </div>
            )}

            {!loading && !error && viewMode === "team" && (
              <div className="ranking-table">
                <div className="ranking-header">
                  <div className="header-cell">Rank</div>
                  <div className="header-cell">Team</div>
                  <div className="header-cell">Score</div>
                </div>
                {teamData.map((item, index) => (
                  <div
                    key={`${item.name}-${item.rank}`}
                    className={getRowClass(index, item.medal)}
                  >
                    <div className="table-cell">
                      {renderMedal(item.medal)}
                      {!item.medal && <span>{item.rank}</span>}
                    </div>
                    <div className="table-cell">{item.name}</div>
                    <div className="table-cell">
                      {item.score.toLocaleString()}
                    </div>
                  </div>
                ))}
              </div>
            )}

            {!loading && !error && viewMode === "log" && (
              <div className="log-table">
                <div className="log-header">
                  <div className="header-cell">Time</div>
                  <div className="header-cell">Name</div>
                  <div className="header-cell">Action</div>
                  <div className="header-cell">Points</div>
                </div>
                {logData.map((item, index) => (
                  <div key={index} className="log-row">
                    <div className="table-cell">{item.time}</div>
                    <div className="table-cell">{item.name}</div>
                    <div className="table-cell">{item.action}</div>
                    <div
                      className={`table-cell ${
                        item.points.startsWith("+")
                          ? "points-positive"
                          : item.points.startsWith("-")
                          ? "points-negative"
                          : ""
                      }`}
                    >
                      {item.points}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  )
}

export default Ranking
