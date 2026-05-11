package com.juanoff.serialization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TextSerializer implements Serializer {
    private static final String TYPE_HEADER = "TYPE: ";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    public void save(Object obj, File file) throws Exception {
        if (!(obj instanceof DataContainer container)) {
            throw new IllegalArgumentException("Data structure is incorrect for text serializer");
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), DEFAULT_CHARSET))
        ) {
            writer.write(TYPE_HEADER + container.typeName);
            writer.newLine();

            if (container.data != null) {
                for (String dataItem : container.data) {
                    writer.write(dataItem);
                    writer.newLine();
                }
            }
        }
    }

    @Override
    public Object load(File file, Class<?> clazz) throws Exception {
        DataContainer container = new DataContainer();
        container.data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), DEFAULT_CHARSET))
        ) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (isFirstLine) {
                    if (line.startsWith(TYPE_HEADER)) {
                        container.typeName = line.substring(TYPE_HEADER.length());
                    } else {
                        throw new IOException("Invalid text format: missing TYPE header");
                    }
                    isFirstLine = false;
                } else {
                    container.data.add(line);
                }
            }
        }
        return container;
    }
}