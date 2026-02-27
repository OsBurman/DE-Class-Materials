// BlogPost receives a single post object as a prop.
// The theming is handled entirely through CSS custom properties — no context needed here!
// This shows how CSS variables mean you DON'T have to thread theme through every component.

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
