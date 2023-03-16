package ru.nucodelabs.gem.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.mapstruct.factory.Mappers;
import ru.nucodelabs.gem.fxmodel.anisotropy.mapper.AnisotropyFxModelMapper;
import ru.nucodelabs.gem.fxmodel.ves.mapper.VesFxModelMapper;

public class MappersModule extends AbstractModule {
    @Provides
    @Singleton
    VesFxModelMapper fxModelMapper() {
        return Mappers.getMapper(VesFxModelMapper.class);
    }

    @Provides
    @Singleton
    AnisotropyFxModelMapper anisotropyFxModelMapper() {
        return Mappers.getMapper(AnisotropyFxModelMapper.class);
    }
}
