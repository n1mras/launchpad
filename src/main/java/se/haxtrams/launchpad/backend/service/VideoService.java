package se.haxtrams.launchpad.backend.service;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.haxtrams.launchpad.backend.converter.DomainConverter;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.model.domain.settings.Settings;
import se.haxtrams.launchpad.backend.model.repository.FileEntity;
import se.haxtrams.launchpad.backend.model.repository.VideoEntity;
import se.haxtrams.launchpad.backend.repository.FileRepository;
import se.haxtrams.launchpad.backend.repository.VideoRepository;

import javax.annotation.PostConstruct;
import java.io.File;

import static org.apache.commons.io.FilenameUtils.removeExtension;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final FileRepository fileRepository;
    private final DomainConverter domainConverter;
    private final DataLoader dataLoader;
    private final Settings settings;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public VideoService(VideoRepository videoRepository, FileRepository fileRepository, DomainConverter domainConverter, DataLoader dataLoader, Settings settings) {
        this.videoRepository = videoRepository;
        this.fileRepository = fileRepository;
        this.domainConverter = domainConverter;
        this.dataLoader = dataLoader;
        this.settings = settings;
    }

    @PostConstruct
    private void init() {
        log.info("Loading video files");
        for (String folder : settings.getVideoSettings().getFolders()) {
            log.info(String.format("Searching %s for video files", folder));

            dataLoader.findAllFilesIn(folder, true).stream()
                .filter(this::isVideoFileType)
                .forEach(this::upsertVideo);
        }

        log.info(String.format("Done, %s video files in db", videoRepository.count()));

    }

    public VideoFile getVideoById(final Long id) {
        return videoRepository.findById(id)
            .map(domainConverter::toVideoFile)
            .orElseThrow(() -> new RuntimeException("File not found"));
    }

    private VideoEntity upsertVideo(final File file) {
        return videoRepository.findByFilePathHash(file.getAbsolutePath().hashCode())
            .orElseGet(() -> videoRepository.save(
                    new VideoEntity(
                        removeExtension(file.getName()),
                        upsertFile(file))
                )
            );
    }

    private FileEntity upsertFile(final File file) {
        return fileRepository.findByPathHash(file.getAbsolutePath().hashCode())
            .orElseGet(() -> fileRepository.save(new FileEntity(file.getAbsolutePath(), file.getParent())));
    }

    private boolean isVideoFileType(final File file) {
        var extension = FilenameUtils.getExtension(file.getName());
        return settings.getVideoSettings().getFileTypes().contains(extension);
    }
}
