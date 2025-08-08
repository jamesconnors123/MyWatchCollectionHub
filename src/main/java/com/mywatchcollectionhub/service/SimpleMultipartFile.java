package com.mywatchcollectionhub.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple implementation of {@link MultipartFile} backed by a byte array.
 *
 * <p>
 * This class is useful for converting regular {@link java.io.File} instances
 * into MultipartFile objects so they can be passed to existing methods that
 * expect multipart uploads (such as {@link ImageStorageService#saveImages}).
 * It is not intended for streaming large files and loads the entire file
 * content into memory.  Use with caution for very large files.
 */
public class SimpleMultipartFile implements MultipartFile {

    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    /**
     * Create a MultipartFile from the given file.  The file's bytes are
     * eagerly loaded into memory.
     *
     * @param file the file to wrap
     * @throws IOException if reading the file fails
     */
    public SimpleMultipartFile(File file) throws IOException {
        this.name = file.getName();
        this.originalFilename = file.getName();
        Path path = file.toPath();
        this.contentType = Files.probeContentType(path);
        this.content = Files.readAllBytes(path);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Path target = dest.toPath();
        Files.write(target, content);
    }
}