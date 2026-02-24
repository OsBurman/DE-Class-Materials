package com.library;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents an author.
 *
 * @Data is a convenience annotation that bundles:
 *   @Getter, @Setter, @RequiredArgsConstructor, @ToString, @EqualsAndHashCode
 *
 * Because @Data only generates @RequiredArgsConstructor (constructor for final/
 * @NonNull fields), we explicitly add @NoArgsConstructor and @AllArgsConstructor
 * to provide the full range of constructors that consumers typically need.
 *
 * Note: when both @Data and @AllArgsConstructor are present, Lombok suppresses the
 * default no-arg constructor it would otherwise produce, so @NoArgsConstructor must
 * be listed explicitly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    private int id;
    private String name;
    private String email;
}
