package ru.nucodelabs.gem.app.project.impl.anisotropy.cloner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import ru.nucodelabs.gem.app.project.Project;
import ru.nucodelabs.geo.anisotropy.FixableValue;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.geo.anisotropy.Signal;
import ru.nucodelabs.geo.anisotropy.Signals;

@Mapper(
    componentModel = MappingConstants.ComponentModel.JSR330,
        mappingControl = DeepClone.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public abstract class PointProjectCloner {
    @Mapping(target = "copy", ignore = true)
    public abstract Project<Point> deepCopy(Project<Point> project);

    @Mapping(target = "isHidden", source = "hidden")
    protected abstract Signal cloneSignal(Signal signal);

    @Mapping(target = "isFixed", source = "fixed")
    protected abstract FixableValue<Double> cloneFixableValue(FixableValue<Double> fixableValue);

    protected Signals cloneSignals(Signals signals) {
        return new Signals(
                signals.getSortedSignals().stream()
                        .map(this::cloneSignal)
                        .toList()
        );
    }
}
