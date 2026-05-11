package com.juanoff.serialization;

import java.io.File;

public interface Serializer {
    void save(Object obj, File file) throws Exception;

    Object load(File file, Class<?> clazz) throws Exception;
}
