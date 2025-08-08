package com.mywatchcollectionhub.repository;

import com.mywatchcollectionhub.model.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for persisting and retrieving {@link Watch} entities.
 *
 * <p>
 * The repository exposes CRUD operations inherited from {@link JpaRepository}.
 */
@Repository
public interface WatchRepository extends JpaRepository<Watch, Long> {
    // Additional query methods can be defined here, e.g. findByBrand, findByModel, etc.
}