package ru.nucodelabs.gem.view.usercontrols.mainmenubar;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import ru.nucodelabs.gem.core.utils.OSDetector;
import ru.nucodelabs.mvvm.VBUserControl;

import java.util.ResourceBundle;

/**
 * Main Menu Bar
 */
public class MainMenuBar extends VBUserControl {
    @FXML
    private MenuItem menuFileSaveSection;
    @FXML
    private Menu menuFileSavePicket;
    @FXML
    private MenuItem menuFileClose;
    @FXML
    private MenuItem menuFileNewWindow;
    @FXML
    private MenuItem menuFileOpenEXP;
    @FXML
    private MenuItem menuFileOpenMOD;
    @FXML
    Menu menuView;
    @FXML
    private CheckMenuItem menuViewLegendsVESCurves;
    @FXML
    private MenuBar menuBar;

    public MainMenuBar() {
        super();
        if (new OSDetector().isMacOS()) {
            ResourceBundle uiProps = ResourceBundle.getBundle("ru/nucodelabs/gem/UI");
            CheckMenuItem useSystemMenu = new CheckMenuItem(uiProps.getString("useSystemMenu"));
            menuView.getItems().add(0, useSystemMenu);
            useSystemMenu.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
        }
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

    public MenuItem getMenuFileNewWindow() {
        return menuFileNewWindow;
    }

    public Menu getMenuView() {
        return menuView;
    }

    public MenuItem getMenuFileSaveSection() {
        return menuFileSaveSection;
    }

    public Menu getMenuFileSavePicket() {
        return menuFileSavePicket;
    }

    public MenuItem getMenuFileClose() {
        return menuFileClose;
    }
}
