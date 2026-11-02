import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { api, formatTime, millis } from './api.ts'
import type { Post, PostList } from './api.ts'
import { Link, useGo } from './router.tsx'
import { clearSession, saveSession, token, userId } from './session.ts'

const COMMUNITIES = [
  { id: 1, name: '人民广场', latitude: 31.230416, longitude: 121.473701 },
  { id: 2, name: '天安门', latitude: 39.904211, longitude: 116.407395 },
]

export function LoginPage() {
  return <AuthPage mode="login" />
}

export function RegisterPage() {
  return <AuthPage mode="register" />
}

function AuthPage({ mode }: { mode: 'login' | 'register' }) {
  const go = useGo()
  const [usernameValue, setUsernameValue] = useState('')
  const [password, setPassword] = useState('')
  const [nickname, setNickname] = useState('')
  const [error, setError] = useState('')

  const submit = async (event: FormEvent) => {
    event.preventDefault()
    setError('')
    if (mode === 'register') {
      const created = await api<string>('/auth/register', {
        method: 'POST',
        body: JSON.stringify({
          username: usernameValue,
          passwordHash: password,
          nickname,
          cityCode: '310100',
          role: 0,
        }),
      })
      if (!created.ok) {
        setError(created.message)
        return
      }
    }
    const loggedIn = await api<string>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username: usernameValue, passwordHash: password }),
    })
    if (!loggedIn.ok || !loggedIn.data) {
      setError(loggedIn.message)
      return
    }
    saveSession(loggedIn.data, usernameValue)
    go('/')
  }

  return (
    <form className="card form" onSubmit={submit}>
      <h2>{mode === 'login' ? '登录' : '注册'}</h2>
      <label>
        用户名
        <input value={usernameValue} onChange={(event) => setUsernameValue(event.target.value)} required />
      </label>
      <label>
        密码
        <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} required />
      </label>
      {mode === 'register' && (
        <label>
          昵称
          <input value={nickname} onChange={(event) => setNickname(event.target.value)} required />
        </label>
      )}
      {error && <p className="error">{error}</p>}
      <button type="submit">{mode === 'login' ? '登录' : '注册并登录'}</button>
      <p className="muted">
        {mode === 'login' ? <Link to="/register">没有账号，去注册</Link> : <Link to="/login">已有账号，去登录</Link>}
      </p>
    </form>
  )
}

