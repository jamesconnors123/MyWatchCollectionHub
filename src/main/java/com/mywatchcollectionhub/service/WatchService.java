package com.mywatchcollectionhub.service;

import com.mywatchcollectionhub.model.Watch;
import com.mywatchcollectionhub.repository.WatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer encapsulating business logic for managing watches.
 */
@Service
public class WatchService {
    private final WatchRepository watchRepository;

    @Autowired
    public WatchService(WatchRepository watchRepository) {
        this.watchRepository = watchRepository;
    }

    /**
     * Persist a new watch record or update an existing one.
     *
     * @param watch the watch to save
     * @return persisted watch
     */
    public Watch save(Watch watch) {
        return watchRepository.save(watch);
    }

    /**
     * Retrieve a watch by id.
     *
     * @param id the identifier of the watch
     * @return optional containing the watch if found
     */
    public Optional<Watch> getById(Long id) {
        return watchRepository.findById(id);
    }

    /**
     * Retrieve all watches.
     *
     * @return list of watches
     */
    public List<Watch> getAll() {
        return watchRepository.findAll();
    }

    /**
     * Delete a watch by id.
     *
     * @param id the identifier of the watch to delete
     */
    public void delete(Long id) {
        watchRepository.deleteById(id);
    }
}