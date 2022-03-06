package se.haxtrams.launchpad.backend.model.domain;

public record VideoFile(
    Long id,
    String name,
    String fileName,
    Long fileId,
    String filePath,
    String fileDirectory
) implements MediaFile { }
