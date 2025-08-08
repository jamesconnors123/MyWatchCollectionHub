package com.mywatchcollectionhub.controller;

import com.mywatchcollectionhub.model.Watch;
import com.mywatchcollectionhub.service.WatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller exposing endpoints for interacting with the watch collection.
 *
 * <p>
 * This controller provides basic CRUD operations.  Additional endpoints for
 * searching, filtering and batch operations can be added as the project
 * evolves.
 */
@RestController
@RequestMapping("/api/watches")
public class WatchController {
    private final WatchService watchService;

    @Autowired
    public WatchController(WatchService watchService) {
        this.watchService = watchService;
    }

    /**
     * Create a new watch record.
     *
     * @param watch watch object from request body
     * @return persisted watch
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Watch createWatch(@RequestBody Watch watch) {
        return watchService.save(watch);
    }

    /**
     * Retrieve all watches.
     *
     * @return list of watches
     */
    @GetMapping
    public List<Watch> getAllWatches() {
        return watchService.getAll();
    }

    /**
     * Retrieve a single watch by its id.
     *
     * @param id watch identifier
     * @return watch if found
     */
    @GetMapping("/{id}")
    public Watch getWatch(@PathVariable Long id) {
        Optional<Watch> watch = watchService.getById(id);
        return watch.orElseThrow(() -> new IllegalArgumentException("Watch not found with id: " + id));
    }

    /**
     * Delete a watch by id.
     *
     * @param id watch identifier
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWatch(@PathVariable Long id) {
        watchService.delete(id);
    }
}