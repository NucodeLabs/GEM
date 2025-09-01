package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import ru.nucodelabs.geo.forward.ForwardSolver;
import ru.nucodelabs.geo.ves.calc.graph.MathVesNativeMisfitsFunction;
import ru.nucodelabs.geo.ves.calc.graph.MisfitsFunction;

public class ChartsModule extends AbstractModule {

    @Provides
    @Singleton
    MisfitsFunction misfitValuesFactory(ForwardSolver forwardSolver) {
        return new MathVesNativeMisfitsFunction(forwardSolver);
    }
}
