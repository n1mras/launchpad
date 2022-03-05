package se.haxtrams.launchpad.backend.model.domain.settings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public class VideoSettings {
    private final String path;
    private final Set<String> args;
    private final Set<String> fileTypes;
    private final List<String> folders;

    @JsonCreator
    public VideoSettings(@JsonProperty("path") String path,
                  @JsonProperty("args") Set<String> args,
                  @JsonProperty("fileTypes") Set<String> fileTypes,
                  @JsonProperty("folders") List<String> folders) {
        this.path = path;
        this.args = args;
        this.fileTypes = fileTypes;
        this.folders = folders;
    }

    public String getPath() {
        return path;
    }

    public Set<String> getArgs() {
        return args;
    }

    public Set<String> getFileTypes() {
        return fileTypes;
    }

    public List<String> getFolders() {
        return folders;
    }
}
