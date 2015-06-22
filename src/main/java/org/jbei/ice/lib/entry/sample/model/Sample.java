package org.jbei.ice.lib.entry.sample.model;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.jbei.ice.lib.dao.IDataModel;
import org.jbei.ice.lib.dto.sample.PartSample;
import org.jbei.ice.lib.entry.model.Entry;
import org.jbei.ice.lib.entry.model.EntryBooleanPropertiesBridge;
import org.jbei.ice.lib.models.Storage;

import javax.persistence.*;
import java.util.Date;

/**
 * Store Sample information.
 * <p>
 * Each sample is a uniquely identified (via UUIDv4) object representing a physical sample. Storage
 * locations are handled by {@link org.jbei.ice.lib.models.Storage} objects.
 *
 * @author Timothy Ham, Zinovii Dmytriv
 */
@Entity
@Table(name = "samples")
@SequenceGenerator(name = "sequence", sequenceName = "samples_id_seq", allocationSize = 1)
public class Sample implements IDataModel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
    private long id;

    @Column(name = "uuid", length = 36)
    private String uuid;

    @Column(name = "depositor", length = 127)
    private String depositor;

    @Column(name = "label", length = 127)
    private String label;

    @Column(name = "notes")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String notes;

    @ContainedIn
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entries_id", nullable = false, unique = false)
    @Field(bridge = @FieldBridge(impl = EntryBooleanPropertiesBridge.class, params = {
            @org.hibernate.search.annotations.Parameter(name = "boolean", value = "hasSample")
    }))
    private Entry entry;

    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    @Column(name = "modification_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id")
    private Storage storage;

    public Sample() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDepositor() {
        return depositor;
    }

    public void setDepositor(String depositor) {
        this.depositor = depositor;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage locationNew) {
        storage = locationNew;
    }

    @Override
    public PartSample toDataTransferObject() {
        PartSample sample = new PartSample();
        sample.setLabel(label);
        if (entry != null)
            sample.setPartId(entry.getId());
        sample.setCreationTime(creationTime.getTime());
        return sample;
    }
}
