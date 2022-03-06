package se.haxtrams.launchpad.backend.model.domain;

public interface MediaFile {
    Long fileId();
    String fileName();
    String filePath();
    String fileDirectory();
}
