package de.enbiz.basyskgt.controller.storage;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "files")
@Getter
@Setter
public class DbFile {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;

    private String type;

    @Lob
    private byte[] data;

    public DbFile() {
    }

    public DbFile(String name, String type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
}
