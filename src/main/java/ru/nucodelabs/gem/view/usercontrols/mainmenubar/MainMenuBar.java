package ru.nucodelabs.gem.view.usercontrols.mainmenubar;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import ru.nucodelabs.mvvm.VBUserControl;

/**
 * Main Menu Bar
 */
public class MainMenuBar extends VBUserControl {

    @FXML
    private MenuItem menuFileOpenEXP;
    @FXML
    private MenuItem menuFileOpenMOD;
    @FXML
    private CheckMenuItem menuViewLegendsVESCurves;
    @FXML
    private MenuBar menuBar;

    public MainMenuBar() {
        super();
        var osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            menuBar.setUseSystemMenuBar(true);
        }
        menuFileOpenEXP.setAccelerator(new KeyCodeCombination(
                KeyCode.O,
                KeyCombination.SHORTCUT_DOWN
        ));
        menuFileOpenMOD.setAccelerator(new KeyCodeCombination(
                KeyCode.O,
                KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN
        ));
    }

    public MenuItem getMenuFileOpenEXP() {
        return menuFileOpenEXP;
    }

    public MenuItem getMenuFileOpenMOD() {
        return menuFileOpenMOD;
    }

    public CheckMenuItem getMenuViewLegendsVESCurves() {
        return menuViewLegendsVESCurves;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
