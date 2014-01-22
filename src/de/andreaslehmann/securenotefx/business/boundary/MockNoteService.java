/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.andreaslehmann.securenotefx.business.boundary;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.util.ArrayList;
import java.util.UUID;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

/**
 *
 * @author Andreas
 */
public class MockNoteService implements NoteService {

    ListProperty<NoteEntity> noteList = new SimpleListProperty<>(javafx.collections.FXCollections.observableList(new ArrayList<NoteEntity>()));


    public MockNoteService() {
        
        noteList.add( new NoteEntity("Title1","Body 1"));
        noteList.add( new NoteEntity("Title2","<h1>HTML</h1>Message."));
        NoteEntity e3 = new NoteEntity("Title3","Body 1");
        e3.setDirty();
        noteList.add( e3);
        NoteEntity e4 = new NoteEntity("Title4","Body 1");
        e4.setDirty();
        noteList.add( e4);
        noteList.add( new NoteEntity("Title5","Body 1"));
        
    }

    @Override
    public ListProperty<NoteEntity> list() {
        return noteList;
    }
    @Override
    public boolean writeNoteEntity(NoteEntity n){
        throw new UnsupportedOperationException();
    }

    @Override
    public NoteEntity readNoteEntity(UUID id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //@Override
    public int synchronize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(NoteEntity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
