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

        // Create authors
        Author fowler = new Author("Martin Fowler");
        Author martin = new Author("Robert C. Martin");

        // Create books and assign to authors (sync both sides)
        Book refactoring = new Book("Refactoring", 1999);
        Book poeaa = new Book("Patterns of Enterprise Application Architecture", 2002);
        Book cleanCode = new Book("Clean Code", 2008);

        fowler.addBook(refactoring);
        fowler.addBook(poeaa);
        martin.addBook(cleanCode);

        // Cascade saves the books
        authorRepository.save(fowler);
        authorRepository.save(martin);

        // Create tags
        Tag bestPractices = new Tag("Best Practices");
        Tag architecture = new Tag("Architecture");

        // Assign tags from the owning side (Book)
        refactoring.getTags().add(bestPractices);
        cleanCode.getTags().add(bestPractices);
        poeaa.getTags().add(architecture);

        // Save tags — books already persisted, join table rows inserted
        tagRepository.save(bestPractices);
        tagRepository.save(architecture);

        // --- Print authors and their books ---
        System.out.println("--- Authors and their books ---");
        authorRepository.findAll().forEach(a -> {
            System.out.println("Author: " + a.getName());
            a.getBooks().forEach(b -> System.out.println("  " + b));
        });

        // --- Print tags and their books ---
        System.out.println("\n--- Tags and their books ---");
        tagRepository.findAll().forEach(t -> {
            System.out.println("Tag: " + t.getName());
            t.getBooks().forEach(b -> System.out.println("  " + b.getTitle()));
        });
    }
}
