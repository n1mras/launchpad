package se.haxtrams.launchpad.backend.integration.video.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.haxtrams.launchpad.backend.exceptions.NotSupportedException;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.model.domain.settings.VideoSettings;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public abstract class VideoPlayer {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final VideoSettings videoSettings;
    protected final Lock processLock = new ReentrantLock();
    protected Process videoProcess;

    public VideoPlayer(VideoSettings videoSettings) {
        this.videoSettings = videoSettings;
    }

    public abstract String getName();

    public abstract Set<ExtendedFeatures> getExtendedFeatures();

    public abstract VideoFile openVideo(VideoFile videoFile);

    public abstract void closePlayer();

    public void pauseResume() {
        throw new NotSupportedException();
    }

    public void skipForward() {
        throw new NotSupportedException();
    }

    public void skipBackward() {
        throw new NotSupportedException();
    }

    public void nextSubtitle() {
        throw new NotSupportedException();
    }

    public void toggleSubtitles() {
        throw new NotSupportedException();
    }

    public void nextAudioTrack() {
        throw new NotSupportedException();
    }


    /**
     * Apply lock before calling
     * @param process
     * @return
     */
    protected static void killVideoProcess(Process process) {
        try {
            process.destroy();
            process.waitFor(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            process.destroyForcibly();
        }
    }

    protected List<String> buildLaunchCommand(VideoFile videoFile) {
        final List<String> cmd = new ArrayList<>();
        cmd.add(videoSettings.getPath());
        cmd.addAll(videoSettings.getArgs());
        cmd.add(videoFile.filePath());

        return cmd;
    }

    protected void lockProcessAndExecuteIfAlive(Consumer<Process> consumer) {
        lockProcessAndExecute(process -> process
            .filter(Process::isAlive)
            .ifPresent(consumer)
        );
    }

    protected void lockProcessAndExecute(Consumer<Optional<Process>> consumer) {
        try {
            processLock.lock();
            consumer.accept(Optional.ofNullable(videoProcess));
        } finally {
            processLock.unlock();
        }
    }

}
