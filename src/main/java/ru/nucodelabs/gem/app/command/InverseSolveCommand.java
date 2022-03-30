package ru.nucodelabs.gem.app.command;

import javafx.stage.Stage;
import ru.nucodelabs.algorithms.inverse_solver.InverseSolver;
import ru.nucodelabs.data.ves.Picket;
import ru.nucodelabs.gem.view.AlertsFactory;

import javax.inject.Inject;
import java.util.List;

public class InverseSolveCommand extends AbstractCommand {

    private final AlertsFactory alertsFactory;
    private final Stage stage;
    private final int picketIndex;

    @Inject
    public InverseSolveCommand(
            int picketIndex,
            List<Picket> currentState,
            AlertsFactory alertsFactory,
            Stage stage) {
        super(currentState);
        this.picketIndex = picketIndex;
        this.alertsFactory = alertsFactory;
        this.stage = stage;
    }

    @Override
    public boolean execute() {
        Picket picket = currentState.get(picketIndex);
        InverseSolver inverseSolver = new InverseSolver(picket);

        try {
            Picket newPicket = new Picket(
                    picket.name(),
                    picket.experimentalData(),
                    inverseSolver.getOptimizedModelData()
            );
            currentState.set(picketIndex, newPicket);
        } catch (Exception e) {
            alertsFactory.unsafeDataAlert(picket.name(), stage).show();
            return false;
        }

        return true;
    }
}
