package se.haxtrams.launchpad.backend.model.api.response;

import java.util.List;

public record PageResponse<T>(List<T> content, int page, int total, boolean isEmpty) {
}
