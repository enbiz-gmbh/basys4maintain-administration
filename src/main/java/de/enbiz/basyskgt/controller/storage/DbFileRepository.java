package de.enbiz.basyskgt.controller.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbFileRepository extends CrudRepository<DbFile, String> {
}
