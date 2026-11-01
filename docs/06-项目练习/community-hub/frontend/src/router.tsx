import { createContext, useContext, useEffect, useState } from 'react'
import type { ReactNode } from 'react'

const GoContext = createContext<(to: string) => void>(() => {})

export function Router({ children }: { children: ReactNode }) {
  const [path, setPath] = useState(window.location.pathname)

  useEffect(() => {
    const sync = () => setPath(window.location.pathname)
    window.addEventListener('popstate', sync)
    return () => window.removeEventListener('popstate', sync)
  }, [])

  const go = (to: string) => {
    window.history.pushState({}, '', to)
    setPath(window.location.pathname)
  }

  return (
    <GoContext.Provider value={go}>
      <PathContext.Provider value={path}>{children}</PathContext.Provider>
    </GoContext.Provider>
  )
}

const PathContext = createContext('/')

export function usePath() {
  return useContext(PathContext)
}

export function useGo() {
  return useContext(GoContext)
}

export function Link({ to, children }: { to: string; children: ReactNode }) {
  const go = useGo()
  return (
    <a
      href={to}
      onClick={(event) => {
        event.preventDefault()
        go(to)
      }}
    >
      {children}
    </a>
  )
}
