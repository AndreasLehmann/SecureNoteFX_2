/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteslist;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.image.Image;

/**
 *
 * @author Andreas
 */
public class DirtyImageBinding extends ObjectBinding<Image> {

    NoteEntity note;
    Image image;

    public DirtyImageBinding(NoteEntity note, Image image) {
        super();
        this.note = note;
        this.image = image;
    }

    @Override
    protected Image computeValue() {
        if (!note.isSyncronizedProperty().get()) {
            return image;
        } else {
            return null;
        }
    }
}
