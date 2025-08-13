package ru.nucodelabs.gem.file.dto.mapper;

import org.mapstruct.*;
import ru.nucodelabs.gem.file.dto.anisotropy.*;
import ru.nucodelabs.geo.anisotropy.*;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Mapper(
    componentModel = MappingConstants.ComponentModel.JSR330,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public abstract class DtoMapper {

    @Mapping(
            target = "resistanceApparent",
            defaultExpression = "java(ru.nucodelabs.geo.ves.calc.VesKt.rhoA(dto.getAb2(), dto.getMn2(), dto.getAmperage(), dto.getVoltage()))"
    )
    @Mapping(target = "errorResistanceApparent", defaultExpression = "java(ru.nucodelabs.geo.anisotropy.DefaultValues.DEFAULT_ERROR)")
    @Mapping(target = "isHidden", source = "hidden", defaultValue = "false")
    protected abstract Signal fromDto(SignalDto dto);

    @Mapping(target = "z", defaultExpression = "java(ru.nucodelabs.geo.anisotropy.DefaultValues.DEFAULT_POINT_Z)")
    @Mapping(target = "comment", defaultExpression = "java(ru.nucodelabs.geo.anisotropy.DefaultValues.DEFAULT_POINT_COMMENT)")
    public abstract Point fromDto(PointDto dto);

    @Mapping(target = "isFixed", source = "fixed")
    protected abstract FixableValue<Double> fromDto(FixableDoubleValueDto fixableDoubleValueDto);

    protected Signals mapSignals(List<SignalDto> dto) {
        return new Signals(emptyIfNull(dto).stream().map(this::fromDto).toList());
    }

    @Mapping(target = "isFixed", source = "fixed")
    protected abstract FixableDoubleValueDto toDto(FixableValue<Double> fixableValue);

    @Mapping(target = "isHidden", source = "hidden")
    protected abstract SignalDto toDto(Signal signal);

    @Mapping(target = "signals", source = "signals.sortedSignals")
    protected abstract AzimuthSignalsDto toDto(AzimuthSignals azimuthSignals);

    public abstract PointDto toDto(Point point);

    public abstract ModelLayerDto toDto(ModelLayer modelLayer);
}
