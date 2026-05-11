package com.juanoff.serialization;

public enum SerializerType {
    JSON("*.json"),
    XML("*.xml"),
    TEXT("*.txt"),
    BIN("*.bin", "*.dat", "*.ser", "*.*");

    private final String[] extensions;

    SerializerType(String... extensions) {
        this.extensions = extensions;
    }

    public String[] getExtensions() {
        return extensions;
    }
}
