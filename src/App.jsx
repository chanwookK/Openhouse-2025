import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Ranking from './pages/Ranking'
import ScoreBoard from './pages/ScoreBoard'
import Manager from './pages/Manager'
import Layout from './components/Layout'

import './App.css'

function App() {
  return (
    <Router>
      <Routes>
        {/* Main routes with Layout */}
        <Route path="/" element={<Layout />}>
          <Route index element={<Ranking />} />
          <Route path="scoreboard" element={<ScoreBoard />} />
          <Route path="manager" element={<Manager />} />
        </Route>
        
      </Routes>
    </Router>
  )
}

export default App
