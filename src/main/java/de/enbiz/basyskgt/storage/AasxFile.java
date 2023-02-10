package de.enbiz.basyskgt.storage;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "aasx_files")
@Getter
@Setter
public class AasxFile {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private String type;

    @Lob
    private byte[] data;

    @Column(name = "time_created")
    private LocalDateTime timeCreated;

    private IdentifierType identifierType;

    private String identifier;

    public AasxFile() {
    }

    public AasxFile(String name, String type, byte[] data, IdentifierType identifierType, String identifier) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.timeCreated = LocalDateTime.now();
        this.identifierType = identifierType;
        this.identifier = identifier;
    }
}
