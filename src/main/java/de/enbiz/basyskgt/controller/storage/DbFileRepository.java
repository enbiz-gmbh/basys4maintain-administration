package de.enbiz.basyskgt.controller.storage;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface DbFileRepository extends CrudRepository<DbFile, String> {

    @Query("select d from DbFile d")
    Stream<DbFile> findAllAsStream();
}
