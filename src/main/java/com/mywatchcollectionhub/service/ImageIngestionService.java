package com.mywatchcollectionhub.service;

import com.mywatchcollectionhub.model.Watch;
import com.mywatchcollectionhub.service.ImageRecognitionService.WatchMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * High‑level service orchestrating image ingestion, clustering and watch creation.
 *
 * <p>
 * This service saves uploaded images, clusters them using {@link ImageClusteringService},
 * performs basic recognition via {@link ImageRecognitionService}, enriches metadata via
 * {@link MetadataCollectionService} and finally persists {@link Watch} records using
 * {@link WatchService}.  It returns a list of newly created watch entities.
 */
@Service
public class ImageIngestionService {
    private final ImageStorageService storageService;
    private final ImageClusteringService clusteringService;
    private final ImageRecognitionService recognitionService;
    private final MetadataCollectionService metadataService;
    private final WatchService watchService;

    @Autowired
    public ImageIngestionService(ImageStorageService storageService,
                                ImageClusteringService clusteringService,
                                ImageRecognitionService recognitionService,
                                MetadataCollectionService metadataService,
                                WatchService watchService) {
        this.storageService = storageService;
        this.clusteringService = clusteringService;
        this.recognitionService = recognitionService;
        this.metadataService = metadataService;
        this.watchService = watchService;
    }

    /**
     * Ingest uploaded images: save, cluster, analyse and persist watch records.
     *
     * @param files uploaded image files
     * @return list of persisted watch entities
     * @throws IOException if file saving or clustering fails
     */
    public List<Watch> ingestImages(MultipartFile[] files) throws IOException {
        // Step 1: persist images to local storage
        List<File> stored = storageService.saveImages(files);
        // Step 2: cluster images to group by watch
        // Cluster images into groups.  The clustering service now runs fully
        // in Java and does not throw InterruptedException.
        Map<File, Integer> clusterMap = clusteringService.clusterImages(stored);
        // Group images by cluster id
        Map<Integer, List<File>> clusters = new HashMap<>();
        for (Map.Entry<File, Integer> entry : clusterMap.entrySet()) {
            clusters.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }
        List<Watch> created = new ArrayList<>();
        for (Map.Entry<Integer, List<File>> entry : clusters.entrySet()) {
            List<File> group = entry.getValue();
            // Step 3: analyse images to extract basic metadata (stub)
            WatchMetadata meta = recognitionService.analyzeImages(group);
            // Step 4: enrich metadata (stub)
            String description = metadataService.fetchDescription(meta.brand, meta.model);
            Double value = metadataService.estimateResaleValue(meta.brand, meta.model, meta.year);
            // Step 5: persist watch
            Watch watch = new Watch();
            watch.setBrand(meta.brand != null ? meta.brand : "Unknown");
            watch.setModel(meta.model != null ? meta.model : "Unknown");
            watch.setYear(meta.year);
            watch.setResaleValue(value != null ? value : meta.approximateValue);
            watch.setDescription(description != null ? description : meta.description);
            // Save relative paths to images for later retrieval
            List<String> imagePaths = group.stream()
                    .map(f -> "uploads" + File.separator + f.getName())
                    .collect(Collectors.toList());
            watch.setImageUrls(imagePaths);
            watch.setReferenceLinks(new ArrayList<>());
            watch.setTags(new ArrayList<>());
            watch = watchService.save(watch);
            created.add(watch);
        }
        return created;
    }
}