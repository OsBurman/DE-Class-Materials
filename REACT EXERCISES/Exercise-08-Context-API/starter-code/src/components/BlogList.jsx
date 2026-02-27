import BlogPost from './BlogPost'

function BlogList({ posts }) {
  return (
    <div className="blog-list">
      {posts.map(post => <BlogPost key={post.id} post={post} />)}
    </div>
  )
}

export default BlogList
