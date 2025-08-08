package com.mywatchcollectionhub.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistent entity representing a single watch in the owner's collection.
 *
 * <p>
 * A watch record stores the key pieces of information about the watch such as
 * brand, model and year as well as lists of image URLs, reference links and
 * tags.  Additional fields (such as serial numbers or movement details) can
 * be added in future iterations.  Collections are stored as element
 * collections which are mapped to a separate table automatically by JPA.
 */
@Entity
public class Watch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private Integer year;
    private Double resaleValue;
    private String description;

    /**
     * URLs to the original images uploaded for this watch.  These could be
     * relative paths or external links depending on how images are stored.
     */
    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    /**
     * Reference links to external sites describing the watch brand or model.
     */
    @ElementCollection
    private List<String> referenceLinks = new ArrayList<>();

    /**
     * Arbitrary tags for searching/filtering watches.  Examples include
     * "diver", "chronograph", or specific material names.
     */
    @ElementCollection
    private List<String> tags = new ArrayList<>();

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getResaleValue() {
        return resaleValue;
    }

    public void setResaleValue(Double resaleValue) {
        this.resaleValue = resaleValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getReferenceLinks() {
        return referenceLinks;
    }

    public void setReferenceLinks(List<String> referenceLinks) {
        this.referenceLinks = referenceLinks;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}