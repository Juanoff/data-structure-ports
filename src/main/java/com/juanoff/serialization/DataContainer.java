package com.juanoff.serialization;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class DataContainer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public String typeName;
    public List<String> data;
}