module GEM.main {
    opens ru.nucodelabs.gem.view;
    opens ru.nucodelabs.gem.view.main;
    opens ru.nucodelabs.gem.core;
    opens ru.nucodelabs.gem.view.usercontrols;
    opens ru.nucodelabs.gem.view.usercontrols.vestables;
    opens ru.nucodelabs.gem.view.usercontrols.vescurves;
    opens ru.nucodelabs.gem.view.usercontrols.misfitstacks;
    opens ru.nucodelabs.gem.view.usercontrols.placeholder;

    opens ru.nucodelabs.data.ves;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.naming;
    requires com.google.common;
}