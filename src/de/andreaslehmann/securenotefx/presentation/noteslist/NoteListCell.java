/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteslist;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 *
 * @author Andreas
 */
public class NoteListCell extends ListCell<NoteEntity> {

    HBox hbox;
    Label label = new Label("(Leer)");
    Pane pane = new Pane();
    Image image = new Image("sync.png");

    ImageView imageView = new ImageView();

    public NoteListCell() {
        super();
    }

    @Override
    public void updateItem(final NoteEntity note, boolean empty) {
        super.updateItem(note, empty);
        if (empty) {
            setText(null);  // No text in label of super class
            setGraphic(null);
        } else {
            if (hbox == null) {
                hbox = new HBox();
                hbox.getChildren().addAll(label, pane, imageView);
                HBox.setHgrow(pane, Priority.ALWAYS);
                imageView.setFitWidth(15);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
            }
            // Title binden
            label.textProperty().bind(note.titleProperty());
            // Icon binden
            final DirtyImageBinding dib = new DirtyImageBinding(note, image);
            // DirtyImageBinding aktualisieren, falls isDirty sich ändert.
            note.isDirtyProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable o) {
                    dib.invalidate();
                }
            });
            // PropertyListener binden
            imageView.imageProperty().bind(dib);
            // Grafik setzen
            setGraphic(hbox);
        }

    }
}
