package se.haxtrams.launchpad.backend.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.haxtrams.launchpad.backend.model.repository.VideoEntity;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    Optional<VideoEntity> findByFilePath(String path);

    Page<VideoEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    int countAllByNameContainingIgnoreCase(String name);
}
