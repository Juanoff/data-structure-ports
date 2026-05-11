package com.juanoff.util;

import com.juanoff.serialization.SerializerType;
import java.io.File;
import java.util.Arrays;

public class SerializerDetector {
    public static SerializerType detect(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File not found");
        }

        String name = file.getName().toLowerCase();
        int dot = name.lastIndexOf('.');
        String ext = (dot == -1) ? "" : name.substring(dot + 1);

        return Arrays.stream(SerializerType.values())
                .filter(type -> Arrays.stream(type.getExtensions())
                        .anyMatch(pattern -> pattern
                                .replace("*.", "")
                                .equalsIgnoreCase(ext)
                        )
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Unsupported file format: '." + ext + "'. Use JSON, XML, TXT or BIN."
                        )
                );
    }
}
