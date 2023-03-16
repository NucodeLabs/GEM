package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.mapstruct.factory.Mappers;
import ru.nucodelabs.gem.fxmodel.ves.mapper.VesFxModelMapper;

public class MappersModule extends AbstractModule {
    @Provides
    VesFxModelMapper fxModelMapper() {
        return Mappers.getMapper(VesFxModelMapper.class);
    }
}
