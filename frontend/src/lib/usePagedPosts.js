import { useCallback, useEffect, useState } from 'react'
import { api } from './api.js'

/** Post paginati con "carica altri": usato dal feed e dal profilo. */
export function usePagedPosts(path, size = 10) {
  const [posts, setPosts] = useState([])
  const [meta, setMeta] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const load = useCallback(
    async (page) => {
      setLoading(true)
      setError(null)
      try {
        const data = await api(`${path}?page=${page}&size=${size}`)
        // ponytail: paginazione a offset, dopo un'eliminazione può saltare un post; serve un cursore lato API per evitarlo
        setPosts((prev) =>
          page === 0 ? data.content : [...prev, ...data.content.filter((post) => !prev.some((p) => p.id === post.id))],
        )
        setMeta(data)
      } catch (err) {
        setError(err)
      } finally {
        setLoading(false)
      }
    },
    [path, size],
  )

  useEffect(() => {
    load(0)
  }, [load])

  return {
    posts,
    loading,
    error,
    total: meta?.totalElements ?? 0,
    hasMore: meta ? meta.page + 1 < meta.totalPages : false,
    loadMore: () => load(meta.page + 1),
    retry: () => load(meta ? meta.page + 1 : 0),
    remove: (id) => {
      setPosts((prev) => prev.filter((post) => post.id !== id))
      setMeta((m) => m && { ...m, totalElements: m.totalElements - 1 })
    },
  }
}
