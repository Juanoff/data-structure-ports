package com.juanoff.controller;

import com.juanoff.factory.SerializerFactory;
import com.juanoff.scala.factory.UserTypeFactory;
import com.juanoff.scala.model.DataStructure;
import com.juanoff.scala.model.MultiList;
import com.juanoff.scala.types.DoubleType;
import com.juanoff.scala.types.IntegerType;
import com.juanoff.scala.types.SparseMatrixType;
import com.juanoff.scala.types.StringType;
import com.juanoff.serialization.DataContainer;
import com.juanoff.serialization.Serializer;
import com.juanoff.serialization.SerializerType;
import com.juanoff.scala.types.UserType;
import com.juanoff.ui.handler.TypeOperationRegistry;
import com.juanoff.ui.util.UIHelper;
import com.juanoff.util.Config;
import com.juanoff.util.NumericValueParser;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainController {
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private ComboBox<String> typeSelector;
    @FXML
    private TextField inputField;
    @FXML
    private TextField indexField;
    @FXML
    private ListView<String> listView;

    @FXML
    private Button addBtn;
    @FXML
    private Button insertBtn;
    @FXML
    private Button removeBtn;
    @FXML
    private Button sortBtn;
    @FXML
    private Button searchBtn;
    @FXML
    private Button balanceBtn;
    @FXML
    private Button clearAllBtn;

    @FXML
    private Menu saveMenu;
    @FXML
    private Menu loadMenu;

    private final UserTypeFactory factory = new UserTypeFactory();
    private final Map<String, DataStructure> dataMap = new HashMap<>();

    private DataStructure data;
    private UserType type;

    @FXML
    public void initialize() {
        factory.register(new IntegerType());
        factory.register(new DoubleType());
        factory.register(new StringType());
        factory.register(new SparseMatrixType());
        
        typeSelector.getItems().addAll(factory.getTypeNameList());

        BooleanBinding noTypeSelected = Bindings.isNull(typeSelector.getSelectionModel().selectedItemProperty());
        addBtn.disableProperty().bind(noTypeSelected);
        insertBtn.disableProperty().bind(noTypeSelected);
        removeBtn.disableProperty().bind(noTypeSelected);
        sortBtn.disableProperty().bind(noTypeSelected);
        searchBtn.disableProperty().bind(noTypeSelected);
        balanceBtn.disableProperty().bind(noTypeSelected);
        clearAllBtn.disableProperty().bind(noTypeSelected);
        saveMenu.disableProperty().bind(noTypeSelected);
        loadMenu.disableProperty().bind(noTypeSelected);

        typeSelector.setOnAction(e -> {
            String typeName = typeSelector.getValue();
            if (typeName == null) {
                return;
            }

            type = factory.getBuilderByName(typeName);
            data = dataMap.computeIfAbsent(typeName, k -> createDataStructure());
            updateUIForType();
            refresh();
        });

        addBtn.setTooltip(new Tooltip("Select data type and enter value"));

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int index = listView.getSelectionModel().getSelectedIndex();
                if (index >= 0 && type != null) {
                    Object item = data.get(index);
                    String typeName = type.typeName();
                    if (TypeOperationRegistry.hasHandler(typeName, TypeOperationRegistry.Operation.EDIT)) {
                        TypeOperationRegistry.execute(
                                typeName,
                                TypeOperationRegistry.Operation.EDIT,
                                listView,
                                item,
                                result -> {
                                    if (result != null) {
                                        data.remove(index);
                                        data.insert(index, result);
                                        refresh();
                                        log.info("Matrix at index {} updated", index);
                                    }
                                }
                        );
                    }
                }
            }
        });
    }

    private DataStructure createDataStructure() {
        return new MultiList(Config.DEFAULT_MULTI_LIST_CHUNK_SIZE);
    }

    private void updateUIForType() {
        boolean noType = (type == null);
        boolean hasSpecialEditor = !noType && TypeOperationRegistry.hasHandler(
                type.typeName(),
                TypeOperationRegistry.Operation.EDIT
        );

        boolean shouldDisable = noType || hasSpecialEditor;
        inputField.setDisable(shouldDisable);
        inputField.setPromptText(hasSpecialEditor ? "Use Add button" : "Value");
    }

    @FXML
    private void add() {
        UIHelper.executeSafely(
                listView,
                "Operation add",
                () -> {
                    String typeName = type.typeName();
                    if (TypeOperationRegistry.hasHandler(typeName, TypeOperationRegistry.Operation.EDIT)) {
                        TypeOperationRegistry.execute(
                                typeName,
                                TypeOperationRegistry.Operation.EDIT,
                                listView,
                                null,
                                value -> {
                                    if (value != null) {
                                        data.add(value);
                                    }
                                }
                        );
                    } else {
                        String inputText = inputField.getText();
                        Object parsedValue = type.parseValue(inputText);
                        Object value = type.clone(parsedValue);
                        data.add(value);
                        log.info("Added element: {}", inputText);
                    }
                },
                this::refresh
        );
    }

    @FXML
    private void insert() {
        UIHelper.executeSafely(
                listView,
                "Operation insert",
                () -> {
                    String typeName = type.typeName();
                    int index = getIndexForOperation();
                    if (TypeOperationRegistry.hasHandler(typeName, TypeOperationRegistry.Operation.EDIT)) {
                        TypeOperationRegistry.execute(
                                typeName,
                                TypeOperationRegistry.Operation.EDIT,
                                listView,
                                null,
                                value -> UIHelper.executeSafely(
                                        listView,
                                        "Insert matrix element",
                                        () -> {
                                            if (value != null) {
                                                data.insert(index, value);
                                                log.info("Inserted matrix at index {}", index);
                                            }
                                        },
                                        this::refresh
                                )
                        );
                    } else {
                        String inputText = inputField.getText();
                        Object parsedValue = type.parseValue(inputText);
                        Object value = type.clone(parsedValue);
                        data.insert(index, value);
                        log.info("Inserted element: {}", inputText);
                    }
                },
                this::refresh
        );
    }

    @FXML
    private void remove() {
        UIHelper.executeSafely(
                listView,
                "Operation remove",
                () -> {
                    int index = getIndexForOperation();
                    data.remove(index);
                },
                this::refresh
        );
    }

    private int getIndexForOperation() {
        int index = listView.getSelectionModel().getSelectedIndex();
        return index == -1 ? parseIndex() : index;
    }

    @FXML
    private void sort() {
        UIHelper.executeSafely(
                listView,
                "Operation sort",
                () -> data.sort(type.getTypeComparator()),
                this::refresh
        );
    }

    @FXML
    private void search() {
        String searchText = inputField.getText().trim();
        UIHelper.executeSafely(
                listView,
                "Operation search",
                () -> {
                    String typeName = type.typeName();
                    if (TypeOperationRegistry.hasHandler(typeName, TypeOperationRegistry.Operation.SEARCH)) {
                        Object elementToSearch = getCurrentElementForSearch();
                        TypeOperationRegistry.execute(
                                typeName,
                                TypeOperationRegistry.Operation.SEARCH,
                                listView,
                                elementToSearch,
                                null
                        );
                    } else {
                        if (searchText.isBlank()) {
                            throw new IllegalArgumentException("Enter search value");
                        }

                        int foundIndex = -1;
                        for (int i = 0; i < data.size(); i++) {
                            Object item = data.get(i);
                            String itemStr = item == null ? "" : item.toString();
                            if (itemStr.contains(searchText)) {
                                foundIndex = i;
                                break;
                            }
                        }

                        if (foundIndex == -1) {
                            UIHelper.showInfo(listView, "Search", "Value not found");
                        } else {
                            listView.getSelectionModel().select(foundIndex);
                            listView.scrollTo(foundIndex);
                            String message = String.format("Value '%s' found at index %d", searchText, foundIndex);
                            UIHelper.showInfo(listView, "Found", message);
                            log.info("Search: found '{}' at index {}", searchText, foundIndex);
                        }
                    }
                },
                this::refresh
        );
    }

    private Object getCurrentElementForSearch() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < data.size()) {
            return data.get(selectedIndex);
        }
        return data.size() > 0 ? data.get(0) : null;
    }

    @FXML
    private void balance() {
        UIHelper.executeSafely(
                listView,
                "Operation balance",
                () -> data = data.balance(),
                this::refresh
        );
    }

    @FXML
    private void clearAll() {
        UIHelper.executeSafely(
                listView,
                "Clear all elements",
                () -> {
                    if (data.size() == 0) {
                        throw new IllegalStateException("List is already empty");
                    }

                    if (!UIHelper.confirm(
                            listView,
                            "Confirm Clear",
                            "Remove all " + data.size() + " elements?"
                    )) {
                        return;
                    }

                    data.clear();

                    log.info("Cleared all elements");
                },
                this::refresh
        );
    }

    @FXML
    private void saveJson() {
        save(SerializerType.JSON.name());
    }

    @FXML
    private void loadJson() {
        load(SerializerType.JSON.name());
    }

    @FXML
    private void saveXml() {
        save(SerializerType.XML.name());
    }

    @FXML
    private void loadXml() {
        load(SerializerType.XML.name());
    }

    @FXML
    private void saveBin() {
        save(SerializerType.BIN.name());
    }

    @FXML
    private void loadBin() {
        load(SerializerType.BIN.name());
    }

    @FXML
    private void saveText() {
        save(SerializerType.TEXT.name());
    }

    @FXML
    private void loadText() {
        load(SerializerType.TEXT.name());
    }

    private void save(String format) {
        UIHelper.executeSafely(
                listView,
                "Operation save",
                () -> {
                    DataContainer container = new DataContainer();
                    container.typeName = type.typeName();
                    container.data = new ArrayList<>();

                    data.forEach(obj -> container.data.add(type.serialize(obj)));

                    SerializerType serializerType = SerializerType.valueOf(format);
                    Serializer serializer = SerializerFactory.get(serializerType);
                    File file = UIHelper.showFileDialog(
                            listView,
                            "Save file",
                            true,
                            serializerType.getExtensions()
                    );
                    if (file != null) {
                        serializer.save(container, file);
                        log.info("File saved successfully: {}", file.getAbsolutePath());
                        UIHelper.showInfo(listView, "Success", "File saved successfully");
                    }
                },
                this::refresh
        );
    }

    private void load(String format) {
        UIHelper.executeSafely(
                listView,
                "Operation load",
                () -> {
                    SerializerType serializerType = SerializerType.valueOf(format);
                    File file = UIHelper.showFileDialog(
                            listView,
                            "Open file",
                            false,
                            serializerType.getExtensions()
                    );
                    if (file == null) {
                        return;
                    }
                    if (!file.exists() || file.length() == 0) {
                        throw new IllegalArgumentException("File is empty or does not exist");
                    }

                    log.info("Loading file: {}", file.getAbsolutePath());

                    Serializer serializer = SerializerFactory.get(serializerType);
                    DataContainer container = (DataContainer) serializer.load(file, DataContainer.class);
                    if (container.typeName == null) {
                        throw new IllegalStateException("Type name is not contained in file");
                    }
                    if (!type.typeName().equals(container.typeName)) {
                        throw new IllegalStateException("File contains wrong data type!");
                    }

                    type = factory.getBuilderByName(container.typeName);

                    data = createDataStructure();
                    for (String s : container.data) {
                        data.add(type.deserialize(s));
                    }

                    dataMap.put(container.typeName, data);
                    typeSelector.setValue(container.typeName);
                },
                this::refresh
        );
    }

    private int parseIndex() {
        String input = indexField.getText();
        if (input.isBlank()) {
            throw new IllegalArgumentException("Enter index or select element");
        }
        return NumericValueParser.parseInt(input, "Index");
    }

    private void refresh() {
        listView.getItems().clear();

        data.forEach(o -> {
            String text = o.toString();
            if (text.length() > 150) {
                text = text.substring(0, 147) + "...";
            }
            listView.getItems().add(text);
        });
    }
}
