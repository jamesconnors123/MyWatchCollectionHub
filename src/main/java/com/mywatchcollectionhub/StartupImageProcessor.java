package com.mywatchcollectionhub;

import com.mywatchcollectionhub.service.ImageIngestionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Component that processes unprocessed watch images on application startup.
 *
 * <p>
 * When the Spring Boot application starts this runner scans the configured
 * directory for image files and ingests them via {@link ImageIngestionService}.
 * Processed images are stored in the "uploads" directory for further
 * processing.  This allows users to drop image files into the defined
 * directory and have them automatically catalogued when the application
 * restarts.
 */
@Component
public class StartupImageProcessor implements ApplicationRunner {

    @Value("${image.unprocessed.path:src/main/resources/ImageFiles/Unprocessed}")
    private String unprocessedPath;

    private final ImageIngestionService ingestionService;

    public StartupImageProcessor(ImageIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File dir = new File(unprocessedPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles((d, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
        });
        if (files == null || files.length == 0) {
            return;
        }
        List<File> fileList = new ArrayList<>(Arrays.asList(files));
        try {
            ingestionService.ingestImageFiles(fileList);
        } catch (IOException e) {
            // Log and swallow exceptions to avoid preventing application startup
            System.err.println("Failed to ingest unprocessed images: " + e.getMessage());
        }
    }
}