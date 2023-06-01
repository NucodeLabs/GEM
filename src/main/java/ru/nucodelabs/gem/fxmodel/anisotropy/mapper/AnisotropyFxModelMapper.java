package ru.nucodelabs.gem.fxmodel.anisotropy.mapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mapstruct.*;
import ru.nucodelabs.gem.fxmodel.anisotropy.*;
import ru.nucodelabs.gem.fxmodel.map.ObservableWgs;
import ru.nucodelabs.geo.anisotropy.*;
import ru.nucodelabs.geo.anisotropy.calc.map.Wgs;

@Mapper(
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public abstract class AnisotropyFxModelMapper {
    protected <T> ObservableList<T> createObservableList() {
        return FXCollections.observableArrayList();
    }

    public abstract ObservablePoint toObservable(Point point);

    public abstract void updateObservable(@MappingTarget ObservablePoint observablePoint, Point src);

    public ObservableWgs toObservable(Wgs wgs) {
        if (wgs == null) {
            return null;
        }
        return new ObservableWgs(wgs.getLongitudeInDegrees(), wgs.getLatitudeInDegrees());
    }

    @Mapping(target = "isHidden", source = "hidden")
    public abstract ObservableSignal toObservable(Signal signal);

    public abstract ObservableAzimuthSignals toObservable(AzimuthSignals azimuthSignals);

    public abstract ObservableModelLayer toObservable(ModelLayer modelLayer);

    @Mapping(target = "isFixed", source = "fixed")
    public abstract ObservableFixableValue<Double> toObservable(FixableValue<Double> fixableValue);

    public abstract Wgs toModel(ObservableWgs observableWgs);
}
