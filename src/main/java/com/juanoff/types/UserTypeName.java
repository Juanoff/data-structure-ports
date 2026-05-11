package com.juanoff.types;

public enum UserTypeName {
    INTEGER("Integer"),
    DOUBLE("Double"),
    STRING("String"),
    SPARSE_MATRIX("SparseMatrix");

    private final String typeName;

    UserTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
