package ru.nucodelabs.gem.file.dto.mapper;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import ru.nucodelabs.gem.config.MappersModule;
import ru.nucodelabs.gem.file.dto.anisotropy.AzimuthSignalsDto;
import ru.nucodelabs.gem.file.dto.anisotropy.PointDto;
import ru.nucodelabs.gem.file.dto.anisotropy.SignalDto;
import ru.nucodelabs.geo.anisotropy.AzimuthSignals;
import ru.nucodelabs.geo.anisotropy.Point;
import ru.nucodelabs.geo.anisotropy.Signal;
import ru.nucodelabs.geo.ves.DefaultValuesKt;
import ru.nucodelabs.geo.ves.calc.VesKt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoMapperTest {

    Injector injector = Guice.createInjector(new MappersModule());

    public DtoMapper dtoMapper = injector.getInstance(DtoMapper.class);

    @Test
    void fromDto() {
        var ab2 = 1.0;
        var mn2 = 2.0;
        var amperage = 1.0;
        var voltage = 1.0;

        SignalDto signalDto = new SignalDto(
                ab2,
                mn2,
                amperage,
                voltage,
                null,
                null,
                null
        );

        var resistanceApparent = VesKt.rhoA(ab2, mn2, amperage, voltage);

        assertEquals(
                new Signal(
                        1.0,
                        2.0,
                        1.0,
                        1.0,
                        resistanceApparent,
                        DefaultValuesKt.DEFAULT_ERROR,
                        false
                ),
                dtoMapper.fromDto(signalDto)
        );
    }

    @Test
    void testFromDto() {
        var ab2 = 1.0;
        var mn2 = 2.0;
        var amperage = 1.0;
        var voltage = 1.0;

        PointDto pointDto = new PointDto(
                List.of(
                        new AzimuthSignalsDto(
                                0.0,
                                List.of(
                                        new SignalDto(
                                                ab2,
                                                mn2,
                                                amperage,
                                                voltage,
                                                null,
                                                null,
                                                null
                                        )
                                )
                        )
                ),
                List.of()
        );

        var resistanceApparent = VesKt.rhoA(ab2, mn2, amperage, voltage);

        Point expected = new Point(
                List.of(
                        new AzimuthSignals(
                                0.0,
                                List.of(
                                        new Signal(
                                                ab2,
                                                mn2,
                                                amperage,
                                                voltage,
                                                resistanceApparent,
                                                DefaultValuesKt.DEFAULT_ERROR,
                                                false
                                        )
                                )
                        )
                ),
                List.of()
        );
    }
}