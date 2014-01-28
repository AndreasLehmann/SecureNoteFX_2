/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.securenotefx;

import de.andreaslehmann.securenotefx.presentation.noteeditor.NoteEditorPresenter;
import de.andreaslehmann.securenotefx.presentation.noteeditor.NoteEditorView;
import de.andreaslehmann.securenotefx.presentation.noteslist.NotesListPresenter;
import de.andreaslehmann.securenotefx.presentation.noteslist.NotesListView;
import de.andreaslehmann.securenotefx.utility.StageManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialogs;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andreas
 */
public class SecureNoteFXPresenter implements Initializable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @FXML
    private VBox leftPane;
    @FXML
    private VBox middlePane;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private ToolBar toolBar;

    NotesListPresenter notesListPresenter;
    NoteEditorPresenter noteEditorPresenter;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnDelete;
    @FXML
    private SplitPane splitPane;
    @FXML
    private ToggleButton btnTrashcan;

    @FXML
    private void btnDeleteClicked(ActionEvent event) {
        log.debug("btnDeleteClicked");
        notesListPresenter.deleteSelectedNote();
    }

    @FXML
    private void btnNewClicked(ActionEvent event) {
        log.debug("btnNewClicked");
        notesListPresenter.addNewNote();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Erzeuge Notizenliste
        NotesListView notesListView = new NotesListView();
        this.notesListPresenter = (NotesListPresenter) notesListView.getPresenter();
        // Ausdehung der Notizliste setzen und in VBox einhängen
        VBox.setVgrow(notesListView.getView(), Priority.ALWAYS);
        this.leftPane.getChildren().add(notesListView.getView());

        // Erzeuge Notiz-Editor
        NoteEditorView noteEditorView = new NoteEditorView();
        this.noteEditorPresenter = (NoteEditorPresenter) noteEditorView.getPresenter();
        // Hänge Listerner an die NoteList
        this.noteEditorPresenter.selectedNotePropery().bind(this.notesListPresenter.selectedNoteProperty());
        // Ausdehung setzen und in VBox einhängen
        VBox.setVgrow(noteEditorView.getView(), Priority.ALWAYS);
        this.middlePane.getChildren().add(noteEditorView.getView());

        // "Löschen" Button wird nur enabled, wenn eine Notiz selektiert ist.
        this.btnDelete.disableProperty().bind(this.notesListPresenter.selectedNoteProperty().isNull());

        // "Löschen" und "Neu" Button nur aktivieren, wenn nicht in Papierkorb-Modus
        this.btnDelete.disableProperty().bind(this.btnTrashcan.selectedProperty());
        this.btnNew.disableProperty().bind(this.btnTrashcan.selectedProperty());
        
        // Binde den "ShowDeleted" Button an das Property der NotizenListe
        this.notesListPresenter.showDeletedNotesProperty().bind(this.btnTrashcan.selectedProperty());
        
    }

    public boolean shutdownAllowed() {

        // send shutdown request to child windows
        this.noteEditorPresenter.onShutdown();
        this.notesListPresenter.onShutdown();

        Dialogs.DialogResponse r = Dialogs.showConfirmDialog(
                StageManager.getInstance().getPrimaryStage(),
                "Soll die Anwendung geschlossen werden?",
                "Anwendung beenden",
                "SecureNoteFX",
                Dialogs.DialogOptions.YES_NO);
        return (r == r.YES);
    }
}
