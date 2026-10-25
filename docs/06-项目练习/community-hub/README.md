# community-hub

06 项目练习的业务代码。需求与验收在上一级目录。前后端都在本目录。

| 目录 | 内容 |
|------|------|
| `backend` | Spring Boot 3.2.12，Java 21 |
| `frontend` | React + Vite + TypeScript |

练习文档里的前端写成 Vue 3。这个工程按当前要求使用 React。

## 后端

需要本机 JDK 21。在本目录执行：

```bash
./backend/start.sh
```

接口：`GET http://localhost:8080/api/health`，响应为 `code / message / data`。

## 前端

```bash
cd frontend
npm install
npm run dev
```

打开 http://localhost:5173 。开发时 `/api` 代理到 `http://localhost:8080`。
