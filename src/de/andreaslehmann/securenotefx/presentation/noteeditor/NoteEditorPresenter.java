/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteeditor;

import java.net.URL;
import java.util.ResourceBundle;
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
    @FXML
    private TextField titleTextField;
    @FXML
    private HTMLEditor bodyEditor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
