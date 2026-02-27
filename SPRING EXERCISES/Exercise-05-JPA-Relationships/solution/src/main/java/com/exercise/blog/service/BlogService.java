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
        return authorRepository.findAll();
    }

    @Transactional
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    public List<Post> getPostsByAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author not found: " + authorId));
        return author.getPosts();
    }

    public Page<Post> getAllPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post createPost(Long authorId, Post post) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author not found: " + authorId));
        post.setAuthor(author);
        return postRepository.save(post);
    }

    @Transactional
    public Post addTagToPost(Long postId, String tagName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));
        Tag tag = tagRepository.findByName(tagName)
                .orElseGet(() -> tagRepository.save(new Tag(tagName)));
        post.getTags().add(tag);
        return postRepository.save(post);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public List<Post> getPostsByTag(Long tagId) {
        return postRepository.findByTagsId(tagId);
    }
}
