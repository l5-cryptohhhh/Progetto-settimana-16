import { Navigate, Route, Routes } from 'react-router'
import Shell from './components/Shell.jsx'
import AuthPage from './pages/AuthPage.jsx'
import CreatePostPage from './pages/CreatePostPage.jsx'
import FeedPage from './pages/FeedPage.jsx'
import ProfilePage from './pages/ProfilePage.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<AuthPage key="login" mode="login" />} />
      <Route path="/register" element={<AuthPage key="register" mode="register" />} />
      {/* Pagine protette: Shell reindirizza al login se non c'è una sessione */}
      <Route element={<Shell />}>
        <Route index element={<FeedPage />} />
        <Route path="new" element={<CreatePostPage />} />
        <Route path="profile" element={<ProfilePage />} />
        <Route path="users/:id" element={<ProfilePage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
