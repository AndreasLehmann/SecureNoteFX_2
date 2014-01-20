/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteeditor;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
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

        // Dieser Listener wird aktiv, wenn jemand  von außen das selectedNote setzt
        ChangeListener<NoteEntity> selectionListener = new ChangeListener<NoteEntity>() {
            @Override
            public void changed(ObservableValue<? extends NoteEntity> observable, NoteEntity oldNote, NoteEntity newNote) {
                titleTextField.setText(newNote.getTitle());
                bodyEditor.setHtmlText(newNote.getBody());
            }
        };
        this.selectedNotePropery.addListener(selectionListener);
    }

    public ObjectProperty<NoteEntity> selectedNotePropery() {
        return this.selectedNotePropery;
    }

}
