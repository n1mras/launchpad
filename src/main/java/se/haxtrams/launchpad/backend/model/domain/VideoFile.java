package se.haxtrams.launchpad.backend.model.domain;

public record VideoFile(
    Long id,
    String name,
    Long fileId,
    String path,
    String directory
) implements MediaFile { }
