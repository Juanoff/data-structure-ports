package com.juanoff.ui.util;

import com.juanoff.controller.Action;
import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIHelper {
    private static final Logger log = LoggerFactory.getLogger(UIHelper.class);

    public static void showError(Node owner, String title, String message) {
        showAlert(owner, title, message, Alert.AlertType.ERROR);
    }

    public static void showError(Window owner, String title, String message) {
        showAlert(owner, title, message, Alert.AlertType.ERROR);
    }

    public static void showInfo(Node owner, String title, String message) {
        showAlert(owner, title, message, Alert.AlertType.INFORMATION);
    }

    public static void showInfo(Window owner, String title, String message) {
        showAlert(owner, title, message, Alert.AlertType.INFORMATION);
    }

    public static boolean confirm(Node owner, String title, String message) {
        return showAlert(owner, title, message, Alert.AlertType.CONFIRMATION)
                .map(r -> r == ButtonType.OK)
                .orElse(false);
    }

    public static boolean confirm(Window owner, String title, String message) {
        return showAlert(owner, title, message, Alert.AlertType.CONFIRMATION)
                .map(r -> r == ButtonType.OK)
                .orElse(false);
    }

    public static void showWarning(Node owner, String title, String message) {
        showAlert(owner, title, message, Alert.AlertType.WARNING);
    }

    public static Optional<ButtonType> showAlert(Node owner,
                                                 String title,
                                                 String message,
                                                 Alert.AlertType type
    ) {
        Window window = owner != null ? owner.getScene().getWindow() : null;
        return showAlert(window, title, message, type);
    }

    public static Optional<ButtonType> showAlert(Window owner,
                                                 String title,
                                                 String message,
                                                 Alert.AlertType type
    ) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }
        return alert.showAndWait();
    }

    public static File showFileDialog(Node ownerNode, String title, boolean save, String... extensions) {
        if (ownerNode == null || ownerNode.getScene() == null) {
            log.error("Owner node must be attached to a scene");
            return null;
        }

        Window window = ownerNode.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);

        if (extensions != null) {
            for (String ext : extensions) {
                String desc = ext.equals("*.*")
                        ? "All Files"
                        : ext.replaceAll("[*.,]", "").toUpperCase() + " Files";
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, ext));
            }
        }

        return save ? chooser.showSaveDialog(window) : chooser.showOpenDialog(window);
    }

    public static void executeSafely(Node owner, String context, Action action, Action onSuccess) {
        try {
            action.run();
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            log.error("{} failed: ", context, e);
            showError(owner, "Error", ErrorHandler.toUserMessage(e));
        }
    }

    public static boolean closeDialog(Node owner) {
        if (owner == null || owner.getScene() == null) {
            return false;
        }

        Window window = owner.getScene().getWindow();
        if (window instanceof Stage stage) {
            stage.close();
            return true;
        }
        return false;
    }

    public static <T> void closeDialog(Node owner, T result, Consumer<T> callback) {
        if (closeDialog(owner) && callback != null) {
            callback.accept(result);
        }
    }
}
