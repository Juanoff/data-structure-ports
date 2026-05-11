package com.juanoff.types;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class StringType implements UserType {

    @Override
    public String typeName() {
        return UserTypeName.STRING.getTypeName();
    }

    @Override
    public Object create() {
        return "";
    }

    @Override
    public Object clone(Object obj) {
        return obj;
    }

    @Override
    public Object readValue(InputStreamReader in) throws Exception {
        BufferedReader reader = new BufferedReader(in);
        return reader.readLine();
    }

    @Override
    public Object parseValue(String ss) {
        if (ss == null || ss.isBlank()) {
            throw new IllegalArgumentException("Enter value");
        }
        return ss.trim();
    }

    @Override
    public Comparator getTypeComparator() {
        return (o1, o2) -> ((String) o1).compareTo((String) o2);
    }

    @Override
    public String serialize(Object obj) {
        return (String) obj;
    }

    @Override
    public Object deserialize(String s) {
        return s;
    }
}
