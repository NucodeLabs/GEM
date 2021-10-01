module ru.nucodelabs.gem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;



    opens ru.nucodelabs.gem to javafx.fxml;
    exports ru.nucodelabs.gem;
}