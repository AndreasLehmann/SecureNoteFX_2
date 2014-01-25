/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Andreas
 */
public interface NoteService {

    List<NoteEntity> list();

    boolean writeNoteEntity(NoteEntity n);

    NoteEntity readNoteEntity(UUID id);

    /*int synchronize();*/
    public void delete(NoteEntity e);

}
