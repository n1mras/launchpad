package se.haxtrams.launchpad.backend.service;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.haxtrams.launchpad.backend.converter.DomainConverter;
import se.haxtrams.launchpad.backend.exceptions.NotFoundException;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.repository.VideoRepository;

@Service
public class VideoLibraryService {
    private final VideoRepository videoRepository;
    private final DomainConverter domainConverter;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Random random = new Random();

    public VideoLibraryService(VideoRepository videoRepository, DomainConverter domainConverter) {
        this.videoRepository = videoRepository;
        this.domainConverter = domainConverter;
    }

    public VideoFile findVideoById(final Long id) {
        return videoRepository
                .findById(id)
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

    @Transactional(readOnly = true)
    public Page<VideoFile> findVideosWithName(String name, Pageable pageable) {
        return videoRepository.findAllByNameContainingIgnoreCase(name, pageable).map(domainConverter::toVideoFile);
    }
}
