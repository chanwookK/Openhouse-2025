import { Outlet } from 'react-router-dom'

function Layout({ children }) {
  // 스코어 보드 페이지에서도 레이아웃 제거
  return <Outlet />
}

export default Layout
