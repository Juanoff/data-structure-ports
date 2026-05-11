package com.juanoff.types;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DoubleType implements UserType {

    @Override
    public String typeName() {
        return UserTypeName.DOUBLE.getTypeName();
    }

    @Override
    public Object create() {
        return 0.0;
    }

    @Override
    public Object clone(Object obj) {
        return obj;
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
            throw new IllegalArgumentException("Enter value");
        }

        try {
            double value = Double.parseDouble(ss.trim());
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new IllegalArgumentException("Value cannot be NaN or Infinity");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid double format: '" + ss.trim() + "'");
        }
    }

    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> Double.compare((Double) o1, (Double) o2);
    }

    @Override
    public String serialize(Object obj) {
        return obj.toString();
    }

    @Override
    public Object deserialize(String s) {
        return parseValue(s);
    }
}
