package com.library;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

/**
 * Prints the configured server port and context path on startup.
 *
 * ServerProperties is a @ConfigurationProperties bean registered automatically
 * by Spring Boot's web auto-configuration — it reflects the values in application.yml.
 */
@Component
public class ServerInfoRunner implements CommandLineRunner {

    private final ServerProperties props;

    public ServerInfoRunner(ServerProperties props) {
        this.props = props;
    }

    @Override
    public void run(String... args) {
        System.out.println("Server port: " + props.getPort());
        // getServlet().getContextPath() returns "/" if context-path is not set
        System.out.println("Context path: " + props.getServlet().getContextPath());
    }
}
