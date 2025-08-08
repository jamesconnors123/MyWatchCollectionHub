package com.mywatchcollectionhub.controller;

import com.mywatchcollectionhub.model.Watch;
import com.mywatchcollectionhub.service.ImageIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for ingesting watch images.  Clients can upload multiple
 * images and receive created watch records in response.  The images are
 * clustered and analysed on the server.
 */
@RestController
@RequestMapping("/api/images")
public class ImageIngestionController {
    private final ImageIngestionService ingestionService;

    @Autowired
    public ImageIngestionController(ImageIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    /**
     * Upload and ingest one or more watch images.  The uploaded images are
     * saved, clustered and used to create watch entries.  The response
     * contains the newly created watch objects.
     *
     * @param files multipart files representing watch photos
     * @return list of created watch entities
     * @throws IOException if ingestion fails
     */
    @PostMapping("/ingest")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Watch> ingest(@RequestParam("files") MultipartFile[] files) throws IOException {
        return ingestionService.ingestImages(files);
    }
}