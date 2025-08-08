package com.mywatchcollectionhub.service;

import org.springframework.stereotype.Service;

/**
 * Placeholder service for enriching watch data with additional metadata.
 *
 * <p>
 * This class would, in a full implementation, call out to external APIs
 * such as watch databases, eBay, Chrono24 or WatchCharts to fetch details
 * like historical pricing, production years and descriptions for a given
 * watch model.  Currently it contains stub methods to be completed later.
 */
@Service
public class MetadataCollectionService {
    /**
     * Enrich a watch record with metadata from external sources.
     *
     * @param brand the watch brand
     * @param model the watch model
     * @return descriptive text about the watch or {@code null} if none found
     */
    public String fetchDescription(String brand, String model) {
        // TODO: implement metadata lookup via external APIs
        return null;
    }

    /**
     * Lookup approximate resale value for the given watch.
     *
     * @param brand watch brand
     * @param model watch model
     * @param year  year of production
     * @return approximate current resale value
     */
    public Double estimateResaleValue(String brand, String model, Integer year) {
        // TODO: implement pricing lookup via watch marketplaces
        return null;
    }
}