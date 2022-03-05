package se.haxtrams.launchpad.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.haxtrams.launchpad.backend.model.repository.VideoEntity;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository <VideoEntity, Long> {
    Optional<VideoEntity> findByFilePathHash(int hashcode);
    Page<VideoEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