export function ListPage() {
  const go = useGo()
  const [communityId, setCommunityId] = useState(1)
  const [posts, setPosts] = useState<Post[]>([])
  const [cursor, setCursor] = useState<{ id?: number; time?: string | number }>({})
  const [error, setError] = useState('')

  const load = async (reset: boolean) => {
    if (!token()) {
      go('/login')
      return
    }
    const params = new URLSearchParams({ communityId: String(communityId) })
    if (!reset && cursor.id && cursor.time !== undefined) {
      const time = millis(cursor.time)
      if (time) {
        params.set('lastId', String(cursor.id))
        params.set('lastCreateTime', time)
      }
    }
    const result = await api<PostList>(`/posts/list?${params}`)
    if (result.status === 401) {
      clearSession()
      go('/login')
      return
    }
    if (!result.ok || !result.data) {
      setError(result.message)
      return
    }
    setError('')
    const incoming = result.data.postList ?? []
    setPosts((current) => (reset ? incoming : current.concat(incoming)))
    setCursor({ id: result.data.lastId, time: result.data.lastCreateTime })
  }

  useEffect(() => {
    setPosts([])
    setCursor({})
    void load(true)
    // community change reloads the first page
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [communityId])

  return (
    <section>
      <div className="row">
        <h2>小区帖子</h2>
        <select value={communityId} onChange={(event) => setCommunityId(Number(event.target.value))}>
          {COMMUNITIES.map((item) => (
            <option key={item.id} value={item.id}>
              {item.name}
            </option>
          ))}
        </select>
      </div>
      {error && <p className="error">{error}</p>}
      <PostCards posts={posts} />
      <button type="button" onClick={() => void load(false)} disabled={!cursor.id}>
        下一页
      </button>
    </section>
  )
}

export function NearbyPage() {
  const [latitude, setLatitude] = useState(String(COMMUNITIES[0].latitude))
  const [longitude, setLongitude] = useState(String(COMMUNITIES[0].longitude))
  const [posts, setPosts] = useState<Post[]>([])
  const [cursor, setCursor] = useState<{ id?: number; time?: string | number }>({})
  const [error, setError] = useState('')

  const load = async (reset: boolean) => {
    const params = new URLSearchParams({ latitude, longitude })
    if (!reset && cursor.id && cursor.time !== undefined) {
      const time = millis(cursor.time)
      if (time) {
        params.set('lastId', String(cursor.id))
        params.set('lastCreateTime', time)
      }
    }
    const result = await api<PostList>(`/posts/nearby?${params}`)
    if (!result.ok || !result.data) {
      setError(result.message)
      return
    }
    setError('')
    const incoming = result.data.postList ?? []
    setPosts((current) => (reset ? incoming : current.concat(incoming)))
    setCursor({ id: result.data.lastId, time: result.data.lastCreateTime })
  }

  return (
    <section>
      <h2>附近</h2>
      <form
        className="row"
        onSubmit={(event) => {
          event.preventDefault()
          setCursor({})
          void load(true)
        }}
      >
        <input value={latitude} onChange={(event) => setLatitude(event.target.value)} aria-label="纬度" />
        <input value={longitude} onChange={(event) => setLongitude(event.target.value)} aria-label="经度" />
        <button type="submit">查看</button>
      </form>
      {posts.length === 0 && !error && <p className="muted">填好经纬度后查看附近的帖。</p>}
      {error && <p className="error">{error}</p>}
      <PostCards posts={posts} />
    </section>
  )
}

function PostCards({ posts }: { posts: Post[] }) {
  if (posts.length === 0) {
    return <p className="muted">这里还没有帖子。</p>
  }
  return (
    <div className="stack">
      {posts.map((post) => (
        <article key={post.id} className="card">
          <h3>
            <Link to={`/posts/${post.id}`}>{post.title}</Link>
          </h3>
          <p>{post.content}</p>
          <p className="muted">
            {post.nickname || `作者 ${post.userId ?? ''}`} · {formatTime(post.createTime)}
          </p>
          <p className="counts">
            浏览 {post.viewCount ?? 0} · 赞 {post.likeCount ?? 0} · 评论 {post.commentCount ?? 0} · 藏 {post.favoriteCount ?? 0}
          </p>
        </article>
      ))}
    </div>
  )
}

export function DetailPage({ id }: { id: string }) {
  const go = useGo()
  const [post, setPost] = useState<Post | null>(null)
  const [comments, setComments] = useState<CommentItem[]>([])
  const [content, setContent] = useState('')
  const [message, setMessage] = useState('')

  const load = async () => {
    const result = await api<Post>(`/posts/${id}`)
    if (!result.ok || !result.data) {
      setMessage(result.message)
      setPost(null)
      return
    }
    setPost(result.data)
    const commentResult = await api<CommentItem[]>(`/posts/${id}/comments`)
    setComments(commentResult.ok && commentResult.data ? commentResult.data : [])
  }

  useEffect(() => {
    void load()
  }, [id])

  const act = async (path: string, method: string) => {
    if (!token()) {
      go('/login')
      return
    }
    const result = await api<string>(path, { method })
    setMessage(result.message)
    await load()
  }

  const sendComment = async (event: FormEvent) => {
    event.preventDefault()
    if (!token()) {
      go('/login')
      return
    }
    const result = await api<string>(`/posts/${id}/comments`, {
      method: 'POST',
      body: JSON.stringify({ content }),
    })
    setMessage(result.message)
    if (result.ok) {
      setContent('')
      await load()
    }
  }

  const remove = async () => {
    const result = await api<string>(`/posts/${id}`, { method: 'DELETE' })
    if (result.ok) {
      go('/')
      return
    }
    setMessage(result.message)
  }

  if (!post) {
    return <p className="error">{message || '正在加载…'}</p>
  }

  const mine = userId() !== null && userId() === String(post.userId)

  return (
    <article className="card">
      <h2>{post.title}</h2>
      <p>{post.content}</p>
      <p className="muted">
        {post.nickname || `作者 ${post.userId ?? ''}`} · {formatTime(post.createTime)}
      </p>
      <p className="counts">
        浏览 {post.viewCount ?? 0} · 赞 {post.likeCount ?? 0} · 评论 {post.commentCount ?? 0} · 藏 {post.favoriteCount ?? 0}
      </p>
      <div className="row">
        <button type="button" onClick={() => void act(`/posts/${id}/likes`, 'POST')}>点赞</button>
        <button type="button" onClick={() => void act(`/posts/${id}/likes`, 'DELETE')}>取消赞</button>
        <button type="button" onClick={() => void act(`/posts/${id}/favorites`, 'POST')}>收藏</button>
        <button type="button" onClick={() => void act(`/posts/${id}/favorites`, 'DELETE')}>取消收藏</button>
        {mine && <Link to={`/posts/${id}/edit`}>编辑</Link>}
        {mine && (
          <button type="button" onClick={() => void remove()}>
            删除
          </button>
        )}
      </div>
      {message && <p className="muted">{message}</p>}
      <h3>评论</h3>
      {comments.length === 0 && <p className="muted">还没有评论。</p>}
      <div className="stack">
        {comments.map((item) => (
          <p key={item.id}>
            {item.content}
            <span className="muted"> · {item.nickname || `用户 ${item.userId ?? item.authorId ?? ''}`}</span>
          </p>
        ))}
      </div>
      <form className="form" onSubmit={sendComment}>
        <textarea value={content} onChange={(event) => setContent(event.target.value)} placeholder="写一条评论" required />
        <button type="submit">发表</button>
      </form>
    </article>
  )
}

type CommentItem = {
  id: number
  content: string
  userId?: number
  authorId?: number
  nickname?: string
}

export function EditorPage({ id }: { id?: string }) {
  const go = useGo()
  const [communityId, setCommunityId] = useState(1)
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token()) {
      go('/login')
      return
    }
    if (!id) {
      return
    }
    void api<Post>(`/posts/${id}`).then((result) => {
      if (!result.ok || !result.data) {
        setError(result.message)
        return
      }
      setTitle(result.data.title)
      setContent(result.data.content)
      if (result.data.communityId) {
        setCommunityId(Number(result.data.communityId))
      }
    })
  }, [id, go])

  const submit = async (event: FormEvent) => {
    event.preventDefault()
    const place = COMMUNITIES.find((item) => item.id === communityId) ?? COMMUNITIES[0]
    const result = id
      ? await api<string>(`/posts/${id}`, {
          method: 'PUT',
          body: JSON.stringify({ title, content }),
        })
      : await api<string>('/posts/post', {
          method: 'POST',
          body: JSON.stringify({
            communityId,
            title,
            content,
            latitude: place.latitude,
            longitude: place.longitude,
          }),
        })
    if (!result.ok) {
      setError(result.message)
      return
    }
    go(id ? `/posts/${id}` : '/')
  }

  return (
    <form className="card form" onSubmit={submit}>
      <h2>{id ? '编辑帖子' : '发帖'}</h2>
      {!id && (
        <label>
          小区
          <select value={communityId} onChange={(event) => setCommunityId(Number(event.target.value))}>
            {COMMUNITIES.map((item) => (
              <option key={item.id} value={item.id}>
                {item.name}
              </option>
            ))}
          </select>
        </label>
      )}
      <label>
        标题
        <input value={title} onChange={(event) => setTitle(event.target.value)} required />
      </label>
      <label>
        正文
        <textarea value={content} onChange={(event) => setContent(event.target.value)} required />
      </label>
      {error && <p className="error">{error}</p>}
      <button type="submit">{id ? '保存' : '发布'}</button>
    </form>
  )
}

