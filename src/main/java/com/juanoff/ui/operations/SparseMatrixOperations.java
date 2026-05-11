package com.juanoff.ui.operations;

import com.juanoff.config.ViewPaths;
import com.juanoff.controller.MatrixEditorController;
import com.juanoff.controller.MatrixSearchController;
import com.juanoff.scala.types.SparseMatrix;
import com.juanoff.scala.types.SparseMatrixType;
import com.juanoff.ui.factory.StageFactory;
import com.juanoff.ui.util.UIHelper;
import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparseMatrixOperations {
    private static final Logger log = LoggerFactory.getLogger(SparseMatrixOperations.class);

    private SparseMatrixOperations() {
    }

    public static void openEditor(Node owner, Object initialMatrix, Consumer<Object> onResult) {
        try {
            FXMLLoader loader = new FXMLLoader(SparseMatrixOperations.class.getResource(ViewPaths.MATRIX_EDITOR_FXML));
            Parent root = loader.load();

            MatrixEditorController editor = loader.getController();
            if (initialMatrix instanceof SparseMatrix matrix) {
                editor.preloadMatrix(matrix);
            }

            editor.setOnSaveCallback(matrix -> {
                if (onResult != null) {
                    onResult.accept(matrix);
                }
                log.info("Matrix editor completed");
            });
            editor.setType(new SparseMatrixType());

            String title = initialMatrix == null ? "Create Sparse Matrix" : "Edit Sparse Matrix";
            Stage stage = StageFactory.createModal(owner, title, root, false);
            stage.getIcons().add(new Image(ViewPaths.MATRIX_ICON));
            stage.showAndWait();
        } catch (Exception e) {
            log.error("Failed to open matrix editor", e);
            UIHelper.showError(owner, "Error", "Could not open matrix editor");
        }
    }

    public static void openSearch(Node owner, Object matrixToSearch, Consumer<Object> onResult) {
        try {
            FXMLLoader loader = new FXMLLoader(SparseMatrixOperations.class.getResource(ViewPaths.MATRIX_SEARCH_FXML));
            Parent root = loader.load();

            MatrixSearchController searcher = loader.getController();
            if (matrixToSearch instanceof SparseMatrix matrix) {
                searcher.setMatrix(matrix);
            }

            searcher.setOnResultFound(result -> {
                if (onResult != null) {
                    onResult.accept(result);
                }
                log.info("Matrix search completed");
            });

            Stage stage = StageFactory.createModal(owner, "Search in Sparse Matrix", root, false);
            stage.getIcons().add(new Image(ViewPaths.SEARCH_ICON));
            stage.showAndWait();
        } catch (Exception e) {
            log.error("Failed to open matrix search", e);
            UIHelper.showError(owner, "Error", "Could not open search dialog");
        }
    }
}
