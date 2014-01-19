/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.securenotefx;

import de.andreaslehmann.securenotefx.presentation.noteslist.NotesListPresenter;
import de.andreaslehmann.securenotefx.presentation.noteslist.NotesListView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Andreas
 */
public class SecureNoteFXPresenter implements Initializable {

    @FXML
    private VBox leftPane;
    @FXML
    private AnchorPane middlePane;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private ToolBar toolBar;

    NotesListPresenter notesListPresenter;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        NotesListView notesListView = new NotesListView();
        this.notesListPresenter = (NotesListPresenter) notesListView.getPresenter();

        // Ausdehung der Notizliste setzen und in VBox einhängen
        VBox.setVgrow(notesListView.getView(), Priority.ALWAYS);
        this.leftPane.getChildren().add(notesListView.getView());
        
    }

}
