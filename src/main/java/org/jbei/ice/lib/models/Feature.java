package org.jbei.ice.lib.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.jbei.ice.lib.value_objects.IFeatureValueObject;

@Entity
@Table(name = "features")
@SequenceGenerator(name = "sequence", sequenceName = "features_id_seq", allocationSize = 1)
public class Feature implements IFeatureValueObject, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    private int id;

    @Column(name = "name", length = 127)
    private String name;

    @Column(name = "description", length = 1023)
    private String description;

    @Column(name = "identification", length = 127)
    private String identification;

    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "auto_find")
    private int autoFind;

    @Column(name = "genbank_type", length = 127)
    private String genbankType;

    public Feature() {
        super();
    }

    public Feature(String name, String description, String identification, String uuid,
            int autoFind, String genbankType) {
        super();

        this.name = name;
        this.description = description;
        this.identification = identification;
        this.uuid = uuid;
        this.autoFind = autoFind;
        this.genbankType = genbankType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getAutoFind() {
        return autoFind;
    }

    public void setAutoFind(int autoFind) {
        this.autoFind = autoFind;
    }

    public String getGenbankType() {
        return genbankType;
    }

    public void setGenbankType(String genbankType) {
        this.genbankType = genbankType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
