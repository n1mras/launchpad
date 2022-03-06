package se.haxtrams.launchpad.backend.model.domain;

public record VideoFile(
    Long id,
    String name,
    Long fileId,
    String fileName,
    String filePath,
    String fileDirectory
) implements MediaFile { }
