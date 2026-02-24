package com.library;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing books.
 *
 * @Service              — marks this class as a Spring-managed service bean;
 *                         component scanning (via @ComponentScan) will detect it.
 *
 * @RequiredArgsConstructor — Lombok generates:
 *                         public BookCatalogService(BookRepository bookRepository) {
 *                             this.bookRepository = bookRepository;
 *                         }
 *                         Because the field is 'final', Spring performs constructor
 *                         injection automatically — no @Autowired needed.
 *
 * @Slf4j                — Lombok injects:
 *                         private static final Logger log =
 *                             LoggerFactory.getLogger(BookCatalogService.class);
 *                         Allows calling log.info(...), log.debug(...) etc. directly.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookCatalogService {

    private final BookRepository bookRepository;

    /**
     * Creates a new book from the given request.
     *
     * @param request the DTO containing title and authorId
     * @return a new Book instance (id=0 indicates not yet persisted)
     */
    public Book createBook(BookRequest request) {
        log.info("Creating book: {}", request.getTitle());
        return new Book(0, request.getTitle(), request.getAuthorId());
    }
}
