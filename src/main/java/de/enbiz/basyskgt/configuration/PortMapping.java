package de.enbiz.basyskgt.configuration;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
class PortMapping {
    @Id
    @Column(name = "port_number", nullable = false)
    private Integer portNumber;
    @Column(name = "identifier_type")
    private IdentifierType identifierType;
    @Column(name = "identifier_string")
    private String identifierString;

    protected PortMapping() {
    }

    public PortMapping(Integer portNumber, IIdentifier identifierString) {
        this(portNumber, identifierString.getIdType(), identifierString.getId());
    }

    public PortMapping(Integer portNumber, IdentifierType identifierType, String identifierString) {
        this.portNumber = portNumber;
        this.identifierType = identifierType;
        this.identifierString = identifierString;
    }

    public IIdentifier getIdentifier() {
        return new Identifier(identifierType, identifierString);
    }
}