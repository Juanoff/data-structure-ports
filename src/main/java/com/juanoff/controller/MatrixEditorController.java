package com.juanoff.controller;

import com.juanoff.factory.SerializerFactory;
import com.juanoff.logic.MatrixEditorLogic;
import com.juanoff.scala.types.SparseMatrix;
import com.juanoff.scala.types.UserType;
import com.juanoff.serialization.DataContainer;
import com.juanoff.serialization.Serializer;
import com.juanoff.serialization.SerializerType;
import com.juanoff.types.MatrixElement;
import com.juanoff.types.UserTypeName;
import com.juanoff.ui.factory.CellFactory;
import com.juanoff.ui.util.UIHelper;
import com.juanoff.util.NumericValueParser;
import com.juanoff.util.SerializerDetector;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatrixEditorController {
    private static final Logger log = LoggerFactory.getLogger(MatrixEditorController.class);

    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    @FXML
    private TextField valueField;
    @FXML
    private TableView<MatrixEditorLogic.MatrixEntry> previewTable;
    @FXML
    private TableColumn<MatrixEditorLogic.MatrixEntry, Integer> colX;
    @FXML
    private TableColumn<MatrixEditorLogic.MatrixEntry, Integer> colY;
    @FXML
    private TableColumn<MatrixEditorLogic.MatrixEntry, Double> colValue;
    @FXML
    private TableColumn<MatrixEditorLogic.MatrixEntry, String> colActions;
    @FXML
    private Label summaryLabel;

    @FXML
    private Button addFromFileBtn;

    private UserType type;
    private final ObservableList<MatrixEditorLogic.MatrixEntry> entries = FXCollections.observableArrayList();
    private Consumer<SparseMatrix> onSaveCallback;
    private boolean hasChanges = false;

    @FXML
    public void initialize() {
        setupTableColumns();
        previewTable.setItems(entries);
        updateSummary();
        setupWindowCloseHandler();
        xField.requestFocus();

        addFromFileBtn.setTooltip(new Tooltip(
                "Loads matrix data and sums it with current entries. " +
                        "Non-zero values are added to existing coordinates.")
        );
    }

    private void setupTableColumns() {
        colX.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().x()).asObject());
        colY.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().y()).asObject());
        colValue.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().value()).asObject());

        colActions.setCellValueFactory(data -> new SimpleStringProperty("Delete"));
        colActions.setCellFactory(CellFactory.actionButton(
                "✕",
                "-fx-text-fill: #d32f2f;",
                entry -> {
                    UIHelper.executeSafely(
                            previewTable,
                            "Delete element",
                            () -> {
                                modifyEntries(() -> entries.remove(entry));
                            },
                            null
                    );
                }
        ));
    }

    private void setupWindowCloseHandler() {
        previewTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((winObs, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.setOnCloseRequest(e -> {
                            if (hasUnsavedChanges()) {
                                e.consume();
                                handleUnsavedChanges();
                            }
                        });
                    }
                });
            }
        });
    }

    public void setType(UserType type) {
        this.type = Objects.requireNonNull(type);
    }

    @FXML
    private void addElement() {
        UIHelper.executeSafely(
                previewTable,
                "Operation add",
                () -> {
                    int x = NumericValueParser.parseInt(xField.getText(), "X");
                    int y = NumericValueParser.parseInt(yField.getText(), "Y");
                    double value = NumericValueParser.parseDouble(valueField.getText(), "Value");

                    if (value == 0.0) {
                        UIHelper.showAlert(
                                previewTable,
                                "Zero value",
                                "Value 0 will not be stored in sparse matrix. Continue?",
                                Alert.AlertType.WARNING
                        ).ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                addEntry(x, y, value);
                            }
                        });
                    } else {
                        addEntry(x, y, value);
                    }
                },
                null
        );
    }

    private void addEntry(int x, int y, double value) {
        Optional<MatrixEditorLogic.MatrixEntry> duplicate = MatrixEditorLogic.findDuplicate(entries, x, y);
        if (duplicate.isPresent()) {
            String message = String.format(
                    "Element at (%d,%d) exists with value %.2f. Update to %.2f?",
                    x, y, duplicate.get().value(), value
            );
            UIHelper.showAlert(previewTable, "Update element", message, Alert.AlertType.CONFIRMATION)
                    .ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            UIHelper.executeSafely(
                                    previewTable,
                                    "Update element",
                                    () -> {
                                        entries.remove(duplicate.get());
                                        entries.add(new MatrixEditorLogic.MatrixEntry(x, y, value));
                                        modifyEntries(() -> {
                                        });
                                    },
                                    null
                            );
                        }
                    });
        } else {
            UIHelper.executeSafely(
                    previewTable,
                    "Add new element",
                    () -> {
                        modifyEntries(() -> {
                            entries.add(new MatrixEditorLogic.MatrixEntry(x, y, value));
                            clearInputFields();
                        });
                    },
                    null
            );
        }
    }

    @FXML
    private void clearAll() {
        if (entries.isEmpty()) {
            return;
        }

        UIHelper.showAlert(
                previewTable,
                "Clear all",
                "Remove all " + entries.size() + " elements?",
                Alert.AlertType.CONFIRMATION
        ).ifPresent(response -> {
            if (response == ButtonType.OK) {
                UIHelper.executeSafely(
                        previewTable,
                        "Clear all elements",
                        () -> modifyEntries(entries::clear),
                        null
                );
            }
        });
    }

    @FXML
    private void cancel() {
        if (handleUnsavedChanges()) {
            UIHelper.closeDialog(previewTable);
        }
    }

    @FXML
    private void addFromFile() {
        List<String> allExtensions = new ArrayList<>();
        for (SerializerType type : SerializerType.values()) {
            allExtensions.addAll(Arrays.asList(type.getExtensions()));
        }

        String[] uniqueExtensions = allExtensions.stream().distinct().toArray(String[]::new);
        File file = UIHelper.showFileDialog(
                previewTable,
                "Select matrix file to add",
                false,
                uniqueExtensions
        );
        if (file == null) {
            return;
        }

        UIHelper.executeSafely(
                previewTable,
                "Merge matrix",
                () -> {
                    SerializerType type = SerializerDetector.detect(file);
                    Serializer serializer = SerializerFactory.get(type);
                    DataContainer container = (DataContainer) serializer.load(file, DataContainer.class);

                    if (!UserTypeName.SPARSE_MATRIX.getTypeName().equals(container.typeName)) {
                        throw new IllegalArgumentException(
                                "File contains wrong data type. Expected: " + UserTypeName.SPARSE_MATRIX.getTypeName()
                        );
                    }

                    modifyEntries(() -> mergeWithFileData(container));

                    UIHelper.showInfo(previewTable, "Success", "Matrices added successfully!");
                },
                null
        );
    }

    private void mergeWithFileData(DataContainer container) throws Exception {
        SparseMatrix currentMatrix = MatrixEditorLogic.toMatrix(entries, new SparseMatrix());

        for (String line : container.data) {
            if (line.trim().isEmpty()) {
                continue;
            }

            SparseMatrix parsedMatrix = (SparseMatrix) type.deserialize(line);
            currentMatrix = currentMatrix.add(parsedMatrix);
        }

        entries.clear();
        for (MatrixElement el : currentMatrix.getEntries()) {
            entries.add(new MatrixEditorLogic.MatrixEntry(el.x, el.y, el.value));
        }
    }

    @FXML
    private void saveToMain() {
        UIHelper.executeSafely(previewTable, "Save matrix", () -> {
            if (entries.isEmpty()) {
                throw new IllegalArgumentException("Add at least one element to save");
            }

            SparseMatrix matrix = MatrixEditorLogic.toMatrix(entries, new SparseMatrix());
            markAsSaved();

            UIHelper.closeDialog(previewTable, matrix, onSaveCallback);
        }, null);
    }

    public void setOnSaveCallback(Consumer<SparseMatrix> callback) {
        this.onSaveCallback = callback;
    }

    public void preloadMatrix(SparseMatrix matrix) throws Exception {
        if (matrix == null) {
            return;
        }

        entries.clear();
        for (MatrixElement el : matrix.getEntries()) {
            entries.add(new MatrixEditorLogic.MatrixEntry(el.x, el.y, el.value));
        }

        modifyEntries(() -> {
        });
    }

    private boolean hasUnsavedChanges() {
        return hasChanges && !entries.isEmpty();
    }

    private void markAsChanged() {
        hasChanges = true;
    }

    private void markAsSaved() {
        hasChanges = false;
    }

    private boolean handleUnsavedChanges() {
        if (!hasUnsavedChanges()) {
            return true;
        }

        return UIHelper.showAlert(
                        previewTable,
                        "Unsaved changes",
                        "You have unsaved elements. Save before closing?",
                        Alert.AlertType.CONFIRMATION
                ).map(response -> {
                    if (response == ButtonType.OK) {
                        saveToMain();
                        return false;
                    }
                    return response == ButtonType.CANCEL;
                })
                .orElse(false);
    }

    private void updateSummary() {
        summaryLabel.setText(MatrixEditorLogic.formatSummary(entries));
    }

    private void clearInputFields() {
        xField.clear();
        yField.clear();
        valueField.clear();
        xField.requestFocus();
    }

    private void modifyEntries(Action action) throws Exception {
        action.run();
        updateSummary();
        markAsChanged();
    }
}
