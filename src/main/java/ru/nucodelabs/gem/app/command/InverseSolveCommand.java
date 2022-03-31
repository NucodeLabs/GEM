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
            List<Picket> state,
            AlertsFactory alertsFactory,
            Stage stage) {
        super(state);
        this.picketIndex = picketIndex;
        this.alertsFactory = alertsFactory;
        this.stage = stage;
    }

    @Override
    public boolean execute() {
        Picket picket = state.get(picketIndex);
        InverseSolver inverseSolver = new InverseSolver(picket);

        try {
            Picket newPicket = new Picket(
                    picket.name(),
                    picket.experimentalData(),
                    inverseSolver.getOptimizedModelData()
            );
            state.set(picketIndex, newPicket);
        } catch (Exception e) {
            alertsFactory.unsafeDataAlert(picket.name(), stage).show();
            return false;
        }

        return true;
    }
}
