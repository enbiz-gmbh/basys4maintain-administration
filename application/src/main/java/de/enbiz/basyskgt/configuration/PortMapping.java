package de.enbiz.basyskgt.configuration;

import de.enbiz.basyskgt.storage.AasxFile;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class PortMapping {
    @Id
    @Column(name = "port_number", nullable = false)
    private Integer portNumber;
    @OneToOne
    @JoinColumn(name = "aasx_file_id", referencedColumnName = "id")
    private AasxFile aasxFile;

    public PortMapping() {
    }

    public PortMapping(Integer portNumber) {
        this.portNumber = portNumber;
    }

}