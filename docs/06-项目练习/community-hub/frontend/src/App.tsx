import { useEffect, useState } from 'react'
import { HotPage, DetailPage, EditorPage, ListPage, LoginPage, NearbyPage, NoticePage, RegisterPage } from './pages.tsx'
import { Link, Router, useGo, usePath } from './router.tsx'
import { clearSession, token, username } from './session.ts'

function Shell() {
  const path = usePath()
  const go = useGo()
  const [name, setName] = useState(username())

  useEffect(() => {
    const sync = () => setName(username())
    window.addEventListener('community-session', sync)
    return () => window.removeEventListener('community-session', sync)
  }, [])

  const page = match(path)

  return (
    <>
      <header className="nav">
        <Link to="/">社区</Link>
        <Link to="/nearby">附近</Link>
        <Link to="/posts/new">发帖</Link>
        <Link to="/hot">热榜</Link>
        <Link to="/notifications">通知</Link>
        {name ? (
          <button
            type="button"
            className="linkish"
            onClick={() => {
              clearSession()
              go('/login')
            }}
          >
            {name} 退出
          </button>
        ) : (
          <Link to="/login">登录</Link>
        )}
      </header>
      <main>{token() || page.public ? page.node : <LoginPage />}</main>
    </>
  )
}

function match(path: string) {
  if (path === '/login') {
    return { public: true, node: <LoginPage /> }
  }
  if (path === '/register') {
    return { public: true, node: <RegisterPage /> }
  }
  if (path === '/nearby') {
    return { public: true, node: <NearbyPage /> }
  }
  if (path === '/hot') {
    return { public: true, node: <HotPage /> }
  }
  if (path === '/posts/new') {
    return { public: false, node: <EditorPage /> }
  }
  const edit = path.match(/^\/posts\/(\d+)\/edit$/)
  if (edit) {
    return { public: false, node: <EditorPage id={edit[1]} /> }
  }
  const detail = path.match(/^\/posts\/(\d+)$/)
  if (detail) {
    return { public: true, node: <DetailPage id={detail[1]} /> }
  }
  if (path === '/notifications') {
    return { public: false, node: <NoticePage /> }
  }
  return { public: false, node: <ListPage /> }
}

export default function App() {
  return (
    <Router>
      <Shell />
    </Router>
  )
}
