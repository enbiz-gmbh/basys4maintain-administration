package de.enbiz.basyskgt.storage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AasxFileRepository extends CrudRepository<AasxFile, String> {
    @Query("select a from AasxFile a")
    List<AasxFileMetaData> findAllMetaData();
}
