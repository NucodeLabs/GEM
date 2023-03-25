package ru.nucodelabs.gem.file.dto.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.nucodelabs.gem.file.dto.anisotropy.AzimuthSignalsDto;
import ru.nucodelabs.gem.file.dto.anisotropy.ModelLayerDto;
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto;
import ru.nucodelabs.gem.file.dto.anisotropy.SignalDto;
import ru.nucodelabs.geo.anisotropy.AzimuthSignals;
import ru.nucodelabs.geo.anisotropy.ModelLayer;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.geo.anisotropy.Signal;

import javax.inject.Inject;

@Mapper(componentModel = "jsr330")
public abstract class DtoMapper {

    @Inject
    private ObjectMapper objectMapper;

    /**
     * Mapping using jackson ObjectMapper to use data class default parameters
     *
     * @param dto      source
     * @param outClass target type
     * @param <O>      target type
     * @param <I>      source type
     * @return mapped
     */
    @SneakyThrows
    protected <O, I> O map(I dto, Class<O> outClass) {
        return objectMapper.readValue(objectMapper.writeValueAsString(dto), outClass);
    }

    public ModelLayer fromDto(ModelLayerDto dto) {
        return map(dto, ModelLayer.class);
    }

    public Signal fromDto(SignalDto dto) {
        return map(dto, Signal.class);
    }

    public Point fromDto(PointDto dto) {
        return map(dto, Point.class);
    }

    public AzimuthSignals fromDto(AzimuthSignalsDto dto) {
        return map(dto, AzimuthSignals.class);
    }

    @Mapping(target = "isFixedResistance", source = "fixedResistance")
    @Mapping(target = "isFixedPower", source = "fixedPower")
    public abstract ModelLayerDto toDto(ModelLayer modelLayer);


    @Mapping(target = "isHidden", source = "hidden")
    public abstract SignalDto toDto(Signal signal);

    public abstract AzimuthSignalsDto toDto(AzimuthSignals azimuthSignals);

    public abstract PointDto toDto(Point point);
}
