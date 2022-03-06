package se.haxtrams.launchpad.backend.converter;

import org.springframework.stereotype.Component;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;
import se.haxtrams.launchpad.backend.model.repository.VideoEntity;

@Component
public class DomainConverter {

    public VideoFile toVideoFile(final VideoEntity entity) {
        return new VideoFile(
            entity.getId(),
            entity.getName(),
            entity.getName(),
            entity.getFile().getId(),
            entity.getFile().getPath(),
            entity.getFile().getDirectory()
        );
    }
}
