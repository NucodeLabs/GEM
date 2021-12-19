package ru.nucodelabs.gem.view.usercontrols.vesmisfitsplit;

import javafx.fxml.FXML;
import ru.nucodelabs.gem.view.usercontrols.misfitstacks.MisfitStacks;
import ru.nucodelabs.gem.view.usercontrols.vescurves.VESCurves;
import ru.nucodelabs.mvvm.VBUserControl;

/**
 * Vertical Split Pane with Misfit Stacks on top and VES Curves under it.
 */
public class VESMisfitSplit extends VBUserControl {

    @FXML
    private VESCurves vesCurves;
    @FXML
    private MisfitStacks misfitStacks;

    public VESCurves getVesCurves() {
        return vesCurves;
    }

    public MisfitStacks getMisfitStacks() {
        return misfitStacks;
    }
}
