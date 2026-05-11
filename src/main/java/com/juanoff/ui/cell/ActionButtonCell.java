package com.juanoff.ui.cell;

import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

public class ActionButtonCell<T> extends TableCell<T, String> {
    private final Button button;
    private Consumer<T> onAction;

    public ActionButtonCell(String buttonText, String style, Consumer<T> onAction) {
        this.button = new Button(buttonText);
        this.onAction = onAction;

        if (style != null) {
            button.setStyle(style);
        }

        button.setOnAction(e -> {
            T item = getTableView().getItems().get(getIndex());
            if (item != null && onAction != null) {
                onAction.accept(item);
            }
        });
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : button);
    }

    public void setOnAction(Consumer<T> onAction) {
        this.onAction = onAction;
    }

    public Consumer<T> onAction() {
        return onAction;
    }
}
