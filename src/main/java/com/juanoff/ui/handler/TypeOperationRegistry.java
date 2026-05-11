package com.juanoff.ui.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.scene.Node;

public class TypeOperationRegistry {

    public enum Operation {
        EDIT,
        SEARCH
    }

    private static final Map<String, Map<Operation, TypeOperationHandler>> registry = new HashMap<>();

    private TypeOperationRegistry() {
    }

    public static void register(String typeName, Operation operation, TypeOperationHandler handler) {
        registry.computeIfAbsent(typeName, k -> new HashMap<>()).put(operation, handler);
    }

    public static boolean hasHandler(String typeName, Operation operation) {
        return Optional.ofNullable(registry.get(typeName))
                .map(ops -> ops.containsKey(operation))
                .orElse(false);
    }

    public static void execute(String typeName,
                               Operation operation,
                               Node owner,
                               Object initialValue,
                               Consumer<Object> onResult
    ) {
        Optional.ofNullable(registry.get(typeName))
                .map(ops -> ops.get(operation))
                .ifPresentOrElse(
                        handler -> handler.handle(owner, initialValue, onResult),
                        () -> {
                            if (onResult != null) {
                                onResult.accept(null);
                            }
                        }
                );
    }
}
