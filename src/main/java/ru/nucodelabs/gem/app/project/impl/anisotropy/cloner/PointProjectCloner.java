package ru.nucodelabs.gem.app.project.impl.anisotropy.cloner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import ru.nucodelabs.gem.app.project.Project;
import ru.nucodelabs.geo.anisotropy.ModelLayer;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.geo.anisotropy.Signal;
import ru.nucodelabs.geo.anisotropy.Signals;

@Mapper(
        componentModel = "jsr330",
        mappingControl = DeepClone.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public abstract class PointProjectCloner {
    @Mapping(target = "copy", ignore = true)
    public abstract Project<Point> deepCopy(Project<Point> project);

    @Mapping(target = "isHidden", source = "hidden")
    protected abstract Signal cloneSignal(Signal signal);

    @Mapping(target = "isFixedPower", source = "fixedPower")
    @Mapping(target = "isFixedResistance", source = "fixedResistance")
    protected abstract ModelLayer cloneLayer(ModelLayer layer);

    protected Signals cloneSignals(Signals signals) {
        return new Signals(
                signals.getSortedSignals().stream()
                        .map(this::cloneSignal)
                        .toList()
        );
    }
}
