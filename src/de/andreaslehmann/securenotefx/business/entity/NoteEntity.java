/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author Andreas UUID uniqueKey = UUID.randomUUID();
 */
public class NoteEntity implements Serializable {

    private UUID uniqueKey;
    private final SimpleStringProperty title;
    private final SimpleStringProperty body;
    private long createdOn = 0L;
    private long lastSavedOn = 0L;
    private long deletedOn = 0L;
    private transient SimpleBooleanProperty dirty;
    
    private final transient DirtyListener dirtyListener = new DirtyListener();

    public NoteEntity() {
        this.title = new SimpleStringProperty();
        this.body = new SimpleStringProperty();
        this.uniqueKey = UUID.randomUUID();
        this.createdOn = System.currentTimeMillis();
        this.body.addListener(dirtyListener);
        this.title.addListener(dirtyListener);
        this.dirty = new SimpleBooleanProperty(false);
    }

    public NoteEntity(String title, String body) {
        super();
        this.title = new SimpleStringProperty(title);
        this.body = new SimpleStringProperty(body);
        this.uniqueKey = UUID.randomUUID();
        this.createdOn = System.currentTimeMillis();
        this.body.addListener(dirtyListener);
        this.title.addListener(dirtyListener);
        this.dirty = new SimpleBooleanProperty(false);
    }

    public String getTitle() {
        return title.getValueSafe();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public ReadOnlyBooleanProperty isDirtyProperty() {
        return this.dirty;
    }

    public void setTitle(String title) {
        // nur setzen, wenn der übergebene Titel != null ist
        // und wenn er sich vom aktuellen Weret unterscheidet.
        if (title != null
                && title.hashCode() != this.title.getValueSafe().hashCode()) {
            this.title.set(title);
            this.dirty.setValue(true);
        }
    }

    public String getBody() {
        return body.getValueSafe();
    }

    public void setBody(String body) {
        if (body != null
                && body.hashCode() != this.body.getValueSafe().hashCode()) {
            this.body.set(body);
            this.dirty.setValue(true);
        }
    }

    public SimpleStringProperty bodyProperty() {
        return body;
    }

    public UUID getUniqueKey() {
        return uniqueKey;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setLastSavedOn(long lastSavedOn) {
        this.lastSavedOn = lastSavedOn;
        this.dirty.setValue(false);
    }

    public long getLastSavedOn() {
        return lastSavedOn;
    }

    public long getDeletedOn() {
        return deletedOn;
    }

    public boolean isDirty() {
        return dirty.getValue();
    }

    public boolean isDeleted() {
        return deletedOn > 0L;
    }

    public void setDirty() {
        this.dirty.setValue(true);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.uniqueKey);
        hash = 23 * hash + (int) (this.createdOn ^ (this.createdOn >>> 32));
        hash = 23 * hash + (int) (this.lastSavedOn ^ (this.lastSavedOn >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NoteEntity other = (NoteEntity) obj;
        if (!Objects.equals(this.uniqueKey, other.uniqueKey)) {
            return false;
        }
        if (this.createdOn != other.createdOn) {
            return false;
        }
        if (this.lastSavedOn != other.lastSavedOn) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (isDeleted()) {
            return "NoteEntity{ id=" + uniqueKey + ", deleted=" + deletedOn + '}';
        } else {
            return "NoteEntity{ id=" + uniqueKey + ", title=" + title + ", lastSaved=" + lastSavedOn + '}';
        }
    }

    /**
     * Diese Methode erzeugt eine komplette Deep-Copy des Elements inklusive der
     * ID, DateCreated usw.
     *
     * Der clone hat den gleichen HasCode wie das Original.
     *
     * @return
     */
    public NoteEntity cloneElement() {
        NoteEntity clone = new NoteEntity(this.getTitle(), this.getBody());
        clone.uniqueKey = this.uniqueKey;
        clone.createdOn = this.createdOn;
        clone.lastSavedOn = this.lastSavedOn;
        clone.deletedOn = this.deletedOn;
        clone.dirty = new SimpleBooleanProperty(this.dirty.getValue());
        return clone;
    }

    public void delete() {
        this.deletedOn = System.currentTimeMillis();
        this.dirty.set(true);
    }

    /**
     * Dieser Listener setzt das Dirty-Flag, falls ein Property geändert wurde.
     */
    private class DirtyListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> ov, String t, String t1) {

            // Achtung: beim Bi-direktionalen binden der Eigenschaften
            // wird mit dieser Implementierung IMMER das dirty Flag gesetzt.
            // Auch wenn der neue und der alte Wert gleich sind.
            setDirty();
        }
    }
}
