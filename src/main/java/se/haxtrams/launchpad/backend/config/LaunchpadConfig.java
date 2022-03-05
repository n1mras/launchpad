package se.haxtrams.launchpad.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.service.DataLoader;

@Configuration
public class LaunchpadConfig {
    private final DataLoader dataLoader;

    public LaunchpadConfig(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Bean
    public Settings getSettings(@Value("${launchpad.settings.path}") final String settingsPath) {
        return dataLoader.loadSettings(settingsPath);
    }
}
