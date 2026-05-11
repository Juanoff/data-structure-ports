package com.juanoff.serialization;

import java.io.File;
import tools.jackson.databind.ObjectMapper;

public class JsonSerializer implements Serializer {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void save(Object obj, File file) throws Exception {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, obj);
    }

    @Override
    public Object load(File file, Class<?> clazz) throws Exception {
        return mapper.readValue(file, clazz);
    }
}
