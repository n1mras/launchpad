package se.haxtrams.launchpad.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.haxtrams.launchpad.backend.integration.video.player.GenericVideoPlayer;
import se.haxtrams.launchpad.backend.integration.video.player.VideoPlayer;
import se.haxtrams.launchpad.backend.integration.video.player.mplayer.MPlayer;
import se.haxtrams.launchpad.backend.integration.video.player.mpv.MpvClient;
import se.haxtrams.launchpad.backend.integration.video.player.mpv.MpvPlayer;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.model.domain.settings.VideoPlayerType;
import se.haxtrams.launchpad.backend.service.DataLoader;

@Configuration
public class SettingsConfig {
    private final DataLoader dataLoader;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public SettingsConfig(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Bean
    public Settings getSettings(@Value("${launchpad.settings.path}") final String settingsPath) {
        return dataLoader.loadSettings(settingsPath);
    }

    @Bean
    public VideoPlayer getVideoPlayer(final Settings settings) {
        var playerType = settings.getVideoSettings().getPlayerType();
        if (VideoPlayerType.MPLAYER_SLAVE_MODE.equals(playerType)) {
            return new MPlayer(settings.getVideoSettings());
        }
        if (VideoPlayerType.MPV.equals(playerType)) {
            return new MpvPlayer(settings.getVideoSettings(), new MpvClient());
        }
        return new GenericVideoPlayer(settings.getVideoSettings());
    }
}
