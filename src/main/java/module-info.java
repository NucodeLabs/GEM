
module ru.nucodelabs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens ru.nucodelabs.gem;
    opens ru.nucodelabs.gem.view;
    opens ru.nucodelabs.gem.view.usercontrols.vescurves;
    opens ru.nucodelabs.gem.view.usercontrols.vesmisfitsplit;
    opens ru.nucodelabs.gem.view.main;
    opens ru.nucodelabs.gem.core;
    opens ru.nucodelabs.gem.view.usercontrols.misfitstacks;
    opens ru.nucodelabs.gem.view.usercontrols.mainmenubar;
    opens ru.nucodelabs.mvvm;
}