package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.mapstruct.factory.Mappers;
import ru.nucodelabs.gem.fxmodel.mapper.FxModelMapper;

public class MappersModule extends AbstractModule {
    @Provides
    FxModelMapper fxModelMapper() {
        return Mappers.getMapper(FxModelMapper.class);
    }
}
