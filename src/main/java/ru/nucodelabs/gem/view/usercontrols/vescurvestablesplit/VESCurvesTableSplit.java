package ru.nucodelabs.gem.view.usercontrols.vescurvestablesplit;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.mvvm.VBUserControl;

public class VESCurvesTableSplit extends VBUserControl {
    @FXML
    public Button prevButton;
    @FXML
    public Text text;
    @FXML
    public Button nextButton;
    @FXML
    public MisfitStacks misfitStacks;
    @FXML
    public VESCurves vesCurves;

    public Button getPrevButton() {
        return prevButton;
    }

    public Text getText() {
        return text;
    }

    public Button getNextButton() {
        return nextButton;
    }

    public MisfitStacks getMisfitStacks() {
        return misfitStacks;
    }

    public VESCurves getVesCurves() {
        return vesCurves;
    }
}
