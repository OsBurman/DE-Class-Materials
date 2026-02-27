package com.exercise.blog.service;

import com.exercise.blog.entity.Author;
import com.exercise.blog.entity.Post;
import com.exercise.blog.entity.Tag;
import com.exercise.blog.repository.AuthorRepository;
import com.exercise.blog.repository.PostRepository;
import com.exercise.blog.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// TODO 16: Implement all methods below.
//          Use @Transactional on methods that modify data.
@Service
public class BlogService {

    private final AuthorRepository authorRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public BlogService(AuthorRepository authorRepository,
                       PostRepository postRepository,
                       TagRepository tagRepository) {
        this.authorRepository = authorRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public List<Author> getAllAuthors() {
        // your code here
        return null;
    }

    @Transactional
    public Author createAuthor(Author author) {
        // your code here
        return null;
    }

    public List<Post> getPostsByAuthor(Long authorId) {
        // Find author or throw NoSuchElementException
        // Return author.getPosts()
        // your code here
        return null;
    }

    public Page<Post> getAllPosts(int page, int size) {
        // Use PageRequest.of(page, size, Sort.by("createdAt").descending())
        // your code here
        return null;
    }

    public Optional<Post> getPostById(Long id) {
        // your code here
        return null;
    }

    @Transactional
    public Post createPost(Long authorId, Post post) {
        // Find author or throw NoSuchElementException("Author not found: " + authorId)
        // Set post.setAuthor(author)
        // Save and return
        // your code here
        return null;
    }

    @Transactional
    public Post addTagToPost(Long postId, String tagName) {
        // Find post or throw NoSuchElementException
        // Find existing tag by name, or create a new Tag(tagName) and save it
        // Add the tag to post.getTags()
        // Save post and return
        // your code here
        return null;
    }

    public List<Tag> getAllTags() {
        // your code here
        return null;
    }

    public List<Post> getPostsByTag(Long tagId) {
        // your code here
        return null;
    }
}
