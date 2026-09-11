import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router'
import { CheckCircleIcon, ImagesIcon, PlusIcon } from '@phosphor-icons/react'
import { useAuth } from '../auth/AuthContext.jsx'
import PostCard, { PostSkeleton } from '../components/PostCard.jsx'
import { Avatar, EmptyState, ErrorNote, Spinner } from '../components/ui.jsx'
import { usePagedPosts } from '../lib/usePagedPosts.js'

const MAX_AUTHORS = 12

export default function FeedPage() {
  const { user } = useAuth()
  const { posts, loading, error, hasMore, loadMore, retry, remove } = usePagedPosts('/api/posts')
  const location = useLocation()
  const navigate = useNavigate()
  const [published, setPublished] = useState(Boolean(location.state?.published))

  // Conferma dopo la pubblicazione, poi pulisce lo stato della navigazione
  useEffect(() => {
    if (!published) return
    navigate('.', { replace: true, state: null })
    const timer = setTimeout(() => setPublished(false), 4000)
    return () => clearTimeout(timer)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  return (
    <section className="feed" aria-busy={loading}>
      <h1 className="sr-only">Feed</h1>

      <RecentAuthors me={user} posts={posts} loading={loading} />

      {published && (
        <p className="note note-success" role="status">
          <CheckCircleIcon size={20} weight="fill" aria-hidden="true" />
          Post pubblicato.
        </p>
      )}

      {(posts.length > 0 || loading) && (
        <div className="works">
          {posts.map((post) => (
            <PostCard key={post.id} post={post} onDeleted={remove} focusOnScroll />
          ))}
          {loading && posts.length === 0 && (
            <>
              <PostSkeleton />
              <PostSkeleton />
            </>
          )}
        </div>
      )}

      {!loading && !error && posts.length === 0 && (
        <EmptyState
          icon={<ImagesIcon size={36} aria-hidden="true" />}
          title="La parete è ancora vuota"
          action={
            <Link to="/new" className="btn btn-primary">
              <PlusIcon size={18} weight="bold" aria-hidden="true" />
              Nuovo post
            </Link>
          }
        >
          Scatta una foto o caricane qualcuna: sarà il primo post del feed.
        </EmptyState>
      )}

      {error && (
        <ErrorNote error={error}>
          <button type="button" className="btn-link" onClick={retry}>
            Riprova
          </button>
        </ErrorNote>
      )}

      {hasMore && !error && (
        <button type="button" className="btn btn-secondary load-more" onClick={loadMore} disabled={loading}>
          {loading && <Spinner />}
          Carica altri
        </button>
      )}
    </section>
  )
}

/**
 * Riga in stile storie del mockup, ma con dati veri: gli autori dei post caricati,
 * dal più recente, ognuno porta al proprio profilo. Il primo cerchio apre un nuovo post.
 */
function RecentAuthors({ me, posts, loading }) {
  const seen = new Set([me.id])
  const authors = []
  for (const { author } of posts) {
    if (seen.has(author.id)) continue
    seen.add(author.id)
    authors.push(author)
    if (authors.length === MAX_AUTHORS) break
  }

  return (
    <section className="authors" aria-labelledby="authors-title">
      <h2 id="authors-title" className="sr-only">
        Autori recenti
      </h2>
      <ul className="authors-row">
        <li>
          <Link to="/new" className="author">
            <span className="ring ring-muted">
              <Avatar name={me.username} size={60} />
              <span className="ring-plus">
                <PlusIcon size={12} weight="bold" aria-hidden="true" />
              </span>
            </span>
            <span className="author-name">Nuovo post</span>
          </Link>
        </li>
        {authors.map((author) => (
          <li key={author.id}>
            <Link to={`/users/${author.id}`} className="author">
              <span className="ring">
                <Avatar name={author.username} size={60} />
              </span>
              <span className="author-name">{author.username}</span>
            </Link>
          </li>
        ))}
        {loading &&
          posts.length === 0 &&
          Array.from({ length: 4 }, (_, i) => (
            <li key={i} aria-hidden="true">
              <span className="author">
                <span className="skeleton skeleton-ring" />
                <span className="skeleton skeleton-name" />
              </span>
            </li>
          ))}
      </ul>
    </section>
  )
}
