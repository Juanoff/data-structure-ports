package com.juanoff.ui.factory;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StageFactory {
    public static Stage createModal(Node owner, String title, Parent content, boolean resizable) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner.getScene().getWindow());
        stage.setTitle(title);
        stage.setScene(new Scene(content));
        stage.setResizable(resizable);
        return stage;
    }
}
