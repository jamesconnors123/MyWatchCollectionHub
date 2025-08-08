#!/usr/bin/env python3
"""
Simple image clustering service.

This script accepts a list of image file paths, computes a basic feature
representation for each (a normalised colour histogram) and clusters the
images using DBSCAN.  It outputs a JSON mapping of each image path to
its assigned cluster ID.  Images with the same cluster ID are considered
duplicates of the same watch.

Dependencies:
  - Pillow (for image loading)
  - numpy (for numerical operations)
  - scikit‑learn (for DBSCAN clustering)

Usage:
  python3 image_clustering_service.py --images img1.jpg img2.jpg img3.jpg
  python3 image_clustering_service.py --images *.png --eps 0.6 --min_samples 1

Parameters:
  --images: list of image file paths to process
  --eps: DBSCAN epsilon parameter (default: 0.5)
  --min_samples: DBSCAN min_samples parameter (default: 1)

This script is intentionally lightweight and avoids GPU-heavy models to
reduce resource usage.  For real‑world clustering of watches, consider
using embeddings from a pre‑trained convolutional network (e.g., ResNet)
and then clustering those embeddings; however, that would require
additional dependencies such as PyTorch or TensorFlow.
"""
import argparse
import json
import os
import sys
from typing import List

import numpy as np
from PIL import Image
from sklearn.cluster import DBSCAN


def load_image_histogram(path: str) -> np.ndarray:
    """Load an image and compute a normalised colour histogram.

    Args:
        path: Path to the image file.

    Returns:
        A 768‑dimensional numpy array representing the RGB histogram,
        normalised to unit length.  If the image cannot be opened,
        a zero vector is returned.
    """
    try:
        with Image.open(path) as img:
            img = img.convert("RGB")
            # Resize to a smaller resolution to normalise histogram across
            # different image sizes.  64x64 is arbitrary but adequate for
            # coarse colour distribution.
            img = img.resize((64, 64))
            hist = img.histogram()
            arr = np.array(hist, dtype=np.float32)
            norm = np.linalg.norm(arr)
            if norm > 0:
                arr /= norm
            return arr
    except Exception:
        # If image cannot be processed, return a zero vector.
        return np.zeros(768, dtype=np.float32)


def cluster_images(paths: List[str], eps: float, min_samples: int) -> dict:
    """Cluster images using DBSCAN based on colour histogram features.

    Args:
        paths: List of image file paths.
        eps: DBSCAN epsilon parameter controlling cluster radius.
        min_samples: Minimum number of samples in a cluster.

    Returns:
        Dictionary mapping each path to its cluster ID.  Noise points are
        assigned a cluster ID of -1.
    """
    # Compute features for each image.
    features = [load_image_histogram(p) for p in paths]
    if not features:
        return {}
    X = np.stack(features)
    # Perform clustering.  Using Euclidean distance on histograms.
    db = DBSCAN(eps=eps, min_samples=min_samples, metric="euclidean")
    labels = db.fit_predict(X)
    return {paths[i]: int(labels[i]) for i in range(len(paths))}


def main() -> None:
    parser = argparse.ArgumentParser(description="Cluster images by colour histogram")
    parser.add_argument("--images", nargs="+", required=True, help="List of image files to cluster")
    parser.add_argument("--eps", type=float, default=0.5, help="DBSCAN epsilon parameter")
    parser.add_argument("--min_samples", type=int, default=1, help="DBSCAN min_samples parameter")
    args = parser.parse_args()

    mapping = cluster_images(args.images, args.eps, args.min_samples)
    json.dump(mapping, sys.stdout)


if __name__ == "__main__":
    main()