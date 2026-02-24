package com.bookstore.dto;

// =============================================================================
// FILE: 05-dtos-and-mapping.java
//
// Covers:
//   1. What DTOs are and why we use them instead of exposing entities
//   2. Manual mapping (entity → DTO by hand)
//   3. MapStruct — annotation-processor based code generation
//   4. ModelMapper — reflection-based mapping (awareness level)
//   5. pom.xml dependency configuration for MapStruct
// =============================================================================

// =============================================================================
// SECTION 1: DTOs vs ENTITIES — WHY THEY EXIST
// =============================================================================
//
// ENTITY = represents a DATABASE ROW
//   - Tied to JPA/Hibernate annotations
//   - May contain relationships (lazy collections, proxies)
//   - May contain sensitive fields (password hash, internal IDs)
//   - Should never be sent directly over the wire
//
// DTO (Data Transfer Object) = represents WHAT THE CLIENT SEES
//   - Plain POJO — no JPA annotations
//   - Only contains fields the client needs
//   - Can reshape the data (combine fields, rename, add computed values)
//   - Decouples your API shape from your database schema
//
// THE PROBLEM WITH EXPOSING ENTITIES DIRECTLY:
//   1. Security: User entity has `passwordHash` field — exposed to client
//   2. N+1: returning a Book entity might lazily load all its Orders
//   3. Coupling: DB schema change breaks your API
//   4. Circular refs: Order → User → Orders (infinite JSON serialization)
//
// DTO TYPES BY CONVENTION:
//   BookDTO          → read response (what GET returns)
//   CreateBookRequest → POST body (what the client sends to create)
//   UpdateBookRequest → PUT body (what the client sends to update)
//   BookSummaryDTO   → lightweight list response (fewer fields)
// =============================================================================

import java.time.LocalDate;

// ─────────────────────────────────────────────────────────────────────────────
// Response DTO — what GET /api/v1/books/{id} returns
// ─────────────────────────────────────────────────────────────────────────────
class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private double price;
    private String genre;
    private boolean inStock;
    private String displayName;  // computed: "Title by Author" — doesn't exist in DB

    // Default constructor needed for Jackson deserialization
    public BookDTO() {}

    // Getters and setters
    public Long getId()              { return id; }
    public void setId(Long id)       { this.id = id; }
    public String getTitle()         { return title; }
    public void setTitle(String t)   { this.title = t; }
    public String getAuthor()        { return author; }
    public void setAuthor(String a)  { this.author = a; }
    public String getIsbn()          { return isbn; }
    public void setIsbn(String i)    { this.isbn = i; }
    public double getPrice()         { return price; }
    public void setPrice(double p)   { this.price = p; }
    public String getGenre()         { return genre; }
    public void setGenre(String g)   { this.genre = g; }
    public boolean isInStock()       { return inStock; }
    public void setInStock(boolean s){ this.inStock = s; }
    public String getDisplayName()   { return displayName; }
    public void setDisplayName(String d){ this.displayName = d; }
}

// ─────────────────────────────────────────────────────────────────────────────
// Request DTO — what POST /api/v1/books expects in the request body
// ─────────────────────────────────────────────────────────────────────────────
class CreateBookRequest {
    private String title;
    private String author;
    private String isbn;
    private Double price;
    private LocalDate publishedDate;
    private String genre;

    // Getters and setters
    public String getTitle()              { return title; }
    public void setTitle(String t)        { this.title = t; }
    public String getAuthor()             { return author; }
    public void setAuthor(String a)       { this.author = a; }
    public String getIsbn()               { return isbn; }
    public void setIsbn(String i)         { this.isbn = i; }
    public Double getPrice()              { return price; }
    public void setPrice(Double p)        { this.price = p; }
    public LocalDate getPublishedDate()   { return publishedDate; }
    public void setPublishedDate(LocalDate d){ this.publishedDate = d; }
    public String getGenre()              { return genre; }
    public void setGenre(String g)        { this.genre = g; }
}

// ─────────────────────────────────────────────────────────────────────────────
// Lightweight summary DTO for list endpoints
// GET /api/v1/books → returns List<BookSummaryDTO> (smaller payload, no ISBN)
// ─────────────────────────────────────────────────────────────────────────────
class BookSummaryDTO {
    private Long id;
    private String title;
    private String author;
    private double price;
    private boolean inStock;

