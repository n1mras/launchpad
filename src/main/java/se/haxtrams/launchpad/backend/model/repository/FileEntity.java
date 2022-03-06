package se.haxtrams.launchpad.backend.model.repository;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table
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
    @CreationTimestamp
    private Instant created;
    @UpdateTimestamp
    private Instant modified;

    protected FileEntity() {

    }

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


}
