package com.library;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.library")  // scans all classes in com.library for @Component/@Service/@Repository
public class ScanningConfig {
    // Empty — no @Bean methods needed; the scanner registers all annotated classes
}
