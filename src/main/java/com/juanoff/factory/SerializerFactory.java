package com.juanoff.factory;

import com.juanoff.serialization.BinarySerializer;
import com.juanoff.serialization.JsonSerializer;
import com.juanoff.serialization.Serializer;
import com.juanoff.serialization.SerializerType;
import com.juanoff.serialization.TextSerializer;
import com.juanoff.serialization.XmlSerializer;

public class SerializerFactory {
    public static Serializer get(SerializerType type) {
        return switch (type) {
            case SerializerType.JSON -> new JsonSerializer();
            case SerializerType.XML -> new XmlSerializer();
            case SerializerType.BIN -> new BinarySerializer();
            case SerializerType.TEXT -> new TextSerializer();
        };
    }
}
