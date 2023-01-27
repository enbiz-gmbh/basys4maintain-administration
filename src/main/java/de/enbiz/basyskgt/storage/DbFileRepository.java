package de.enbiz.basyskgt.storage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DbFileRepository extends CrudRepository<DbFile, String> {

    @Query("select d from DbFile d")
    List<DbFileMetadataDto> findAllMetaData();
}