    public BookSummaryDTO() {}

    public BookSummaryDTO(Long id, String title, String author,
                          double price, boolean inStock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.inStock = inStock;
    }

    // Getters
    public Long getId()      { return id; }
    public String getTitle() { return title; }
    public String getAuthor(){ return author; }
    public double getPrice() { return price; }
    public boolean isInStock(){ return inStock; }
}

// =============================================================================
// SECTION 2: MANUAL MAPPING
// =============================================================================
// The simplest approach: write the mapping code yourself.
//
// PROS:
//   ✅ Completely explicit — you see exactly what maps where
//   ✅ No additional dependencies
//   ✅ Easy to add computed fields (displayName = title + " by " + author)
//
// CONS:
//   ❌ Tedious for large objects (10+ fields)
//   ❌ Easy to forget a field when a new one is added
//   ❌ No compile-time safety (if you miss a field, no warning)
// =============================================================================

class ManualBookMapper {

    // Entity → response DTO
    public static BookDTO toDTO(com.bookstore.model.Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPrice(book.getPrice().doubleValue());
        dto.setGenre(book.getGenre());
        dto.setInStock(book.isInStock());
        // Computed field — doesn't exist in the entity
        dto.setDisplayName(book.getTitle() + " by " + book.getAuthor());
        return dto;
    }

    // Request DTO → Entity
    public static com.bookstore.model.Book toEntity(CreateBookRequest request) {
        return new com.bookstore.model.Book(
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                java.math.BigDecimal.valueOf(request.getPrice()),
                request.getPublishedDate(),
                request.getGenre()
        );
    }

    // Summary DTO — only a subset of fields
    public static BookSummaryDTO toSummaryDTO(com.bookstore.model.Book book) {
        return new BookSummaryDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice().doubleValue(),
                book.isInStock()
        );
    }
}

// =============================================================================
// SECTION 3: MAPSTRUCT — ANNOTATION-PROCESSOR BASED MAPPING
// =============================================================================
// MapStruct is a Java annotation processor that generates the mapping code at
// COMPILE TIME. You write a mapper interface; MapStruct generates the
// implementation automatically.
//
// PROS:
//   ✅ Zero runtime overhead — plain Java, no reflection
//   ✅ Compile-time safety — missing mappings cause compilation warnings/errors
//   ✅ Works with Lombok, Spring, CDI
//   ✅ Supports nested mappings, type conversions, custom expressions
//
// CONS:
//   ❌ Requires annotation processor setup in pom.xml (see below)
//   ❌ Slightly more setup overhead vs manual mapping for tiny objects
//
// MAPSTRUCT vs MODELMAPPER:
//   MapStruct   → code generation at compile time; fast; explicit
//   ModelMapper → reflection at runtime; slower; more "magic"; fragile for complex types
//   RECOMMENDATION: Prefer MapStruct in production. ModelMapper is convenient
//                   for quick prototypes but can have subtle runtime bugs.
// =============================================================================

/*
 * ─── pom.xml CONFIGURATION FOR MAPSTRUCT ────────────────────────────────────
 *
 * <properties>
 *     <mapstruct.version>1.5.5.Final</mapstruct.version>
 * </properties>
 *
 * <dependencies>
 *     <!-- MapStruct annotation (interfaces, @Mapper) -->
 *     <dependency>
 *         <groupId>org.mapstruct</groupId>
 *         <artifactId>mapstruct</artifactId>
 *         <version>${mapstruct.version}</version>
 *     </dependency>
 * </dependencies>
 *
 * <build>
 *     <plugins>
 *         <plugin>
 *             <groupId>org.apache.maven.plugins</groupId>
 *             <artifactId>maven-compiler-plugin</artifactId>
 *             <configuration>
 *                 <annotationProcessorPaths>
 *                     <!-- MapStruct processor generates BookMapperImpl.java -->
 *                     <path>
 *                         <groupId>org.mapstruct</groupId>
 *                         <artifactId>mapstruct-processor</artifactId>
 *                         <version>${mapstruct.version}</version>
 *                     </path>
 *                     <!-- If you also use Lombok, add it BEFORE mapstruct-processor -->
 *                     <path>
 *                         <groupId>org.projectlombok</groupId>
 *                         <artifactId>lombok</artifactId>
 *                         <version>${lombok.version}</version>
 *                     </path>
 *                     <path>
 *                         <groupId>org.projectlombok</groupId>
 *                         <artifactId>lombok-mapstruct-binding</artifactId>
 *                         <version>0.2.0</version>
 *                     </path>
 *                 </annotationProcessorPaths>
 *             </configuration>
 *         </plugin>
 *     </plugins>
 * </build>
 */

