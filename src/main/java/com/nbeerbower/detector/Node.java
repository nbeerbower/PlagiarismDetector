package com.nbeerbower.detector;

import java.util.HashMap;
import java.util.Map;

public class Node<T> {
    private Map<T, Node<T>> neighbors;
    private T value;

    public Node(T value) {
        this.value = value;
        this.neighbors = new HashMap<T, Node<T>>();
    }

    public Node<T> addNeighbor(Node<T> neighbor) {
        return neighbors.computeIfAbsent(neighbor.getValue(), k -> neighbor);
    }

    public Node<T> findNeighbor(T value) {
        return neighbors.get(value);
    }

    public T getValue() {
        return value;
    }
}
