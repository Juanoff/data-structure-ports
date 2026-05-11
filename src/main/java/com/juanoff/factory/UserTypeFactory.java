package com.juanoff.factory;

import com.juanoff.types.DoubleType;
import com.juanoff.types.IntegerType;
import com.juanoff.types.SparseMatrixType;
import com.juanoff.types.StringType;
import com.juanoff.types.UserType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTypeFactory {
    private final Map<String, UserType> types = new HashMap<>();

    public UserTypeFactory() {
        register(new IntegerType());
        register(new DoubleType());
        register(new StringType());
        register(new SparseMatrixType());
    }

    private void register(UserType type) {
        types.put(type.typeName(), type);
    }

    public List<String> getTypeNameList() {
        return new ArrayList<>(types.keySet());
    }

    public UserType getBuilderByName(String name) {
        return types.get(name);
    }
}
