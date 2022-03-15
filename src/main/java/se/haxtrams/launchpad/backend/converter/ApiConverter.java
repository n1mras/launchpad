package se.haxtrams.launchpad.backend.converter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import se.haxtrams.launchpad.backend.model.api.response.PageResponse;
import se.haxtrams.launchpad.backend.model.api.response.VideoFileResponse;
import se.haxtrams.launchpad.backend.model.domain.VideoFile;

@Component
public class ApiConverter {

    public PageResponse<VideoFileResponse> toPaginatedVideoFileResponse(final Page<VideoFile> page) {
        return toPageResponse(page.map(this::toVideoFileResponse));
    }

    public VideoFileResponse toVideoFileResponse(final VideoFile videoFile) {
        return new VideoFileResponse(
            videoFile.id(),
            videoFile.name(),
            videoFile.fileName()
        );
    }

    public <T> PageResponse<T> toPageResponse(Page<T> input) {
        return new PageResponse<>(input.getContent(),
            input.getNumber(),
            input.getTotalPages(),
            input.isEmpty());
    }
}
