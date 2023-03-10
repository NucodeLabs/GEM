package ru.nucodelabs.gem.fxmodel.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.nucodelabs.gem.fxmodel.ObservableExperimentalData;
import ru.nucodelabs.gem.fxmodel.ObservableModelLayer;
import ru.nucodelabs.geo.ves.ExperimentalData;
import ru.nucodelabs.geo.ves.ModelLayer;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
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
}