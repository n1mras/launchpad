package se.haxtrams.launchpad.backend.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import se.haxtrams.launchpad.backend.model.api.response.PageResponse;
import se.haxtrams.launchpad.backend.model.api.response.VideoFileResponse;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;

@Component
public class ApiConverter {
    public VideoFileResponse toVideoFileResponse(final VideoFile videoFile) {
        return new VideoFileResponse(
            videoFile.id(),
            videoFile.name()
        );
    }

    public <T> PageResponse<T> toPageResponse(Page<T> input) {
        return new PageResponse<>(input.getContent(),
            input.getNumber(),
            input.getTotalPages(),
            input.isEmpty());
    }
}
