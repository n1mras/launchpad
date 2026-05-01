package se.haxtrams.launchpad.backend.repository;

import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.haxtrams.launchpad.backend.model.repository.VideoEntity;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    Optional<VideoEntity> findByFilePath(String path);

    @Modifying
    @Query("delete from VideoEntity v where v.file.id in :fileIds")
    void deleteAllByFileIdIn(@Param("fileIds") Collection<Long> fileIds);

    @Query("select video from VideoEntity video inner join fetch video.file file "
            + "where upper(video.name) like upper(concat('%', :name, '%'))")
    Page<VideoEntity> findAllByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    int countAllByNameContainingIgnoreCase(String name);
}
