package de.enbiz.basyskgt.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRepository extends CrudRepository<HealthEntity, Long> {

    @Query("select h from HealthEntity h where h.timeCreated between ?1 and ?2")
    List<HealthEntity> findByTimeCreatedIsBetween(LocalDateTime timeCreatedStart, LocalDateTime timeCreatedEnd);

    HealthEntity findFirstByOrderByIdDesc();

    @Override
    Optional<HealthEntity> findById(Long aLong);

    @Query("select h from HealthEntity h order by h.id DESC")
    List<HealthEntity> findByOrderByIdDesc(Pageable pageable);
}
