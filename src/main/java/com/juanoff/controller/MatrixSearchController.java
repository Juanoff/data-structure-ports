package com.juanoff.controller;

import com.juanoff.kotlin.types.MatrixElement;
import com.juanoff.kotlin.types.SparseMatrix;
import com.juanoff.ui.util.UIHelper;
import com.juanoff.util.NumericValueParser;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatrixSearchController {
    private static final Logger log = LoggerFactory.getLogger(MatrixSearchController.class);
    private static final double EPSILON = 0.0001;

    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    @FXML
    private TextField valueField;
    @FXML
    private CheckBox searchByX;
    @FXML
    private CheckBox searchByY;
    @FXML
    private CheckBox searchByValue;
    @FXML
    private CheckBox exactMatch;
    @FXML
    private TableView<MatrixElement> resultsTable;
    @FXML
    private TableColumn<MatrixElement, Integer> colX;
    @FXML
    private TableColumn<MatrixElement, Integer> colY;
    @FXML
    private TableColumn<MatrixElement, Double> colValue;
    @FXML
    private Label statusLabel;

    private SparseMatrix matrix;
    private Consumer<Object> onResultFound;
    private final ObservableList<MatrixElement> results = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupContextMenu();
        resultsTable.setItems(results);
        updateStatus();
    }

    private void setupTableColumns() {
        colX.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getX()).asObject()
        );
        colY.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getY()).asObject()
        );
        colValue.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getValue()).asObject()
        );
    }

    private void setupContextMenu() {
        MenuItem copyRow = new MenuItem("Copy Row");
        copyRow.setDisable(true);
        copyRow.setOnAction(e -> copySelectedRow());

        MenuItem copyAll = new MenuItem("Copy All Results");
        copyAll.setDisable(true);
        copyAll.setOnAction(e -> copyAllResults());

        resultsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) ->
                        copyRow.setDisable(newVal == null)
        );

        results.addListener((ListChangeListener<MatrixElement>) change ->
                copyAll.setDisable(results.isEmpty())
        );

        resultsTable.setContextMenu(new ContextMenu(copyRow, copyAll));
    }

    public void setMatrix(SparseMatrix matrix) {
        this.matrix = matrix;
        results.clear();
        updateStatus();
    }

    public void setOnResultFound(Consumer<Object> callback) {
        this.onResultFound = callback;
    }

    @FXML
    private void search() {
        UIHelper.executeSafely(
                resultsTable,
                "Find element",
                () -> {
                    if (matrix == null) {
                        throw new IllegalArgumentException("No matrix loaded for search");
                    }

                    results.clear();

                    Integer searchX = parseField(searchByX, xField, "X");
                    Integer searchY = parseField(searchByY, yField, "Y");
                    Double searchValue = parseDoubleField(searchByValue, valueField, "Value");

                    boolean exact = exactMatch.isSelected();
                    int matchCount = 0;
                    for (MatrixElement el : matrix.getEntries()) {
                        if (matchesCriteria(el, searchX, searchY, searchValue, exact)) {
                            results.add(el);
                            matchCount++;
                        }
                    }

                    log.info("Search completed: {} matches found", matchCount);
                    updateStatus();
                },
                null
        );
    }

    private Integer parseField(CheckBox checkBox, TextField field, String name) {
        return checkBox.isSelected() && !field.getText().isBlank()
                ? NumericValueParser.parseInt(field.getText(), name)
                : null;
    }

    private Double parseDoubleField(CheckBox checkBox, TextField field, String name) {
        return checkBox.isSelected() && !field.getText().isBlank()
                ? NumericValueParser.parseDouble(field.getText(), name)
                : null;
    }

    private boolean matchesCriteria(MatrixElement el, Integer x, Integer y, Double value, boolean exact) {
        if (x != null && el.getX() != x) return false;
        if (y != null && el.getY() != y) return false;
        if (value != null) {
            return exact ? el.getValue() == value : Math.abs(el.getValue() - value) <= EPSILON;
        }
        return true;
    }

    private void copySelectedRow() {
        MatrixElement selected = resultsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        String text = formatElement(selected);
        Clipboard.getSystemClipboard().setContent(createClipboardContent(text));
        UIHelper.showInfo(resultsTable, "Copied", "Row data copied");
        log.info("Copied matrix element: {}", text);
    }

    private void copyAllResults() {
        if (results.isEmpty()) {
            return;
        }

        String header = "X,Y,Value\n";
        String rows = results.stream()
                .map(el -> String.format("%d,%d,%.6f", el.getX(), el.getY(), el.getValue()))
                .collect(Collectors.joining("\n"));

        String csv = header + rows;
        Clipboard.getSystemClipboard().setContent(createClipboardContent(csv));
        UIHelper.showInfo(resultsTable, "Copied", "All results copied as CSV");
        log.info("Copied {} matrix elements as CSV", results.size());
    }

    private String formatElement(MatrixElement el) {
        return String.format("(%d, %d) = %.6f", el.getX(), el.getY(), el.getValue());
    }

    private ClipboardContent createClipboardContent(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        return content;
    }

    @FXML
    private void cancel() {
        log.debug("Closing matrix search dialog");
        UIHelper.closeDialog(resultsTable, null, onResultFound);
    }

    private void updateStatus() {
        int count = results.size();
        if (matrix == null) {
            statusLabel.setText("No matrix loaded");
        } else if (count == 0) {
            statusLabel.setText("No matches found");
        } else {
            statusLabel.setText(String.format("Found %d element%s", count, count == 1 ? "" : "s"));
        }
    }
}
