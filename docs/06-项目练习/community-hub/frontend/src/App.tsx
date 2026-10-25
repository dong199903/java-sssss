import { useEffect, useState } from 'react'

type Health = {
  code: number
  message: string
  data: { service: string }
}

function App() {
  const [text, setText] = useState('正在连接后端…')

  useEffect(() => {
    fetch(`${import.meta.env.VITE_API_BASE}/health`)
      .then((res) => {
        if (!res.ok) {
          throw new Error(String(res.status))
        }
        return res.json() as Promise<Health>
      })
      .then((body) => setText(`${body.data.service}：${body.message}`))
      .catch(() => setText('后端未启动，先在 backend 目录运行 mvn spring-boot:run'))
  }, [])

  return (
    <main>
      <h1>社区练习</h1>
      <p>{text}</p>
    </main>
  )
}

export default App
