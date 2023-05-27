package se.haxtrams.launchpad.backend.model.domain.settings;

import static java.util.Objects.requireNonNull;
import static se.haxtrams.launchpad.backend.helper.Utils.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ClassCanBeRecord")
public class VideoSettings {
    private final String path;
    private final Set<String> args;
    private final Set<String> fileTypes;
    private final List<String> folders;
    private final VideoPlayerType playerType;

    @JsonCreator
    public VideoSettings(
            @JsonProperty("path") String path,
            @JsonProperty("args") Set<String> args,
            @JsonProperty("fileTypes") Set<String> fileTypes,
            @JsonProperty("folders") List<String> folders,
            @JsonProperty("playerType") VideoPlayerType playerType) {
        this.path = requireNonNull(path);
        this.args = deNullify(args, new HashSet<>());
        this.fileTypes = requireNonNull(fileTypes);
        this.folders = requireNonNull(folders);
        this.playerType = deNullify(playerType, VideoPlayerType.GENERIC);
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

    public VideoPlayerType getPlayerType() {
        return playerType;
    }
}
