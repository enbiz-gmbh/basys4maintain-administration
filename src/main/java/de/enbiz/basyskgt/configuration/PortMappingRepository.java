package de.enbiz.basyskgt.configuration;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortMappingRepository extends CrudRepository<PortMapping, Integer> {

    PortMapping findByPortNumber(Integer portNumber);

    long deleteByPortNumber(Integer portNumber);

    PortMapping findByIdentifierTypeAndIdentifierString(IdentifierType identifierType, String identifierString);

}

