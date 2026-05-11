package com.juanoff.types;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class IntegerType implements UserType {

    @Override
    public String typeName() {
        return UserTypeName.INTEGER.getTypeName();
    }

    @Override
    public Object create() {
        return 0;
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
            return Integer.parseInt(ss.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer format: '" + ss.trim() + "'. Expected a number.");
        }
    }

    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> Integer.compare((Integer) o1, (Integer) o2);
    }

    @Override
    public String serialize(Object obj) {
        return obj.toString();
    }

    @Override
    public Object deserialize(String s) {
        return Integer.parseInt(s);
    }
}
