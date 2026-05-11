package com.juanoff.logic;

import com.juanoff.scala.types.SparseMatrix;
import java.util.List;
import java.util.Optional;

public class MatrixEditorLogic {

    public record MatrixEntry(int x, int y, double value) {
    }

    public static Optional<MatrixEntry> findDuplicate(List<MatrixEntry> entries, int x, int y) {
        return entries.stream()
                .filter(e -> e.x() == x && e.y() == y)
                .findFirst();
    }

    public static String formatSummary(List<MatrixEntry> entries) {
        int count = entries.size();
        if (count == 0) {
            return "No elements";
        }

        double sum = entries.stream().mapToDouble(MatrixEntry::value).sum();
        double min = entries.stream().mapToDouble(MatrixEntry::value).min().orElse(0);
        double max = entries.stream().mapToDouble(MatrixEntry::value).max().orElse(0);

        return String.format("%d element%s | Sum: %.2f | Range: [%.2f, %.2f]",
                count, count == 1 ? "" : "s", sum, min, max
        );
    }

    public static SparseMatrix toMatrix(List<MatrixEntry> entries, SparseMatrix prototype) {
        SparseMatrix matrix = prototype.copy();
        for (MatrixEntry e : entries) {
            if (e.value() != 0.0) {
                matrix.set(e.x(), e.y(), e.value());
            }
        }
        return matrix;
    }
}
