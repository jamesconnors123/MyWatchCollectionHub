package com.mywatchcollectionhub.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Service that clusters images based on their visual similarity.
 *
 * <p>
 * This implementation is self‑contained in Java to avoid external script
 * dependencies.  It computes a simple colour histogram for each image and
 * applies a lightweight DBSCAN‑like algorithm to group similar images.
 * The algorithm uses Euclidean distance on normalised histograms and
 * configurable parameters for the epsilon radius and minimum number of
 * neighbours.  Images in the same cluster are presumed to depict the
 * same watch from different angles.
 */
@Service
public class ImageClusteringService {

    /**
     * Epsilon parameter controlling the maximum distance between two
     * histograms for them to be considered neighbours.  Smaller values
     * result in more clusters.  This default mirrors the Python script's
     * default of 0.5.  It can be tuned as needed.
     */
    private static final double EPS = 0.5;

    /**
     * Minimum number of neighbouring points required to form a cluster.
     * When set to 1 the algorithm behaves similarly to DBSCAN with
     * min_samples=1, where every point becomes a core point.  Increase
     * this value to require more evidence before grouping images.
     */
    private static final int MIN_PTS = 1;

    /**
     * Cluster the given images using a simple DBSCAN‑like algorithm.  It
     * computes a normalised colour histogram for each image and groups
     * images whose histograms are within the epsilon radius.  Noise
     * images (those that do not have enough neighbours) are assigned a
     * cluster id of -1.
     *
     * @param images the list of image files to cluster
     * @return a mapping of each file to a cluster id
     * @throws IOException if an image cannot be read
     */
    public Map<File, Integer> clusterImages(List<File> images) throws IOException {
        Map<File, Integer> clusterAssignments = new HashMap<>();
        int n = images.size();
        if (n == 0) {
            return clusterAssignments;
        }
        // Compute histograms for each image.
        double[][] features = new double[n][];
        for (int i = 0; i < n; i++) {
            features[i] = computeHistogram(images.get(i));
        }
        boolean[] visited = new boolean[n];
        int clusterId = 0;
        for (int i = 0; i < n; i++) {
            if (visited[i]) {
                continue;
            }
            visited[i] = true;
            List<Integer> neighbours = regionQuery(i, features);
            if (neighbours.size() < MIN_PTS) {
                // mark as noise
                clusterAssignments.put(images.get(i), -1);
            } else {
                // create new cluster and expand
                expandCluster(i, neighbours, clusterId, clusterAssignments, visited, features, images);
                clusterId++;
            }
        }
        return clusterAssignments;
    }

    /**
     * Expand a cluster by recursively visiting neighbouring points.
     */
    private void expandCluster(int index,
                              List<Integer> neighbours,
                              int clusterId,
                              Map<File, Integer> assignments,
                              boolean[] visited,
                              double[][] features,
                              List<File> images) {
        assignments.put(images.get(index), clusterId);
        Queue<Integer> queue = new LinkedList<>(neighbours);
        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (!visited[current]) {
                visited[current] = true;
                List<Integer> currentNeighbours = regionQuery(current, features);
                if (currentNeighbours.size() >= MIN_PTS) {
                    queue.addAll(currentNeighbours);
                }
            }
            if (!assignments.containsKey(images.get(current))) {
                assignments.put(images.get(current), clusterId);
            }
        }
    }

    /**
     * Find neighbours for the point at the given index.
     */
    private List<Integer> regionQuery(int index, double[][] features) {
        List<Integer> neighbours = new ArrayList<>();
        double[] base = features[index];
        for (int j = 0; j < features.length; j++) {
            double dist = euclideanDistance(base, features[j]);
            if (dist <= EPS) {
                neighbours.add(j);
            }
        }
        return neighbours;
    }

    /**
     * Compute Euclidean distance between two histograms.
     */
    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    /**
     * Compute a normalised RGB histogram for the given image.
     *
     * <p>
     * The image is resized to 64x64 to reduce computation.  Three 256‑bin
     * histograms (one per colour channel) are concatenated into a single
     * 768‑element array.  The histogram is then normalised to unit length.
     *
     * @param file image file
     * @return a double array of length 768 representing the histogram
     * @throws IOException if the file cannot be read
     */
    private double[] computeHistogram(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        if (img == null) {
            // Return zero vector if image cannot be read.
            return new double[768];
        }
        // Resize to 64x64
        Image scaled = img.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        BufferedImage buffered = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        int[] hist = new int[768];
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                int rgb = buffered.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                hist[r]++;
                hist[256 + g]++;
                hist[512 + b]++;
            }
        }
        double[] feature = new double[768];
        double norm = 0.0;
        for (int i = 0; i < hist.length; i++) {
            feature[i] = hist[i];
            norm += hist[i] * hist[i];
        }
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < feature.length; i++) {
                feature[i] /= norm;
            }
        }
        return feature;
    }
}