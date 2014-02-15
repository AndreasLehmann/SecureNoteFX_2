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
    static final Image image00 = new Image("status00.png");
    static final Image image10 = new Image("status10.png");
    static final Image image01 = new Image("status01.png");
    static final Image image11 = new Image("status11.png");

    public DirtyImageBinding(NoteEntity note) {
        super();
        this.note = note;
    }

    @Override
    protected Image computeValue() {
        if (!note.isDirtyProperty().get() && note.isSyncronizedProperty().get()) { 
            return image00;
        } 
        if (note.isDirtyProperty().get() && note.isSyncronizedProperty().get()) { 
            return image10;
        } 
        if (!note.isDirtyProperty().get() && !note.isSyncronizedProperty().get()) { 
            return image01;
        } 
        if (note.isDirtyProperty().get() && !note.isSyncronizedProperty().get()) { 
            return image11;
        } 
        return null;
    }
}
