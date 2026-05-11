package com.juanoff.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparseMatrix {
    private final Map<String, Double> data = new HashMap<>();

    public void set(int x, int y, double value) {
        var k = key(x, y);
        if (value == 0) {
            data.remove(k);
        } else {
            data.put(k, value);
        }
    }

    public double get(int x, int y) {
        return data.getOrDefault(key(x, y), 0.0);
    }

    public int size() {
        return data.size();
    }

    public SparseMatrix add(SparseMatrix other) {
        SparseMatrix result = this.copy();
        for (MatrixElement elem : other.getEntries()) {
            double currentVal = result.get(elem.x, elem.y);
            double newVal = currentVal + elem.value;
            result.set(elem.x, elem.y, newVal);
        }
        return result;
    }

    public SparseMatrix copy() {
        SparseMatrix copy = new SparseMatrix();
        copy.data.putAll(data);
        return copy;
    }

    public double sum() {
        return data.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public List<MatrixElement> getEntries() {
        List<MatrixElement> list = new ArrayList<>();

        data.forEach((key, value) -> {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            list.add(new MatrixElement(x, y, value));
        });

        return list;
    }

    @Override
    public String toString() {
        if (data.isEmpty()) {
            return "[ ]";
        }

        List<String> parts = new ArrayList<>();
        data.forEach((key, value) -> {
            String[] coordinates = key.split(",");
            String x = coordinates[0];
            String y = coordinates[1];
            parts.add(String.format("(%s,%s)=%.1f", x, y, value));
        });

        return "[" + String.join("; ", parts) + "]";
    }

    private String key(int x, int y) {
        return x + "," + y;
    }
}
