/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteslist;

import de.andreaslehmann.securenotefx.business.boundary.LocalFSNoteService;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
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

    @Inject
    private LocalFSNoteService service;

    /**
     * Hält eine Referenza fu die aktuell selektierte Notiz
     */
    private ObjectProperty<NoteEntity> selectedNote;

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

        registerListeners();
        this.loadFromLocalStore();
        setupListView();

    }

    private void loadFromLocalStore() {
        List<NoteEntity> all = service.list();
        for (NoteEntity e : all) {
            notes.add(e);
        }
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
                log.debug("selcted Note changed to:" + newValue.getTitle());

            }
        };
        this.notesListView.getSelectionModel().selectedItemProperty().addListener(selectionListener);
    }

    /**
     * ListView konfigurieren und Modell setzen
     */
    private void setupListView() {

        this.notesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.notesListView.itemsProperty().set(this.notes);
        // setze CellFactory um den Titel der Notizen anzuzeigen
        this.notesListView.setCellFactory(new NoteTitleCellFactory());

    }

    public ObservableList<NoteEntity> getNotesProperty() {
        return notes;
    }

    public ReadOnlyObjectProperty<NoteEntity> selectedNoteProperty() {
        return this.selectedNote;
    }

    public void onShutdown() {
        this.saveAllModified();
    }

    public void addNewNote() {
        NoteEntity n = new NoteEntity("Neue Notiz", "");
        n.setDirty();

        this.notes.add(n);
        this.notesListView.getSelectionModel().select(n);
        this.selectedNote.set(n);
    }

    private void saveAllModified() {
        for (NoteEntity note : notes) {
            if (note.isDirty()) {
                service.writeNoteEntity(note);
            }
        }
    }

}
