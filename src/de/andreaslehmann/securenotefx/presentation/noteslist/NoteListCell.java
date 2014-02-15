/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteslist;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
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
            // Tooltip setzen
            label.setTooltip(new Tooltip(getTooltip(note)));
            // Icon binden
            final DirtyImageBinding dib = new DirtyImageBinding(note);
            
            // DirtyImageBinding aktualisieren, falls isSychonized sich ändert.
            note.isSyncronizedProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable o) {
                    dib.invalidate();
                }
            });
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
    
    protected String getTooltip(NoteEntity n){
        StringBuilder b=new StringBuilder();
        b.append("Created=");
        b.append(SimpleDateFormat.getDateTimeInstance().format(new Date(n.getCreatedOn())));
        b.append("\nLastSaved=");
        b.append(SimpleDateFormat.getDateTimeInstance().format(new Date(n.getLastSavedOn())));
        b.append("\nDeleted=");
        b.append(SimpleDateFormat.getDateTimeInstance().format(new Date(n.getDeletedOn())));
        
        return b.toString();
    }
    
}
