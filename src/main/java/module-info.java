module GEM.main {
    opens ru.nucodelabs.gem.view;
    opens ru.nucodelabs.gem.view.main;
    opens ru.nucodelabs.gem.core;
    opens ru.nucodelabs.gem.view.usercontrols;
    opens ru.nucodelabs.gem.view.usercontrols.placeholder;
    opens ru.nucodelabs.data.ves;
    opens ru.nucodelabs.gem.view.charts;
    opens ru.nucodelabs.gem.view.tables;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.naming;
    requires com.google.common;
}