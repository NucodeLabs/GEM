package ru.nucodelabs.gem.fxmodel.ves.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nucodelabs.gem.fxmodel.ves.ObservableExperimentalData;
import ru.nucodelabs.gem.fxmodel.ves.ObservableModelLayer;
import ru.nucodelabs.geo.ves.ExperimentalData;
import ru.nucodelabs.geo.ves.ModelLayer;

@Mapper
public abstract class VesFxModelMapper {
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