package ru.nucodelabs.gem.fxmodel.anisotropy.mapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableAzimuthSignals;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableModelLayer;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservablePoint;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableSignal;
import ru.nucodelabs.geo.anisotropy.AzimuthSignals;
import ru.nucodelabs.geo.anisotropy.ModelLayer;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.geo.anisotropy.Signal;

@Mapper(
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class AnisotropyFxModelMapper {
    protected <T> ObservableList<T> createObservableList() {
        return FXCollections.observableArrayList();
    }

    public abstract ObservablePoint toObservable(Point point);

    public abstract void updateObservable(@MappingTarget ObservablePoint observablePoint, Point src);

    @Mapping(target = "isHidden", source = "hidden")
    public abstract ObservableSignal toObservable(Signal signal);

    public abstract ObservableAzimuthSignals toObservable(AzimuthSignals azimuthSignals);

    @Mapping(target = "isFixedResistance", source = "fixedResistance")
    @Mapping(target = "isFixedPower", source = "fixedPower")
    public abstract ObservableModelLayer toObservable(ModelLayer modelLayer);
}
