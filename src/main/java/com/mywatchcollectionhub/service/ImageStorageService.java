package com.mywatchcollectionhub.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for storing uploaded images on the local filesystem.
 *
 * <p>
 * All uploaded images are saved into an "uploads" directory in the project root.
 * If the directory does not exist it will be created on demand.  The service
 * returns a list of {@link File} objects pointing to the stored files which
 * can then be passed to downstream processing (e.g., clustering).  Filenames
 * are preserved to aid debugging; collisions are resolved by appending a
 * timestamp.
 */
@Service
public class ImageStorageService {

    private static final String UPLOAD_DIR = "uploads";

    /**
     * Save an array of uploaded MultipartFiles to the local filesystem.
     *
     * @param files the uploaded multipart files
     * @return a list of {@link File} objects representing the saved files
     * @throws IOException if saving any file fails
     */
    public List<File> saveImages(MultipartFile[] files) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        List<File> saved = new ArrayList<>();
        for (MultipartFile mf : files) {
            if (mf.isEmpty()) {
                continue;
            }
            String originalName = mf.getOriginalFilename();
            // Fallback to generic name if original is null
            if (originalName == null || originalName.trim().isEmpty()) {
                originalName = "uploaded";
            }
            // Resolve potential collisions by appending current time in millis
            Path target = uploadPath.resolve(originalName);
            if (Files.exists(target)) {
                String baseName = originalName;
                String ext = "";
                int idx = originalName.lastIndexOf('.');
                if (idx > 0) {
                    baseName = originalName.substring(0, idx);
                    ext = originalName.substring(idx);
                }
                target = uploadPath.resolve(baseName + "_" + System.currentTimeMillis() + ext);
            }
            Files.copy(mf.getInputStream(), target);
            saved.add(target.toFile());
        }
        return saved;
    }
}