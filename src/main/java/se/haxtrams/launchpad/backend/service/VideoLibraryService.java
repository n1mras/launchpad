package se.haxtrams.launchpad.backend.service;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import se.haxtrams.launchpad.backend.converter.DomainConverter;
import se.haxtrams.launchpad.backend.exceptions.NotFoundException;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.model.repository.FileEntity;
import se.haxtrams.launchpad.backend.model.repository.VideoEntity;
import se.haxtrams.launchpad.backend.repository.FileRepository;
import se.haxtrams.launchpad.backend.repository.VideoRepository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.io.FilenameUtils.removeExtension;
import static se.haxtrams.launchpad.backend.helper.Utils.cleanupFileName;

@Service
public class VideoLibraryService {
    private final VideoRepository videoRepository;
    private final FileRepository fileRepository;
    private final DomainConverter domainConverter;
    private final DataLoader dataLoader;
    private final Settings settings;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Random random = new Random();

    private AtomicBoolean syncInProgress = new AtomicBoolean(false);

    public VideoLibraryService(VideoRepository videoRepository, FileRepository fileRepository, DomainConverter domainConverter, DataLoader dataLoader, Settings settings) {
        this.videoRepository = videoRepository;
        this.fileRepository = fileRepository;
        this.domainConverter = domainConverter;
        this.dataLoader = dataLoader;
        this.settings = settings;
    }

    @PostConstruct
    public void loadFiles() {
        if (!syncInProgress.compareAndSet(false, true)) {
            log.warn("A file sync is already in progress, skipping");
            return;
        }

        try {
            final var startTs = Instant.now();
            log.info("Loading video files");
            for (String folder : settings.getVideoSettings().getFolders()) {
                log.info(String.format("Searching %s", folder));

                dataLoader.processAllFilesIn(folder, true, file ->
                    Optional.of(file)
                        .filter(this::isVideoFileType)
                        .ifPresent(this::persistVideo)
                );
            }
            log.info(String.format("Done, %s video files in db", videoRepository.count()));
            log.info(String.format("Finished in %ss", Instant.now().getEpochSecond() - startTs.getEpochSecond()));
        } finally {
            syncInProgress.set(false);
        }
    }

    public VideoFile findVideoById(final Long id) {
        return videoRepository.findById(id)
            .map(domainConverter::toVideoFile)
            .orElseThrow(() -> new NotFoundException(String.format("No video with id: %s", id)));
    }

    public VideoFile findRandomVideo(final String filter) {
        final var pageCount = videoRepository.countAllByNameContainingIgnoreCase(filter);
        if (pageCount <= 0) {
            throw new NotFoundException("Could not find any video matching filter");
        }

        final var pageRequest = PageRequest.of(random.nextInt(pageCount), 1);

        return videoRepository.findAllByNameContainingIgnoreCase(filter, pageRequest).stream()
            .findFirst()
            .map(domainConverter::toVideoFile)
            .orElseThrow();
    }

    public Page<VideoFile> findVideosWithName(String name, Pageable pageable) {
        return videoRepository.findAllByNameContainingIgnoreCase(name, pageable)
            .map(domainConverter::toVideoFile);
    }

    private VideoEntity persistVideo(final File file) {
        return videoRepository.findByFilePath(file.getAbsolutePath())
            .orElseGet(() -> videoRepository.save(
                    new VideoEntity(
                        cleanupFileName(removeExtension(file.getName())),
                        persistFile(file))
                )
            );
    }

    private FileEntity persistFile(final File file) {
        return fileRepository.findByPath(file.getAbsolutePath())
            .orElseGet(() -> fileRepository.save(new FileEntity(
                file.getName(),
                file.getAbsolutePath(),
                file.getParent())));
    }

    private boolean isVideoFileType(final File file) {
        var extension = FilenameUtils.getExtension(file.getName());
        return settings.getVideoSettings().getFileTypes().contains(extension);
    }
}
