package se.haxtrams.launchpad.backend.model.repository;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "file")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column
    private String name;

    @NotNull
    @Lob
    @Column
    private String path;

    @NotNull
    @Lob
    private String directory;

    @OneToOne(mappedBy = "file", orphanRemoval = true)
    private VideoEntity video;

    @CreationTimestamp
    private Instant created;

    @UpdateTimestamp
    private Instant modified;

    protected FileEntity() {}

    public FileEntity(String name, String path, String directory) {
        this.name = name;
        this.path = path;
        this.directory = directory;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    @Override
    public String toString() {
        return "FileEntity{" + "id="
                + id + ", name='"
                + name + '\'' + ", path='"
                + path + '\'' + ", directory='"
                + directory + '\'' + ", created="
                + created + ", modified="
                + modified + '}';
    }
}
