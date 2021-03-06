/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteslist;

import de.andreaslehmann.securenotefx.business.boundary.LocalFSNoteService;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andreas
 */
public class NotesListPresenter implements Initializable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    ContextMenu contextMenu4DeletedNotes = null;
    NoteTitleCellFactory noteTitleCellFactory = new NoteTitleCellFactory();

    private final ComparatorNotesByTitle comparatorNotes_byTitle = new ComparatorNotesByTitle();

    @Inject
    private LocalFSNoteService service;

    /**
     * Hält eine Referenz für die aktuell selektierte Notiz
     */
    private ObjectProperty<NoteEntity> selectedNote;

    /**
     * Steuert, ob gelöschte Notizen angezeigt werden sollen, oder nicht.
     */
    private BooleanProperty showDeleted;
    /**
     * List aller Notizen as Backing für die ListView
     */
    private ObservableList<NoteEntity> notes;

    @FXML
    private ListView<NoteEntity> notesListView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.notes = FXCollections.observableArrayList();
        this.selectedNote = new SimpleObjectProperty<>();
        this.showDeleted = new SimpleBooleanProperty(false);

        buildContextMenues();
        registerListeners();

        // ListView konfigurieren und Modell setzen
        this.notesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.notesListView.itemsProperty().set(this.notes);

        this.service.init();
        reload();

        // Bei Änderungen der "ShowDeleted"_Properties: Liste neu einlesen
        this.showDeletedNotesProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                saveAllModified();
                reload();
            }
        });

    }

    private void reload() {
        List<NoteEntity> all = service.list();
        notes.clear();

        if (this.showDeleted.getValue()) { // spezielles Kontextmenü für gelöschte
            this.notesListView.setCellFactory(ContextMenuListCell.<NoteEntity>forListView(this.contextMenu4DeletedNotes, this.noteTitleCellFactory));
        } else {
            this.notesListView.setCellFactory(this.noteTitleCellFactory);
        }

        for (NoteEntity n : all) {
            if (this.showDeleted.getValue()) { // nur gelöschte
                if (n.isDeleted()) {
                    this.notes.add(n);
                }
            } else {
                if (!n.isDeleted()) { // nur anzeigen, wenn nicht gelöscht
                    this.notes.add(n);
                }
            }
        }
        Collections.sort(this.notes, comparatorNotes_byTitle);
    }

    /**
     * Erzeugt und registriert alle Listener
     */
    private void registerListeners() {
        ChangeListener<NoteEntity> selectionListener = new ChangeListener<NoteEntity>() {
            @Override
            public void changed(ObservableValue<? extends NoteEntity> observable, NoteEntity oldValue, NoteEntity newValue) {
                if (selectedNote.get() != null) {
                    service.writeNoteEntity(selectedNote.get());
                }
                selectedNote.set(newValue);
                if (newValue != null) {
                    log.debug("selcted Note changed to:" + newValue.getTitle());
                } else {
                    log.debug("selcted Note changed to nothing!");
                }

            }
        };
        this.notesListView.getSelectionModel().selectedItemProperty().addListener(selectionListener);
    }

    private void buildContextMenues() {
        // Erzeuge KontextmenüMenuItem 
        MenuItem restore = new MenuItem("Wiederherstellen");
        // Eventhandler für Context menüs
        restore.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                log.debug("restore item: " + notesListView.getSelectionModel().getSelectedItem());
                restoreNote(notesListView.getSelectionModel().getSelectedItem());
            }

        });
        this.contextMenu4DeletedNotes = new ContextMenu();
        this.contextMenu4DeletedNotes.getItems().add(restore);
    }

    public ObservableList<NoteEntity> getNotesProperty() {
        return notes;
    }

    public ReadOnlyObjectProperty<NoteEntity> selectedNoteProperty() {
        return this.selectedNote;
    }

    public BooleanProperty showDeletedNotesProperty() {
        return this.showDeleted;
    }

    public void onShutdown() {
        this.saveAllModified();
    }

    private void restoreNote(NoteEntity selectedItem) {

        this.selectedNote.set(null);
        this.notes.remove(selectedItem);
        this.service.undelete(selectedItem);
    }

    public void addNewNote() {
        NoteEntity n = new NoteEntity("Neue Notiz", "");
        n.setDirty();

        this.notes.add(n);
        this.notesListView.getSelectionModel().select(n);
        this.selectedNote.set(n);
    }

    public void deleteSelectedNote() {
        NoteEntity n = this.selectedNote.get();
        if (n == null) {
            return;
        }
        this.selectedNote.set(null);
        this.notes.remove(n);
        this.service.delete(n);
    }

    private void saveAllModified() {
        for (NoteEntity note : notes) {
            if (note.isDirty()) {
                service.writeNoteEntity(note);
            }
        }
    }

    public void remoteSyncNotes() {
        saveAllModified();
        service.remoteSyncNote(this.notes);
    }
}