import org.mapstruct.*;

// The @Mapper annotation tells MapStruct to generate an implementation.
//   componentModel = "spring" → the generated BookMapperImpl is a @Component
//   and can be @Autowired into your services.
@Mapper(componentModel = "spring")
interface BookMapper {

    // ── Simple field-to-field mapping ─────────────────────────────────────────
    // MapStruct matches fields by NAME automatically.
    // If source and target field names match, no configuration needed.
    BookDTO bookToBookDTO(com.bookstore.model.Book book);

    // ── Rename a field ─────────────────────────────────────────────────────────
    // source entity field 'isbn' → target DTO field 'bookIsbn' (hypothetical)
    // @Mapping(source = "isbn", target = "bookIsbn")
    // BookDTO bookToBookDTOWithRename(com.bookstore.model.Book book);

    // ── Ignore a field in the target ──────────────────────────────────────────
    // Target field 'displayName' has no corresponding entity field.
    // Tell MapStruct to ignore it (we'll set it in an @AfterMapping method).
    @Mapping(target = "displayName", ignore = true)
    BookDTO bookToBookDTOWithDisplayName(com.bookstore.model.Book book);

    // After the main mapping, run this method to set the computed field
    @AfterMapping
    default void setDisplayName(com.bookstore.model.Book book,
                                @MappingTarget BookDTO dto) {
        dto.setDisplayName(book.getTitle() + " by " + book.getAuthor());
    }

    // ── Map price: BigDecimal → double ────────────────────────────────────────
    // MapStruct handles common type conversions (BigDecimal → double) automatically.
    // For custom conversions, use expression or qualifiedByName.
    @Mapping(source = "price", target = "price",
             numberFormat = "#.##")  // format as 2 decimal places
    BookDTO bookToBookDTOFormatted(com.bookstore.model.Book book);

    // ── List mapping ──────────────────────────────────────────────────────────
    // MapStruct generates the list mapping by calling bookToBookDTO() per element.
    java.util.List<BookDTO> booksToBookDTOs(java.util.List<com.bookstore.model.Book> books);

    // ── Reverse mapping: DTO → Entity ─────────────────────────────────────────
    // Note: the generated id from the DB must be ignored when creating a new entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inStock", constant = "true")  // new books default to in-stock
    com.bookstore.model.Book createRequestToBook(CreateBookRequest request);
}

// =============================================================================
// SECTION 4: MODELMAPPER — AWARENESS OVERVIEW (not used in production here)
// =============================================================================
// ModelMapper works by reflection at runtime. It inspects field names and
// types and maps them automatically.
//
// Usage:
//   ModelMapper modelMapper = new ModelMapper();
//   BookDTO dto = modelMapper.map(book, BookDTO.class);
//
// Pros:  Zero interface writing; zero processor config
// Cons:
//   - Runtime exceptions if types don't match (not caught at compile time)
//   - Slow for complex type hierarchies (reflection overhead)
//   - Hard to debug when mapping doesn't work as expected
//   - Renaming a field causes silent data loss (null in target) not a compile error
//
// pom.xml dependency (awareness only):
//
// <dependency>
//     <groupId>org.modelmapper</groupId>
//     <artifactId>modelmapper</artifactId>
//     <version>3.2.0</version>
// </dependency>
//
// RECOMMENDATION:
//   - New projects: use MapStruct (explicit, compile-time safe, zero runtime overhead)
//   - Quick prototypes: ModelMapper is fine
//   - Never mix both in the same project
// =============================================================================
