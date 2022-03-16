package ru.nucodelabs.gem.view;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

public abstract class Controller implements Initializable {
    protected abstract Stage getStage();
}
