package com.library;

// TODO: Import @Component, CommandLineRunner
// TODO: Import Environment from org.springframework.core.env
// TODO: Import @Qualifier from org.springframework.beans.factory.annotation

/**
 * Prints which profile is active and which DataSource bean was selected.
 *
 * TODO:
 *   1. Add @Component and implement CommandLineRunner
 *   2. Inject:
 *        - Environment env   (for reading active profiles)
 *        - String dataSourceDescription  (the profile-specific bean)
 *          Use @Qualifier("dataSourceDescription") to select the correct bean by name
 *   3. In run():
 *        - Print "Active profile: " + env.getActiveProfiles()[0]
 *        - Print "DataSource: " + dataSourceDescription
 */
public class ProfileDemoRunner {

    // TODO: declare injected fields

    // TODO: constructor with Environment and String parameters

    // TODO: implement run(String... args)
}
