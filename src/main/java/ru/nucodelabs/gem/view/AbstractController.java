package ru.nucodelabs.gem.view;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 * Контроллер, который не только инициализируется FXML-загрузчиком, но и знает как добыть свою сцену
 */
public abstract class AbstractController implements Initializable {
    protected abstract Stage getStage();
}