package se.haxtrams.launchpad.backend.model.repository;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

@Entity
public class VideoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @ManyToOne
    @JoinColumn(name = "file_id", unique = true)
    private FileEntity file;
    @CreationTimestamp
    private Instant created;
    @UpdateTimestamp
    private Instant modified;

    protected VideoEntity() {
    }

    public VideoEntity(String name, FileEntity file) {
        this.name = name;
        this.file = file;
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

    public Instant getCreated() {
        return created;
    }

    public Instant getModified() {
        return modified;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoEntity that = (VideoEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(file, that.file) && Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, file, created, modified);
    }

    @Override
    public String toString() {
        return "VideoEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", file=" + file +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}
