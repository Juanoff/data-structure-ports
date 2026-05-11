package com.juanoff.types;

import java.io.InputStreamReader;

public interface UserType {
    String typeName();

    Object create();

    Object clone(Object obj);

    Object readValue(InputStreamReader in) throws Exception;

    Object parseValue(String ss) throws Exception;

    Comparator getTypeComparator();

    String serialize(Object object);

    Object deserialize(String s) throws Exception;
}
