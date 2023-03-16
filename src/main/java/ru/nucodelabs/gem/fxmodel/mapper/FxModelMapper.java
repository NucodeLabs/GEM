package ru.nucodelabs.gem.fxmodel.mapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableAzimuthSignals;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservablePoint;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableSignal;
import ru.nucodelabs.gem.fxmodel.ves.ObservableExperimentalData;
import ru.nucodelabs.gem.fxmodel.ves.ObservableModelLayer;
import ru.nucodelabs.geo.anisotropy.AzimuthSignals;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.geo.anisotropy.Signal;
import ru.nucodelabs.geo.ves.ExperimentalData;
import ru.nucodelabs.geo.ves.ModelLayer;

@Mapper
public abstract class FxModelMapper {
    @Mapping(target = "isHidden", source = "hidden")
    public abstract ObservableExperimentalData toObservable(ExperimentalData experimentalData);

    @Mapping(target = "isHidden", source = "hidden")
    public abstract ExperimentalData toModel(ObservableExperimentalData experimentalData);

    @Mapping(target = "isFixedResistance", source = "fixedResistance")
    @Mapping(target = "isFixedPower", source = "fixedPower")
    public abstract ObservableModelLayer toObservable(ModelLayer modelLayer);

    @Mapping(target = "isFixedResistance", source = "fixedResistance")
    @Mapping(target = "isFixedPower", source = "fixedPower")
    public abstract ModelLayer toModel(ObservableModelLayer modelLayer);

    public abstract ObservablePoint toObservable(Point point);

    public abstract void updateObservable(@MappingTarget ObservablePoint observablePoint, Point src);

    @Mapping(target = "isHidden", source = "hidden")
    public abstract ObservableSignal toObservable(Signal signal);

    public abstract ObservableAzimuthSignals toObservable(AzimuthSignals azimuthSignals);

    public <T> ObservableList<T> createObservableList() {
        return FXCollections.observableArrayList();
    }
}