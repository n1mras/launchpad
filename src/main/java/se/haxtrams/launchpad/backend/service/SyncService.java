package se.haxtrams.launchpad.backend.service;

import static org.apache.commons.io.FilenameUtils.removeExtension;
import static se.haxtrams.launchpad.backend.util.Utils.cleanupFileName;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.model.repository.FileEntity;
import se.haxtrams.launchpad.backend.model.repository.VideoEntity;
import se.haxtrams.launchpad.backend.repository.FileRepository;
import se.haxtrams.launchpad.backend.repository.VideoRepository;

@Service
public class SyncService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private AtomicBoolean inProgress = new AtomicBoolean(false);

    private final Settings settings;
    private final DataLoader dataLoader;
    private final FileRepository fileRepository;
    private final VideoRepository videoRepository;

    public SyncService(
            Settings settings, DataLoader dataLoader, FileRepository fileRepository, VideoRepository videoRepository) {
        this.settings = settings;
        this.dataLoader = dataLoader;
        this.fileRepository = fileRepository;
        this.videoRepository = videoRepository;
    }

    @Transactional
    public void sync() {
        if (!inProgress.compareAndSet(false, true)) {
            log.warn("Sync already in progress");
            return;
        }

        try {
            final var startTs = Instant.now();
            log.info("Searching for new files");
            addNewFiles();
            log.info("Removing missing or invalid entries");
            removeInvalidEntries();

            log.info(String.format(
                    "Sync complete, duration %ss", Instant.now().getEpochSecond() - startTs.getEpochSecond()));

        } catch (Exception e) {
            log.error("Error during sync", e);
        } finally {
            inProgress.set(false);
        }
    }

    private void addNewFiles() {
        for (String folder : settings.getVideoSettings().getFolders()) {
            log.info(String.format("Searching %s", folder));

            dataLoader.processFilesIn(
                    folder,
                    true,
                    file -> Optional.of(file).filter(this::isVideoFileType).ifPresent(this::persistVideo));
        }
    }

    private void removeInvalidEntries() {
        fileRepository
                .streamAllBy()
                .filter(entity -> !isVideoFileType(entity.getPath()) || Files.notExists(Paths.get(entity.getPath())))
                .peek(entity -> log.info("{} was removed", entity.getPath()))
                .map(FileEntity::getId)
                .forEach(fileRepository::deleteById);
    }

    private VideoEntity persistVideo(final File file) {
        return videoRepository
                .findByFilePath(file.getAbsolutePath())
                .orElseGet(() -> videoRepository.save(
                        new VideoEntity(cleanupFileName(removeExtension(file.getName())), persistFile(file))));
    }

    private FileEntity persistFile(final File file) {
        return fileRepository
                .findByPath(file.getAbsolutePath())
                .orElseGet(() ->
                        fileRepository.save(new FileEntity(file.getName(), file.getAbsolutePath(), file.getParent())));
    }

    private boolean isVideoFileType(final File file) {
        return isVideoFileType(file.getName());
    }

    private boolean isVideoFileType(final String fileName) {
        var extension = FilenameUtils.getExtension(fileName);
        return settings.getVideoSettings().getFileTypes().contains(extension);
    }
}