type HotItem = {
  postId?: number
  id?: number
  title?: string
  score?: number
}

export function HotPage() {
  const [items, setItems] = useState<HotItem[]>([])
  const [message, setMessage] = useState('正在加载热榜…')

  useEffect(() => {
    void api<HotItem[]>('/ranks/hot').then((result) => {
      if (!result.ok || !result.data) {
        setMessage(result.message)
        setItems([])
        return
      }
      setMessage('')
      setItems(result.data)
    })
  }, [])

  return (
    <section>
      <h2>热榜</h2>
      {message && <p className="muted">{message}</p>}
      <ol className="stack">
        {items.map((item, index) => {
          const id = item.postId ?? item.id
          return (
            <li key={id ?? index}>
              {id ? <Link to={`/posts/${id}`}>{item.title || `帖子 ${id}`}</Link> : item.title} · {item.score ?? 0} 分
            </li>
          )
        })}
      </ol>
    </section>
  )
}

type Notice = {
  id: number
  content: string
  readFlag?: number
}

export function NoticePage() {
  const go = useGo()
  const [items, setItems] = useState<Notice[]>([])
  const [message, setMessage] = useState('')

  const load = async () => {
    if (!token()) {
      go('/login')
      return
    }
    const result = await api<Notice[]>('/notifications')
    if (!result.ok || !result.data) {
      setMessage(result.message)
      return
    }
    setMessage('')
    setItems(result.data)
  }

  useEffect(() => {
    void load()
  }, [])

  const markRead = async (id: number) => {
    const result = await api<string>(`/notifications/${id}/read`, { method: 'POST' })
    setMessage(result.message)
    await load()
  }

  return (
    <section>
      <h2>通知</h2>
      {message && <p className="muted">{message}</p>}
      {items.length === 0 && <p className="muted">还没有通知。</p>}
      <div className="stack">
        {items.map((item) => (
          <article key={item.id} className="card row">
            <span>{item.content}</span>
            {item.readFlag ? (
              <span className="muted">已读</span>
            ) : (
              <button type="button" onClick={() => void markRead(item.id)}>
                标为已读
              </button>
            )}
          </article>
        ))}
      </div>
    </section>
  )
}
