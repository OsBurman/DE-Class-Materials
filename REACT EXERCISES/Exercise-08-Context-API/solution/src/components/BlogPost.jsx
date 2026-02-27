function BlogPost({ post }) {
  return (
    <article className="blog-post">
      <h2>{post.title}</h2>
      <div className="meta">By {post.author} · {post.date}</div>
      <p>{post.excerpt}</p>
    </article>
  )
}

export default BlogPost
