package com.academy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model — demonstrates Lombok annotations.
 *
 * @Data generates:  getters, setters, toString, equals, hashCode, requiredArgsConstructor
 * @Builder generates: Course.builder().code("CS101").title("Java").credits(3).build()
 * @NoArgsConstructor — required when @Builder is used alongside JPA (for no-arg instantiation)
 * @AllArgsConstructor — constructor with all fields
 *
 * This bean is PROTOTYPE scoped (configured in AppConfig).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String code;
    private String title;
    private String instructor;
    private int    credits;

    // Without Lombok you'd write ~50 lines of boilerplate for getters/setters/toString/equals
}
