package se.haxtrams.launchpad.backend.integration.video.player.mplayer;

import se.haxtrams.launchpad.backend.integration.video.player.VideoPlayer;
import se.haxtrams.launchpad.backend.integration.video.player.ExtendedFeatures;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.model.domain.settings.VideoSettings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class MPlayer extends VideoPlayer {
    public MPlayer(VideoSettings videoSettings) {
        super(videoSettings);
    }

    @Override
    public String getName() {
        return "MPlayer";
    }

    @Override
    public Set<ExtendedFeatures> getExtendedFeatures() {
        return Set.of(
            ExtendedFeatures.NEXT_SUBTITLE,
            ExtendedFeatures.TOGGLE_SUBTITLES,
            ExtendedFeatures.PAUSE_RESUME,
            ExtendedFeatures.NEXT_AUDIO_TRACK,
            ExtendedFeatures.SKIP_FORWARD,
            ExtendedFeatures.SKIP_BACKWARD
        );
    }

    @Override
    public VideoFile openVideo(VideoFile videoFile) {
        lockProcessAndExecute(process -> {
            try {
                process
                    .filter(Process::isAlive)
                    .ifPresent(VideoPlayer::killVideoProcess);

                this.videoProcess = new ProcessBuilder(buildLaunchCommand(videoFile))
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return videoFile;
    }

    @Override
    public void closePlayer() {
        lockProcessAndExecuteIfAlive(VideoPlayer::killVideoProcess);
    }

    @Override
    public void pauseResume() {
        sendCommand(CLICommands.PAUSE);
    }

    @Override
    public void skipForward() {
        sendCommand(String.format("%s 10 0", CLICommands.SEEK.getValue()));
    }

    @Override
    public void skipBackward() {
        sendCommand(String.format("%s -10 0", CLICommands.SEEK.getValue()));
    }

    @Override
    public void nextSubtitle() {
        sendCommand(CLICommands.SUB_SELECT);
    }

    @Override
    public void nextAudioTrack() {
        sendCommand(CLICommands.SWITCH_AUDIO);
    }

    @Override
    public void toggleSubtitles() {
        sendCommand(CLICommands.SUB_VISIBILITY);
    }

    private void sendCommand(CLICommands cmd) {
        sendCommand(cmd.getValue());
    }

    private void sendCommand(String cmd) {
        lockProcessAndExecuteIfAlive(process -> {
            try {
                var writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                writer.write(cmd);
                writer.newLine();
                writer.flush();
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error in MPlayer::sendCommand", e);
            }
        });
    }
}
