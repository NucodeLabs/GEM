package ru.nucodelabs.gem.view;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class Controller implements Initializable {
    @Override
    public abstract void initialize(URL location, ResourceBundle resources);

    protected abstract Stage getStage();
}
