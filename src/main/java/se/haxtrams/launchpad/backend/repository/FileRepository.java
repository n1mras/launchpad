package se.haxtrams.launchpad.backend.repository;

import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import se.haxtrams.launchpad.backend.model.repository.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    @Query("select f.path from FileEntity f")
    List<String> findAllPaths();

    @QueryHints({@QueryHint(name = "org.hibernate.fetchSize", value = "5000")})
    Stream<FileEntity> streamAllBy();
}
