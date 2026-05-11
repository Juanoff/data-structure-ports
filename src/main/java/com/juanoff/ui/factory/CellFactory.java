package com.juanoff.ui.factory;

import com.juanoff.ui.cell.ActionButtonCell;
import java.util.function.Consumer;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class CellFactory {
    public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> actionButton(String text,
                                                                                          String style,
                                                                                          Consumer<T> onAction
    ) {
        return col -> new ActionButtonCell<>(text, style, onAction);
    }
}
