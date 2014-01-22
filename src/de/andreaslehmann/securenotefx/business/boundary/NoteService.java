/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.util.UUID;
import javafx.beans.property.ReadOnlyListProperty;

/**
 *
 * @author Andreas
 */
public interface NoteService {

    ReadOnlyListProperty<NoteEntity> list();

    boolean writeNoteEntity(NoteEntity n);

    NoteEntity readNoteEntity(UUID id);

    /*int synchronize();*/
    public void delete(NoteEntity e);

}
