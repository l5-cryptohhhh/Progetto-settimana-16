import { useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router'
import { ImagesIcon, PlusIcon, StackIcon, XIcon } from '@phosphor-icons/react'
import { useAuth } from '../auth/AuthContext.jsx'
import Documents from '../components/Documents.jsx'
import PostCard from '../components/PostCard.jsx'
import { Avatar, EmptyState, ErrorNote, Modal, Photo, Segmented, Spinner, Viewfinder } from '../components/ui.jsx'
import { photoUrl } from '../lib/api.js'
import { formatDate } from '../lib/format.js'
import { usePagedPosts } from '../lib/usePagedPosts.js'
import './profile.css'

const SECTIONS = [
  { value: 'post', label: 'Post' },
  { value: 'documenti', label: 'Documenti' },
]

export default function ProfilePage() {
  const { id } = useParams()
  const { user } = useAuth()
  const userId = id ? Number(id) : user.id
  // key: cambiando utente lo stato del profilo riparte da zero
  return <Profile key={userId} userId={userId} me={userId === user.id ? user : null} />
}

function Profile({ userId, me }) {
  const [params, setParams] = useSearchParams()
  const section = me && params.get('sezione') === 'documenti' ? 'documenti' : 'post'
  const { posts, loading, error, total, hasMore, loadMore, retry, remove } = usePagedPosts(`/api/users/${userId}/posts`, 12)
  const [openPost, setOpenPost] = useState(null)
  const username = me?.username ?? posts[0]?.author.username

  return (
    <div className="profile">
      <header className="profile-head">
        {username ? (
          <span className="ring ring-lg">
            <Avatar name={username} size={96} />
          </span>
        ) : (
          <span className="skeleton skeleton-avatar-lg" />
        )}
        <div className="profile-info">
          <h1>{username ?? 'Profilo'}</h1>
          {me && <p className="profile-meta">{me.email}</p>}
          {me?.createdAt && (
            <p className="profile-meta">Su Scatto da {formatDate(me.createdAt, { month: 'long', year: 'numeric' })}</p>
          )}
          <p className="profile-stats">
            <strong>{loading && !posts.length ? '…' : total}</strong> post
          </p>
        </div>
      </header>

      {me && (
        <Segmented
          label="Sezione del profilo"
          options={SECTIONS}
          value={section}
          onChange={(next) => setParams(next === 'documenti' ? { sezione: next } : {}, { replace: true })}
        />
      )}

      {section === 'documenti' ? (
        <Documents />
      ) : (
        <section aria-label="Post" className="profile-posts">
          {error && (
            <ErrorNote error={error}>
              <button type="button" className="btn-link" onClick={retry}>
                Riprova
              </button>
            </ErrorNote>
          )}

          {posts.length > 0 && (
            <ul className="grid">
              {posts.map((post) => (
                <li key={post.id}>
                  <button
                    type="button"
                    className="grid-item reveal-host"
                    onClick={() => setOpenPost(post)}
                    aria-label={`Apri il post del ${formatDate(post.createdAt)}${post.caption ? `: ${post.caption.slice(0, 60)}` : ''}`}
                  >
                    <Photo src={photoUrl(post.photos[0].url)} alt="" loading="lazy" decoding="async" />
                    {post.photos.length > 1 && <StackIcon size={20} weight="fill" className="grid-badge" aria-hidden="true" />}
                    <Viewfinder dot reveal />
                  </button>
                </li>
              ))}
            </ul>
          )}

          {loading && posts.length === 0 && (
            <ul className="grid" aria-hidden="true">
              {Array.from({ length: 6 }, (_, i) => (
                <li key={i} className="skeleton skeleton-tile" />
              ))}
            </ul>
          )}

          {!loading && !error && posts.length === 0 && (
            <EmptyState
              icon={<ImagesIcon size={36} aria-hidden="true" />}
              title="Nessun post"
              action={
                me && (
                  <Link to="/new" className="btn btn-primary">
                    <PlusIcon size={18} weight="bold" aria-hidden="true" />
                    Nuovo post
                  </Link>
                )
              }
            >
              {me ? 'Le foto che pubblichi appariranno qui, in griglia.' : 'Questo utente non ha ancora pubblicato foto.'}
            </EmptyState>
          )}

          {hasMore && !error && (
            <button type="button" className="btn btn-secondary load-more" onClick={loadMore} disabled={loading}>
              {loading && <Spinner />}
              Carica altri
            </button>
          )}
        </section>
      )}

      <Modal open={Boolean(openPost)} onClose={() => setOpenPost(null)} label="Post" className="modal-post">
        {openPost && (
          <>
            <div className="modal-bar">
              <button type="button" className="icon-btn" onClick={() => setOpenPost(null)} aria-label="Chiudi">
                <XIcon size={20} />
              </button>
            </div>
            <PostCard
              post={openPost}
              onDeleted={(postId) => {
                remove(postId)
                setOpenPost(null)
              }}
            />
          </>
        )}
      </Modal>
    </div>
  )
}
