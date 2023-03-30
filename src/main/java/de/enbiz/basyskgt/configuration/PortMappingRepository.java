package de.enbiz.basyskgt.configuration;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface PortMappingRepository extends CrudRepository<PortMapping, Integer> {

    PortMapping findByPortNumber(Integer portNumber);

    long deleteByPortNumber(Integer portNumber);

    PortMapping findByAasxFile_Id(String id);


}

