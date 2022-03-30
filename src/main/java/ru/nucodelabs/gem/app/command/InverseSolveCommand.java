package ru.nucodelabs.gem.app.command;

import javafx.beans.property.ObjectProperty;
import javafx.stage.Stage;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;

public class InverseSolveCommand extends AbstractPicketCommand {

    private final AlertsFactory alertsFactory;
    private final Stage stage;

    @Inject
    public InverseSolveCommand(
            ObjectProperty<Picket> picket,
            AlertsFactory alertsFactory,
            Stage stage) {
        super(picket);
        this.alertsFactory = alertsFactory;
        this.stage = stage;
    }

    @Override
    public boolean execute() {
        InverseSolver inverseSolver = new InverseSolver(picket.get());

        try {
            Picket newPicket = new Picket(
                    picket.get().name(),
                    picket.get().experimentalData(),
                    inverseSolver.getOptimizedModelData()
            );
            picket.set(newPicket);
        } catch (Exception e) {
            alertsFactory.unsafeDataAlert(picket.get().name(), stage).show();
            return false;
        }

        return true;
    }
}
