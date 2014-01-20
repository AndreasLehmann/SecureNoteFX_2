/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteslist;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Andreas
 */
public class NoteTitleCellFactory implements Callback<ListView<NoteEntity>, ListCell<NoteEntity>> {

    public ListCell<NoteEntity> call(ListView<NoteEntity> param) {
        NoteListCell c2 = new NoteListCell();
        return c2;
    }
}
