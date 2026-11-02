const TOKEN = 'community-token'
const NAME = 'community-username'

export function token() {
  return localStorage.getItem(TOKEN)
}

export function username() {
  return localStorage.getItem(NAME)
}

export function userId() {
  const current = token()
  if (!current) {
    return null
  }
  const payload = current.split('.')[1]
  if (!payload) {
    return null
  }
  try {
    const json = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/'))) as { sub?: string }
    return json.sub ? String(json.sub) : null
  } catch {
    return null
  }
}

export function saveSession(nextToken: string, name: string) {
  localStorage.setItem(TOKEN, nextToken)
  localStorage.setItem(NAME, name)
  window.dispatchEvent(new Event('community-session'))
}

export function clearSession() {
  localStorage.removeItem(TOKEN)
  localStorage.removeItem(NAME)
  window.dispatchEvent(new Event('community-session'))
}
