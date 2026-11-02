import { token } from './session.ts'

export type Post = {
  id: number
  communityId?: number
  title: string
  content: string
  userId?: number
  latitude?: number
  longitude?: number
  createTime?: string | number
  updateTime?: string | number
  nickname?: string
  viewCount?: number
  likeCount?: number
  commentCount?: number
  favoriteCount?: number
}

export type PostList = {
  postList: Post[]
  lastId?: number
  lastCreateTime?: string | number
}

type Body<T> = {
  code?: number
  message?: string
  data?: T
  error?: string
}

export type Result<T> = {
  ok: boolean
  status: number
  message: string
  data: T | null
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<Result<T>> {
  const headers = new Headers(options.headers)
  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  const current = token()
  if (current) {
    headers.set('Authorization', `Bearer ${current}`)
  }
  const base = import.meta.env.VITE_API_BASE || '/api'
  const response = await fetch(`${base}${path}`, { ...options, headers })
  const body = (await response.json().catch(() => ({}))) as Body<T>
  const code = body.code ?? response.status
  return {
    ok: response.ok && (body.code === undefined || body.code === 200),
    status: response.status === 200 && body.code && body.code !== 200 ? body.code : response.status || code,
    message: body.message || body.error || '请求失败',
    data: body.data ?? null,
  }
}

export function millis(value: string | number | undefined) {
  if (value === undefined || value === '') {
    return null
  }
  if (typeof value === 'number') {
    return String(value)
  }
  const parsed = Date.parse(value)
  return Number.isNaN(parsed) ? null : String(parsed)
}

export function formatTime(value: string | number | undefined) {
  if (value === undefined || value === '') {
    return ''
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return String(value)
  }
  return date.toLocaleString('zh-CN', { hour12: false })
}
