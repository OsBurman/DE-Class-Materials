package com.library;

// TODO: Import the following Lombok annotations:
//       @Service  (from org.springframework.stereotype)
//       @RequiredArgsConstructor
//       @Slf4j

/**
 * Service layer for managing books.
 *
 * TODO:
 *   1. Add @Service           — marks this as a Spring-managed service bean
 *   2. Add @RequiredArgsConstructor — generates a constructor for all 'final' fields;
 *                              Spring will use this constructor to inject BookRepository
 *                              without an explicit @Autowired
 *   3. Add @Slf4j             — injects:
 *                              private static final Logger log = LoggerFactory.getLogger(BookCatalogService.class);
 *                              You can then call log.info(...) directly.
 *   4. Make 'bookRepository' a 'private final' field so Lombok generates the injection constructor.
 */
public class BookCatalogService {

    // TODO: Declare a 'private final BookRepository bookRepository' field here

    /**
     * Creates a new book from the given request and logs the operation.
     *
     * @param request the DTO containing title and authorId
     * @return a new Book instance built from the request
     *
     * TODO:
     *   - Log an INFO message: "Creating book: {}" where {} is request.getTitle()
     *   - Return: new Book(0, request.getTitle(), request.getAuthorId())
     */
    public Book createBook(BookRequest request) {
        // TODO: implement
        return null;
    }
}
