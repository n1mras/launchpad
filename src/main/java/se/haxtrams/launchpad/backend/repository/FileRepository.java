package se.haxtrams.launchpad.backend.repository;

import static org.hibernate.annotations.QueryHints.*;

import jakarta.persistence.QueryHint;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import se.haxtrams.launchpad.backend.model.repository.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByPath(String path);

    @QueryHints({@QueryHint(name = FETCH_SIZE, value = "5000")})
    Stream<FileEntity> streamAllBy();
}
