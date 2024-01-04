package se.haxtrams.launchpad.backend.integration.video.player.mpv;

import java.io.IOException;
import java.util.Set;
import se.haxtrams.launchpad.backend.integration.video.player.ExtendedFeatures;
import se.haxtrams.launchpad.backend.integration.video.player.VideoPlayer;
import se.haxtrams.launchpad.backend.integration.video.player.mpv.model.MpvCommand;
import se.haxtrams.launchpad.backend.integration.video.player.mpv.model.MpvProperty;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.model.domain.settings.VideoSettings;

public class MpvPlayer extends VideoPlayer {
    private final MpvClient mpvClient;

    public MpvPlayer(VideoSettings videoSettings, MpvClient mpvClient) {
        super(videoSettings);
        this.mpvClient = mpvClient;
    }

    @Override
    public String getName() {
        return "MPV";
    }

    @Override
    public Set<ExtendedFeatures> getExtendedFeatures() {
        return Set.of(
                ExtendedFeatures.NEXT_SUBTITLE,
                ExtendedFeatures.TOGGLE_SUBTITLES,
                ExtendedFeatures.PAUSE_RESUME,
                ExtendedFeatures.NEXT_AUDIO_TRACK,
                ExtendedFeatures.SKIP_FORWARD,
                ExtendedFeatures.SKIP_BACKWARD);
    }

    @Override
    public VideoFile openVideo(VideoFile videoFile) {
        lockProcessAndExecute(process -> {
            try {
                process.filter(Process::isAlive).ifPresent(VideoPlayer::killVideoProcess);
                this.videoProcess = new ProcessBuilder(buildLaunchCommand(videoFile))
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start();
                mpvClient.connect();

                this.videoProcess.onExit().thenRun(mpvClient::disconnect);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return videoFile;
    }

    @Override
    public void pauseResume() {
        mpvClient.send(MpvCommand.CYCLE, MpvProperty.PAUSE);
    }

    @Override
    public void skipForward() {
        mpvClient.send(MpvCommand.SEEK, 15);
    }

    @Override
    public void skipBackward() {
        mpvClient.send(MpvCommand.SEEK, -15);
    }

    @Override
    public void nextSubtitle() {
        mpvClient.send(MpvCommand.CYCLE, MpvProperty.SUB);
    }

    @Override
    public void toggleSubtitles() {
        mpvClient.send(MpvCommand.CYCLE, MpvProperty.SUB_VISIBILITY);
    }

    @Override
    public void nextAudioTrack() {
        mpvClient.send(MpvCommand.CYCLE, MpvProperty.AUDIO);
    }
}
