package ru.nucodelabs.gem.fxmodel.anisotropy.mapper;

import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservablePoint;
import ru.nucodelabs.geo.anisotropy.Point;

@Mapper(
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        mappingControl = DeepClone.class
)
public abstract class AnisotropyFxModelUpdater extends AnisotropyCommonConfig {

    public abstract void updateObservable(@MappingTarget ObservablePoint observablePoint, Point src);

}
