/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteeditor;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.HTMLEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andreas
 */
public class NoteEditorPresenter implements Initializable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Referenz der aktuell selektierten Notiz
     */
    private ObjectProperty<NoteEntity> selectedNotePropery;

    @FXML
    private TextField titleTextField;
    @FXML
    private HTMLEditor bodyEditor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.selectedNotePropery = new SimpleObjectProperty<>();
        this.bodyEditor.disableProperty().set(true);

        // eigene Click-Handler registrieren
        customizeHTMLEditor();

        // Dieser Listener wird aktiv, wenn jemand  von außen das selectedNote setzt
        ChangeListener<NoteEntity> selectionListener = new ChangeListener<NoteEntity>() {
            @Override
            public void changed(ObservableValue<? extends NoteEntity> observable, NoteEntity oldNote, NoteEntity newNote) {
                titleTextField.setText(newNote.getTitle());
                bodyEditor.setHtmlText(newNote.getBody());
                bodyEditor.disableProperty().set(false);
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        titleTextField.requestFocus();
                    }
                });

            }
        };
        this.selectedNotePropery.addListener(selectionListener);
    }

    public ObjectProperty<NoteEntity> selectedNotePropery() {
        return this.selectedNotePropery;
    }

    @FXML
    private void titleTextField_keyReleased(KeyEvent event) {
        sendChangesToModel();
    }

    @FXML
    private void bodyEditor_keyReleased(KeyEvent event) {
        sendChangesToModel();
    }

    void bodyEditor_ToolbarClicked() {
        sendChangesToModel();
    }

    private void customizeHTMLEditor() {
        // eigenen Handler für clicks auf die HTML-Editor Toolbar setzen.
        // TODO: refactor!
        Node node = this.bodyEditor.lookup(".top-toolbar");
        if (node != null && node instanceof ToolBar) {
            ToolBar bar = (ToolBar) node;
            HTMLEditorToolbarWrapper.wrapButtons(bar, this);
        }
        node = this.bodyEditor.lookup(".bottom-toolbar");
        if (node != null && node instanceof ToolBar) {
            ToolBar bar = (ToolBar) node;
            HTMLEditorToolbarWrapper.wrapButtons(bar, this);
        }
    }

    protected void sendChangesToModel() {
        if (this.selectedNotePropery != null && this.selectedNotePropery.get() != null) {
            this.selectedNotePropery.get().setTitle(titleTextField.getText());
            this.selectedNotePropery.get().setBody(bodyEditor.getHtmlText());
        }
    }

    /**
     * Diese Methode wird beim Schließen-Request aufgerufen. Der Benutzer kann
     * aber noch abbrechen
     */
    public void onShutdown() {
        sendChangesToModel();
    }

}
