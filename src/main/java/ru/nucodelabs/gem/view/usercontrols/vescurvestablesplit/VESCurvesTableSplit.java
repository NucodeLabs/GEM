package ru.nucodelabs.gem.view.usercontrols.vescurvestablesplit;

import javafx.fxml.FXML;
import ru.nucodelabs.gem.view.usercontrols.vesmisfitsplit.VESMisfitSplit;
import ru.nucodelabs.mvvm.VBUserControl;

public class VESCurvesTableSplit extends VBUserControl {
    @FXML
    private VESMisfitSplit vesMisfitSplit;

    public VESMisfitSplit getVesMisfitSplit() {
        return vesMisfitSplit;
    }
}
