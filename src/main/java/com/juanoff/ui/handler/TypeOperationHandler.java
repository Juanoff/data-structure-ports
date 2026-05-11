package com.juanoff.ui.handler;

import java.util.function.Consumer;
import javafx.scene.Node;

@FunctionalInterface
public interface TypeOperationHandler {
    void handle(Node owner, Object initialValue, Consumer<Object> onResult);
}
