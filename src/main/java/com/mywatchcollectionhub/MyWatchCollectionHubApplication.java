package com.mywatchcollectionhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the MyWatchCollectionHub Spring Boot application.
 *
 * <p>
 * This class boots the Spring application context and exposes any REST
 * controllers defined under the {@code com.mywatchcollectionhub} package.  It
 * currently hosts a simple API for managing watches in a collection but is
 * intended to be extended over time with services for image recognition,
 * metadata enrichment and search capabilities as described in the project
 * README.
 */
@SpringBootApplication
public class MyWatchCollectionHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyWatchCollectionHubApplication.class, args);
    }
}