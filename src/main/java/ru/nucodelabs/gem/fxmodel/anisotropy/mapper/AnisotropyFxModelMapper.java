package ru.nucodelabs.gem.fxmodel.anisotropy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableAzimuthSignals;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservableModelLayer;
import ru.nucodelabs.gem.fxmodel.anisotropy.ObservablePoint;
import ru.nucodelabs.gem.fxmodel.map.ObservableWgs;
import ru.nucodelabs.geo.anisotropy.AzimuthSignals;
import ru.nucodelabs.geo.anisotropy.ModelLayer;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.geo.anisotropy.calc.map.Wgs;

@Mapper(
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public abstract class AnisotropyFxModelMapper extends AnisotropyCommonConfig {

    public abstract ObservablePoint toObservable(Point point);

    public abstract ObservableAzimuthSignals toObservable(AzimuthSignals azimuthSignals);

    public abstract ObservableModelLayer toObservable(ModelLayer modelLayer);

    public abstract Wgs toModel(ObservableWgs observableWgs);
}
