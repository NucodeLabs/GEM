package ru.nucodelabs.gem.view;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 * Контроллер, который не только инициализируется FXML-загрузчиков, но и знает как добыть свою сцену
 */
public abstract class Controller implements Initializable {
    protected abstract Stage getStage();
}
