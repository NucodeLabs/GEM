package ru.nucodelabs.gem.fxmodel.anisotropy.mapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableFixableValue;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableSignal;
import ru.nucodelabs.gem.fxmodel.map.ObservableWgs;
import ru.nucodelabs.geo.anisotropy.FixableValue;
import ru.nucodelabs.geo.anisotropy.Signal;
import ru.nucodelabs.geo.anisotropy.calc.map.Wgs;

@MapperConfig(
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public abstract class AnisotropyCommonConfig {
    public ObservableWgs toObservable(Wgs wgs) {
        if (wgs == null) {
            return null;
        }
        return new ObservableWgs(wgs.getLongitudeInDegrees(), wgs.getLatitudeInDegrees());
    }

    @Mapping(target = "isHidden", source = "hidden")
    public abstract ObservableSignal toObservable(Signal signal);

    @Mapping(target = "isFixed", source = "fixed")
    public abstract ObservableFixableValue<Double> toObservable(FixableValue<Double> fixableValue);

    protected <T> ObservableList<T> createObservableList() {
        return FXCollections.observableArrayList();
    }
}
