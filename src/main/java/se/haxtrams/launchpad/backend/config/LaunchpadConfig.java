package se.haxtrams.launchpad.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.model.domain.settings.VideoSettings;
import se.haxtrams.launchpad.backend.service.DataLoader;

import java.util.List;
import java.util.Set;

@Configuration
public class LaunchpadConfig {
    private final DataLoader dataLoader;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public LaunchpadConfig(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Bean
    public Settings getSettings(@Value("${launchpad.settings.path}") final String settingsPath) {
        try {
            return dataLoader.loadSettings(settingsPath);
        } catch (Exception e) {
            log.error("Could not load settings", e);
            return new Settings(new VideoSettings(null, null, Set.of(), List.of()));
        }
    }
}
