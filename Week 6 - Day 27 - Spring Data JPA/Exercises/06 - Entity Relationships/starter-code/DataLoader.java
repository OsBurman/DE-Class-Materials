package com.library;

import com.library.model.Author;
import com.library.model.Book;
import com.library.model.Tag;
import com.library.repository.AuthorRepository;
import com.library.repository.TagRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;

    public DataLoader(AuthorRepository authorRepository, TagRepository tagRepository) {
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("=== Entity Relationships Demo ===\n");

        // TODO: Create 2 Authors
        //         Author author1 = new Author("Martin Fowler");
        //         Author author2 = new Author("Robert C. Martin");

        // TODO: Create 3 Books and assign each to an author using author.addBook(book)
        //         new Book("Refactoring", 1999)
        //         new Book("Patterns of Enterprise Application Architecture", 2002)
        //         new Book("Clean Code", 2008)

        // TODO: Save both authors — CascadeType.ALL will persist the books automatically

        // TODO: Create 2 Tags
        //         new Tag("Best Practices")
        //         new Tag("Architecture")

        // TODO: Assign tags to books:
        //         book1.getTags().add(bestPractices);
        //         book3.getTags().add(bestPractices);
        //         book2.getTags().add(architecture);

        // TODO: Save the tags

        // --- Print authors and their books ---
        System.out.println("--- Authors and their books ---");
        // TODO: For each author, print author name and each book in author.getBooks()

        // --- Print tags and their books ---
        System.out.println("\n--- Tags and their books ---");
        // TODO: For each tag, print tag name and each book title in tag.getBooks()
    }
}
