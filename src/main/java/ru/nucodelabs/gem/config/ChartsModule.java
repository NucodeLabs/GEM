package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import ru.nucodelabs.geo.forward.ForwardSolver;
import ru.nucodelabs.geo.ves.calc.graph.MisfitsFunction;

public class ChartsModule extends AbstractModule {

    @Provides
    MisfitsFunction misfitValuesFactory(ForwardSolver forwardSolver) {
        return MisfitsFunction.createDefault(forwardSolver);
    }
}
