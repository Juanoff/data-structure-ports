package com.juanoff.serialization;

import java.io.File;
import tools.jackson.dataformat.xml.XmlMapper;

public class XmlSerializer implements Serializer {
    private final XmlMapper mapper = new XmlMapper();

    @Override
    public void save(Object obj, File file) throws Exception {
        mapper.writeValue(file, obj);
    }

    @Override
    public Object load(File file, Class<?> clazz) throws Exception {
        return mapper.readValue(file, clazz);
    }
}
