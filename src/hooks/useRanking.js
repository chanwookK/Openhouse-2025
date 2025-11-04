import { useState, useEffect } from 'react'
import { rankingService } from '../services/rankingService'

function useRanking(type) {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        setError(null)
        
        let result
        switch (type) {
          case 'individual':
            result = await rankingService.getIndividualRanking()
            break
          case 'team':
            result = await rankingService.getTeamRanking()
            break
          case 'log':
            result = await rankingService.getScoreLogs()
            break
          default:
            result = []
        }
        
        setData(result)
      } catch (err) {
        setError(err.message || '데이터를 불러오는데 실패했습니다.')
        console.error('Error fetching ranking data:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchData()

    // 실제 환경에서는 WebSocket으로 실시간 업데이트
    // const subscription = rankingService.subscribeToUpdates((updatedData) => {
    //   setData(updatedData)
    // })

    // return () => {
    //   subscription.unsubscribe()
    // }
  }, [type])

  return { data, loading, error }
}

export default useRanking


