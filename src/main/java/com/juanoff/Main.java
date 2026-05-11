package com.juanoff;

import com.juanoff.config.ViewPaths;
import com.juanoff.ui.operations.SparseMatrixOperations;
import com.juanoff.types.UserTypeName;
import com.juanoff.ui.handler.TypeOperationRegistry;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        registerTypeOperations();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewPaths.MAIN_VIEW_FXML));
        Scene scene = new Scene(loader.load(), 700, 500);
        String styles = Objects.requireNonNull(getClass().getResource(ViewPaths.STYLES_CSS)).toExternalForm();
        scene.getStylesheets().add(styles);
        stage.setTitle("Data Structure App");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(ViewPaths.APP_ICON));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void registerTypeOperations() {
        TypeOperationRegistry.register(
                UserTypeName.SPARSE_MATRIX.getTypeName(),
                TypeOperationRegistry.Operation.EDIT,
                SparseMatrixOperations::openEditor
        );
        TypeOperationRegistry.register(
                UserTypeName.SPARSE_MATRIX.getTypeName(),
                TypeOperationRegistry.Operation.SEARCH,
                SparseMatrixOperations::openSearch
        );
    }
}
