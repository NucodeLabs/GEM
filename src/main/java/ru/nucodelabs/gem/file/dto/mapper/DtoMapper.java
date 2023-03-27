package ru.nucodelabs.gem.file.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.nucodelabs.gem.file.dto.anisotropy.AzimuthSignalsDto;
import ru.nucodelabs.gem.file.dto.anisotropy.ModelLayerDto;
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto;
import ru.nucodelabs.gem.file.dto.anisotropy.SignalDto;
import ru.nucodelabs.geo.anisotropy.*;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Mapper(
        componentModel = "jsr330",
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public abstract class DtoMapper {

    @Mapping(target = "isFixedResistance", source = "fixedPower", defaultValue = "false")
    @Mapping(target = "isFixedPower", source = "fixedResistance", defaultValue = "false")
    public abstract ModelLayer fromDto(ModelLayerDto dto);

    @Mapping(
            target = "resistanceApparent",
            defaultExpression = "java(ru.nucodelabs.geo.ves.calc.VesKt.rhoA(dto.getAb2(), dto.getMn2(), dto.getAmperage(), dto.getVoltage()))"
    )
    @Mapping(target = "errorResistanceApparent", defaultExpression = "java(ru.nucodelabs.geo.anisotropy.DefaultValues.DEFAULT_ERROR)")
    @Mapping(target = "isHidden", source = "hidden", defaultValue = "false")
    public abstract Signal fromDto(SignalDto dto);

    @Mapping(target = "z", defaultExpression = "java(ru.nucodelabs.geo.anisotropy.DefaultValues.DEFAULT_POINT_Z)")
    @Mapping(target = "comment", defaultExpression = "java(ru.nucodelabs.geo.anisotropy.DefaultValues.DEFAULT_POINT_COMMENT)")
    public abstract Point fromDto(PointDto dto);

    public abstract AzimuthSignals fromDto(AzimuthSignalsDto dto);

    protected Signals mapSignals(List<SignalDto> dto) {
        return new Signals(emptyIfNull(dto).stream().map(this::fromDto).toList());
    }

    @Mapping(target = "isFixedResistance", source = "fixedResistance")
    @Mapping(target = "isFixedPower", source = "fixedPower")
    public abstract ModelLayerDto toDto(ModelLayer modelLayer);

    @Mapping(target = "isHidden", source = "hidden")
    public abstract SignalDto toDto(Signal signal);

    @Mapping(target = "signals", source = "signals.effectiveSignals")
    public abstract AzimuthSignalsDto toDto(AzimuthSignals azimuthSignals);

    public abstract PointDto toDto(Point point);
}
