package se.haxtrams.launchpad.backend.service;

import static org.apache.commons.io.FilenameUtils.removeExtension;
import static se.haxtrams.launchpad.backend.util.Utils.cleanupFileName;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
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
        var knownPaths = new HashSet<>(fileRepository.findAllPaths());

        for (String folder : settings.getVideoSettings().getFolders()) {
            log.info(String.format("Searching %s", folder));

            var newFiles = new ArrayList<File>();
            dataLoader.processFilesIn(folder, true, file -> {
                if (isVideoFileType(file) && !knownPaths.contains(file.getAbsolutePath())) {
                    newFiles.add(file);
                    knownPaths.add(file.getAbsolutePath());
                }
            });

            if (!newFiles.isEmpty()) {
                var fileEntities = fileRepository.saveAll(newFiles.stream()
                        .map(f -> new FileEntity(f.getName(), f.getAbsolutePath(), f.getParent()))
                        .toList());

                videoRepository.saveAll(fileEntities.stream()
                        .map(fe -> new VideoEntity(cleanupFileName(removeExtension(fe.getName())), fe))
                        .toList());
            }
        }
    }

    private void removeInvalidEntries() {
        var toDelete = fileRepository
                .streamAllBy()
                .filter(entity -> !isVideoFileType(entity.getPath()) || Files.notExists(Paths.get(entity.getPath())))
                .peek(entity -> log.info("{} was removed", entity.getPath()))
                .map(FileEntity::getId)
                .toList();

        fileRepository.deleteAllByIdInBatch(toDelete);
    }

    private boolean isVideoFileType(final File file) {
        return isVideoFileType(file.getName());
    }

    private boolean isVideoFileType(final String fileName) {
        var extension = FilenameUtils.getExtension(fileName);
        return settings.getVideoSettings().getFileTypes().contains(extension);
    }
}
