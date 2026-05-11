package com.juanoff.types;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class SparseMatrixType implements UserType {

    @Override
    public String typeName() {
        return UserTypeName.SPARSE_MATRIX.getTypeName();
    }

    @Override
    public Object create() {
        return new SparseMatrixType();
    }

    @Override
    public Object clone(Object obj) {
        return ((SparseMatrix) obj).copy();
    }

    @Override
    public Object readValue(InputStreamReader in) throws Exception {
        BufferedReader reader = new BufferedReader(in);
        String line = reader.readLine();
        return parseValue(line);
    }

    @Override
    public Object parseValue(String ss) {
        if (ss == null || ss.isBlank()) {
            throw new IllegalArgumentException("Enter matrix parameters");
        }

        SparseMatrix matrix = new SparseMatrix();
        String[] parts = ss.split(";");
        for (String p : parts) {
            String[] vals = p.trim().split(",");
            if (vals.length != 3) {
                throw new IllegalArgumentException("Invalid matrix format");
            }

            try {
                int x = Integer.parseInt(vals[0].trim());
                int y = Integer.parseInt(vals[1].trim());
                double v = Double.parseDouble(vals[2].trim());
                matrix.set(x, y, v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid format in part: " + Arrays.toString(vals));
            }
        }
        return matrix;
    }

    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> {
            SparseMatrix m1 = (SparseMatrix) o1;
            SparseMatrix m2 = (SparseMatrix) o2;
            int sizeCmp = Integer.compare(m1.size(), m2.size());
            if (sizeCmp != 0) {
                return sizeCmp;
            }
            return Double.compare(m1.sum(), m2.sum());
        };
    }

    @Override
    public String serialize(Object obj) {
        SparseMatrix m = (SparseMatrix) obj;
        return m.getEntries().stream()
                .map(e -> e.x + "," + e.y + "," + e.value)
                .reduce((a, b) -> a + ";" + b)
                .orElse("");
    }

    @Override
    public Object deserialize(String s) {
        return parseValue(s);
    }
}
