package se.haxtrams.launchpad.backend.model.repository;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Lob
    @Column
    private String path;
    @Column(unique = true)
    private int pathHash;
    @NotNull
    @Lob
    private String directory;
    @CreationTimestamp
    private Instant created;
    @UpdateTimestamp
    private Instant modified;

    protected FileEntity() {

    }

    public FileEntity(String path, String directory) {
        this.path = path;
        this.pathHash = path.hashCode();
        this.directory = directory;
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        this.pathHash = path.hashCode();
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Instant getCreated() {
        return created;
    }

    public Instant getModified() {
        return modified;
    }

    public int getPathHash() {
        return pathHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return pathHash == that.pathHash && Objects.equals(id, that.id) && Objects.equals(path, that.path) && Objects.equals(directory, that.directory) && Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, pathHash, directory, created, modified);
    }

    @Override
    public String toString() {
        return "FileEntity{" +
            "id=" + id +
            ", path='" + path + '\'' +
            ", pathHash=" + pathHash +
            ", directory='" + directory + '\'' +
            ", created=" + created +
            ", modified=" + modified +
            '}';
    }
}
