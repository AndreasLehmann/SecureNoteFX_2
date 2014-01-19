/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.andreaslehmann.securenotefx.business.boundary;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.util.Iterator;
import org.junit.Test;

/**
 *
 * @author Andreas
 */
public class NoteServiceTest {
    
    public NoteServiceTest() {
    }
    
    @Test
    public void testListImpl() {
    
        NoteService s = new FilebasedNoteService("f:/tmp/MySecretNoteStorage_DEBUG/");
     
        for (Iterator<NoteEntity> iter = s.list().listIterator(); iter.hasNext();) {
            NoteEntity note = iter.next();
            System.out.println(note);
        }
    }

    @Test
    public void testListMock() {
    
        NoteService s = new MockNoteService();
        
        for (Iterator<NoteEntity> iter = s.list().listIterator(); iter.hasNext();) {
            NoteEntity note = iter.next();
            System.out.println(note);
        }
    }
    
}
