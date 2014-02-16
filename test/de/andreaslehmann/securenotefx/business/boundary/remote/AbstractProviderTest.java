/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary.remote;

import de.andreaslehmann.securenotefx.business.boundary.remote.AbstractProvider;
import de.andreaslehmann.securenotefx.business.entity.ChangeSet;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Andreas
 */
public class AbstractProviderTest {

    DummyTestClass abstractProxyClass = new DummyTestClass();

    NoteEntity e1_1;
    NoteEntity e1_2;
    NoteEntity e1_3;
    NoteEntity tmp; // temporäre Variable zum tests des Schreibens mit Dummy von writeNoteEntity()

    @Before
    public void tearUp() {
        e1_1 = new NoteEntity();
        e1_1.setLastSavedOn(12121212);
        e1_2 = new NoteEntity();
        e1_2.setLastSavedOn(12121213);
        e1_3 = new NoteEntity();
        e1_3.setLastSavedOn(12121214);
        tmp = null;
    }

    @Test
    public void testSynchronize_Null() {

        List<ChangeSet> changes;
        // null input
        changes = abstractProxyClass.compare(null, null);
        assertNull(changes);
    }

    @Test
    public void testSynchronize_NO_CHANGES() {
        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        assertEquals("Änderungsliste ist nicht leer.", 0, changes.size());
    }

    @Test
    public void testCompareRemote_NO_CHANGES() {
        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        e1_2.setRemoteTimestamp(1234L);
        e1_2.setLastSavedOn(1234L);

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2);
        localList.add(e1_3);

        assertFalse(e1_1.isSyncronized()); // Vor dem Compare sind die lokalen Elemente unsynchronisert
        assertFalse(e1_2.isSyncronized());
        assertFalse(e1_3.isSyncronized());

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        //System.out.println(changes);
        assertEquals("Änderungsliste ist nicht leer.", 0, changes.size());

        assertTrue(e1_1.isSyncronized()); // Nach dem Compare sind die lokalen Elemente synchronisert
        assertTrue(e1_2.isSyncronized());
        assertTrue(e1_3.isSyncronized());
    }

    @Test
    public void testSynchronize_REMOTE_ADD() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2);
        //localList.add(e1_3); kein passendes lokales Element

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.REMOTE_ADDED, changes.get(0).changeType);

    }

    @Test
    public void testCompareRemote_REMOTE_ADD() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;
        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        e1_2.setRemoteTimestamp(1234L);
        e1_2.setLastSavedOn(1234L);

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2);
        remoteList.add(e1_3); // neues Element

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2);
        //localList.add(e1_3); kein passendes lokales Element

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.REMOTE_ADDED, changes.get(0).changeType);
        assertNull(changes.get(0).oldNote);
        assertEquals(e1_3, changes.get(0).newNote);

        /**
         * ************'################################## DAS KANN NICHT
         * gehen, da es von CompareRemote noch nicht implameneiertt ist 1!!
         *
         * ##################################################
         *
         */
    }

    @Test
    public void testSynchronize_LOCAL_ADD() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2);
        //remoteList.add(e1_3); kein passendes remote Element

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2);
        localList.add(e1_3);// neues Element

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.LOCAL_ADDED, changes.get(0).changeType);
    }

    @Test
    public void testCompareRemote_LOCAL_ADD() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        e1_2.setRemoteTimestamp(1234L);
        e1_2.setLastSavedOn(1234L);

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2);
        //remoteList.add(e1_3); kein passendes remote Element

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2);
        localList.add(e1_3); // neues Element

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.LOCAL_ADDED, changes.get(0).changeType);
        assertNull(changes.get(0).oldNote);
        assertEquals(e1_3, changes.get(0).newNote);
    }

    /**
     * In diesem Fall ist das Save-Date der einen Entität höher das ist aber nur
     * dann der Fall, wenn die Datei bereits gespeichert wurde.
     *
     */
    @Test
    public void testSynchronize_LOCAL_UPDATE1() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000050L);
        e1_2_local.setLastSavedOn(1000000060L); // 10ms neuer , nicht dirty!

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.LOCAL_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_local, changes.get(0).newNote);
    }

    /**
     * In diesem Fall ist das Save-Date der einen Entität höher das ist aber nur
     * dann der Fall, wenn die Datei bereits gespeichert wurde.
     *
     */
    @Test
    public void testCompareRemote_LOCAL_UPDATE1() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        NoteEntity e1_2_remote = e1_2.cloneElement();
        e1_2_remote.setRemoteTimestamp(1000000050L);
        e1_2_remote.setLastSavedOn(1000000050L);

        // Element 2 lokal vorbereiten
        NoteEntity e1_2_local = e1_2.cloneElement();
        e1_2_local.setRemoteTimestamp(1000000050L);
        e1_2_local.setLastSavedOn(1000000060L); // 10ms neuer , nicht dirty!

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.LOCAL_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_local, changes.get(0).newNote);
        assertSame(e1_2_remote, changes.get(0).oldNote);
    }

    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher noch nicht
     * gespeichert. Die Remote Datei ist unverändert.
     */
    @Test
    public void testSynchronize_LOCAL_DELETE1() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000050L);

        e1_2_local.setLastSavedOn(1000000060L); // 10ms neuer
        e1_2_local.delete(); // löschen + dirty setzen!

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.LOCAL_DELETED, changes.get(0).changeType);
        assertSame(e1_2_local, changes.get(0).newNote);
    }

    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher bereits
     * gespeichert. Die Remote Datei ist unverändert.
     */
    @Test
    public void testSynchronize_LOCAL_DELETE2() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000050L);
        e1_2_local.delete(); // löschen
        e1_2_local.setLastSavedOn(1000000060L); // 10ms neuer

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.LOCAL_DELETED, changes.get(0).changeType);
        assertSame(e1_2_local, changes.get(0).newNote);
    }

    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher bereits
     * gespeichert. Die Remote Datei ist unverändert.
     */
    @Test
    public void testCompareRemote_LOCAL_DELETE2() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;
        
        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setRemoteTimestamp(1000000010L);// 
        e1_2_remote.setLastSavedOn(1000000010L);// 

        e1_2_local.setRemoteTimestamp(1000000010L); // 
        e1_2_local.delete();
        e1_2_local.setLastSavedOn(1000000060L); // 10ms neuer

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        System.out.println(changes);
        assertEquals(1, changes.size());
        // Ich verzeichte auf den Spezialfall LOCAL_DELETE, dieser Fall wird als LOCAL_UPDATE erkannt
        // assertEquals(ChangeSet.ChangeTyp.LOCAL_DELETED, changes.get(0).changeType);
        
        assertEquals(ChangeSet.ChangeTyp.LOCAL_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_local, changes.get(0).newNote);
        assertSame(e1_2_remote, changes.get(0).oldNote);
    }

    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher noch nicht
     * gespeichert. Die Remote Datei wurde aktualisiert und ist neuer!
     */
    @Test
    public void testSynchronize_LOCAL_DELETE3() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000070L); // 10ms neuer

        e1_2_local.setLastSavedOn(1000000060L);
        e1_2_local.delete(); // löschen + dirty setzen!

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.BOTH_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
    }
    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher noch nicht
     * gespeichert. Die Remote Datei wurde aktualisiert und ist neuer!
     */
    @Test
    public void testCompareRemote_LOCAL_DELETE3() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setRemoteTimestamp(1000000080L);// neuer
        e1_2_remote.setLastSavedOn(1000000080L);// 

        e1_2_local.setRemoteTimestamp(1000000010L); // 
        e1_2_local.setLastSavedOn(1000000020L); // 
        e1_2_local.delete();

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);


        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.BOTH_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
    }

    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher noch nicht
     * gespeichert. Die Remote Datei wurde auch gelöscht und ist neuer!
     */
    @Test
    public void testSynchronize_LOCAL_DELETE4() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.delete();
        e1_2_remote.setLastSavedOn(1000000070L); // 10ms neuer

        e1_2_local.setLastSavedOn(1000000060L);
        e1_2_local.delete(); // löschen + dirty setzen!

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.BOTH_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
    }

    /**
     * In diesem Fall wurde eine remote Datei gelöscht. Die loakle ist
     * unverändert.
     */
    @Test
    public void testSynchronize_REMOTE_DELETE1() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_local.setLastSavedOn(1000000050L); //älter aber nicht dirty
        e1_2_remote.delete(); // löschen
        e1_2_remote.setLastSavedOn(1000000060L); // 10ms neuer

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.REMOTE_DELETED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
    }

    /**
     * In diesem Fall wurde eine remote Datei gelöscht. Die loakle ist
     * unverändert.
     */
    @Test
    public void testCompareRemote_REMOTE_DELETE1() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setRemoteTimestamp(1000000050L);// neuer
        e1_2_remote.delete();
        e1_2_remote.setLastSavedOn(1000000050L);// 

        e1_2_local.setRemoteTimestamp(1000000010L); // 
        e1_2_local.setLastSavedOn(1000000010L); // 

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        // Ich verzeichte auf den Spezialfall REMOTE_DELETED, dieser Fall wird als REMOTE_UPDATE erkannt
        // assertEquals(ChangeSet.ChangeTyp.REMOTE_DELETED, changes.get(0).changeType);
        assertEquals(ChangeSet.ChangeTyp.REMOTE_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
        assertSame(e1_2_local, changes.get(0).oldNote);
    }


    /**
     * In diesem Fall wurde eine remote Datei gelöscht. Die loakle ist
     * verändert, aber noch nicht gespeichert (dirty).
     */
    @Test
    public void testSynchronize_REMOTE_DELETE2() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_local.setLastSavedOn(1000000050L); //älter 
        e1_2_local.setTitle("A");        // und dirty

        e1_2_remote.delete(); // löschen
        e1_2_remote.setLastSavedOn(1000000060L); // 10ms neuer

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.BOTH_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
    }

    /**
     * In diesem Fall ist die Entität noch nicht gespeichert. Daher sind die
     * SaveDates identisch. Aber das eine Element wurde verändert.
     */
    @Test
    public void testSynchronize_LOCAL_UPDATE2() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_local.setTitle("xyz"); // es geht nicht um den Titel,sondern darum, dass das DIRTY Flag gesetzt werden soll.

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.LOCAL_UPDATED, changes.get(0).changeType);
    }

    /**
     * Remote Datei ist neuer, lokale Datei unverändert.
     */
    @Test
    public void testSynchronize_REMOTE_UPDATE1() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000070L);// 10ms neuer
        e1_2_local.setLastSavedOn(1000000060L); // nicht dirty

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.REMOTE_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
    }

    /**
     * Remote Datei ist neuer, lokale Datei unverändert.
     */
    @Test
    public void testCompareRemote_REMOTE_UPDATE1() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setRemoteTimestamp(1000000070L);// 10ms neuer
        e1_2_remote.setLastSavedOn(1000000070L);// 10ms neuer

        e1_2_local.setRemoteTimestamp(1000000060L); // nicht dirty
        e1_2_local.setLastSavedOn(1000000060L); // nicht dirty

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote); // Neuer
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local); // älter
        localList.add(e1_3);

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.REMOTE_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
        assertSame(e1_2_local, changes.get(0).oldNote);
    }

    /**
     * Remote Datei ist neuer, lokale Datei dirty!.
     */
    @Test
    public void testSynchronize_REMOTE_UPDATE2() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000070L);// 10ms neuer
        e1_2_local.setLastSavedOn(1000000060L);
        e1_2_local.setTitle("Q");// dirtY

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.BOTH_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);

    }

    /**
     * Remote Datei ist neuer, lokale Datei dirty!.
     */
    @Test
    public void testCompareRemote_REMOTE_UPDATE2() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        // Element 1 vorbereiten
        e1_1.setRemoteTimestamp(1234L);
        e1_1.setLastSavedOn(1234L);

        // Element 2 remote vorbereiten
        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setRemoteTimestamp(1000000070L);// 10ms neuer
        e1_2_remote.setLastSavedOn(1000000070L);// 10ms neuer

        e1_2_local.setRemoteTimestamp(1000000060L); // älter
        e1_2_local.setLastSavedOn(1000000060L); // älter
        e1_2_local.setTitle("Q");// und dirty!

        // Element 3 vorbereiten
        e1_3.setRemoteTimestamp(1234L);
        e1_3.setLastSavedOn(1234L);

        remoteList = new ArrayList<>();
        remoteList.add(e1_1);
        remoteList.add(e1_2_remote); // neuer
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local); // älter & Dirty
        localList.add(e1_3);

        changes = abstractProxyClass.compareRemote(remoteList, localList);
        //System.out.println(changes);
        assertEquals(1, changes.size());
        assertEquals(ChangeSet.ChangeTyp.BOTH_UPDATED, changes.get(0).changeType);
        assertSame(e1_2_remote, changes.get(0).newNote);
        assertSame(e1_2_local, changes.get(0).oldNote);
    }

    @Test
    public void testSynchronize_MULTI1() {

        List<ChangeSet> changes;
        List<NoteEntity> remoteList;
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000070L);// 10ms neuer
        e1_2_local.setLastSavedOn(1000000060L);

        remoteList = new ArrayList<>();
        //remoteList.add(e1_1); // noch nicht gespeichert
        remoteList.add(e1_2_remote);
        remoteList.add(e1_3);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        //localList.add(e1_3); noch nicht gelesen

        changes = abstractProxyClass.compare(remoteList, localList);
        //System.out.println(changes);
        assertEquals(3, changes.size());
        assertEquals(ChangeSet.ChangeTyp.REMOTE_UPDATED, changes.get(0).changeType);
        assertEquals(ChangeSet.ChangeTyp.REMOTE_ADDED, changes.get(1).changeType);
        assertEquals(ChangeSet.ChangeTyp.LOCAL_ADDED, changes.get(2).changeType);
    }

    @Test
    public void testApplyChanges_REMOTE_ADD() {

        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList = new ArrayList<>();

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_ADDED, null, e1_1));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(1, localList.size());
        assertSame(e1_1, localList.get(0));
    }

    /**
     * Datei wurde Remote gelöscht, aber es gibt keine passende lokale Datei
     * (z.B. wenn das Programm startet)
     */
    @Test
    public void testApplyChanges_REMOTE_DELETE1() {

        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList = new ArrayList<>();

        e1_1.delete();

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_DELETED, null, e1_1));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(1, localList.size());
        assertSame(e1_1, localList.get(0));
        assertTrue(localList.get(0).isDeleted());
    }

    /**
     * Datei wurde Remote gelöscht, die lokale Datei ist noch vorhanden, aber
     * älter
     */
    @Test
    public void testApplyChanges_REMOTE_DELETE2() {

        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList = new ArrayList<>();

        e1_1.setLastSavedOn(1212121000); // lokale Datei
        localList.add(e1_1);

        e1_2 = e1_1.cloneElement();
        e1_2.delete();
        e1_2.setLastSavedOn(1212121000 + 10); // remote Datei

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_DELETED, e1_1, e1_2));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(1, localList.size());
        assertSame(e1_2, localList.get(0));
        assertTrue(localList.get(0).isDeleted());
    }

    @Test
    public void testApplyChanges_LOCAL_ADD() {
        assert (tmp == null);
        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList = new ArrayList<>();

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_ADDED, null, e1_1));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(0, localList.size());
        assertSame(e1_1, tmp); // Schreibversuch?

    }

    @Test
    public void testApplyChanges_REMOTE_UPDATE() {

        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000070L);// 10ms neuer
        e1_2_local.setLastSavedOn(1000000060L);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_UPDATED, e1_2_local, e1_2_remote));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_remote, localList.get(1));
    }

    @Test
    public void testApplyChanges_REMOTE_DELETE() {

        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.delete();
        e1_2_remote.setLastSavedOn(1000000070L);// 10ms neuer

        e1_2_local.setLastSavedOn(1000000060L);

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_DELETED, e1_2_local, e1_2_remote));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_remote, localList.get(1));
    }

    @Test
    public void testApplyChanges_LOCAL_DELETE1() {

        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000050L);
        e1_2_local.setLastSavedOn(1000000060L);// 10ms neuer
        e1_2_local.delete();

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_DELETED, e1_2_remote, e1_2_local));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_local, localList.get(1));
        assertSame(e1_2_local, tmp); // Schreibversuch?
    }

    @Test
    public void testApplyChanges_LOCAL_DELETE2() {

        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        // last Save Date ist bei beiden identisch (nicht gespeichert)
        e1_2_local.delete();

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_DELETED, e1_2_remote, e1_2_local));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_local, localList.get(1));
        assertSame(e1_2_local, tmp); // Schreibversuch?
    }

    @Test
    public void testApplyChanges_LOCAL_UPDATE1() {

        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000050L);
        e1_2_local.setLastSavedOn(1000000060L);// 10ms neuer

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_UPDATED, e1_2_remote, e1_2_local));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_local, localList.get(1));
        assertSame(e1_2_local, tmp); // Schreibversuch?
    }

    /**
     * In diesem Fall ist die Entität noch nicht gespeichert. Daher sind die
     * SaveDates identisch. Aber das eine Element wurde verändert.
     */
    @Test
    public void testApplyChanges_LOCAL_UPDATE2() {
        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_local.setDirty();

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_UPDATED, e1_2_remote, e1_2_local));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_local, localList.get(1));
        assertSame(e1_2_local, tmp); // Schreibversuch?

    }

    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher noch nicht
     * gespeichert. Die Remote Datei wurde aktualisiert und ist neuer!
     */
    @Test
    public void testApplyChanges_BOTH_CHANGED_1() {
        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000070L); // 10ms neuer

        e1_2_local.setLastSavedOn(1000000060L);
        e1_2_local.delete();

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.BOTH_UPDATED, e1_2_local, e1_2_remote));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_remote, localList.get(1));
        assertNull(tmp); // kein Schreibversuch

    }

    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher noch nicht
     * gespeichert. Die Remote Datei wurde auch gelöscht und ist neuer!
     */
    @Test
    public void testApplyChanges_BOTH_CHANGED_2() {
        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.delete();
        e1_2_remote.setLastSavedOn(1000000070L); // 10ms neuer

        e1_2_local.setLastSavedOn(1000000060L);
        e1_2_local.delete();

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.BOTH_UPDATED, e1_2_local, e1_2_remote));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_remote, localList.get(1));
        assertNull(tmp); // kein Schreibversuch
    }

    /**
     * In diesem Fall wurde eine lokale Datei gelöscht und vorher noch nicht
     * gespeichert. Die Remote Datei wurde auch gelöscht und ist älter!
     */
    @Test
    public void testApplyChanges_BOTH_CHANGED_3() {
        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.delete();
        e1_2_remote.setLastSavedOn(1000000060L);

        e1_2_local.setLastSavedOn(1000000080L); // 20ms neuer
        e1_2_local.delete();

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.BOTH_UPDATED, e1_2_remote, e1_2_local));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_local, localList.get(1));
        assertSame(e1_2_local, tmp); //  Schreibversuch
    }

    /**
     * In diesem Fall wurde eine lokale Datei geändert und noch nicht
     * gespeichert. Die Remote Datei wurde gelöscht und ist neuer!
     */
    @Test
    public void testApplyChanges_BOTH_CHANGED_4() {
        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.delete();
        e1_2_remote.setLastSavedOn(1000000070L); // 10ms neuer

        e1_2_local.setLastSavedOn(1000000060L);
        e1_2_local.setTitle("Q"); // geändert

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.BOTH_UPDATED, e1_2_local, e1_2_remote));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertSame(e1_2_local, localList.get(1));
        assertSame(e1_2_local, tmp); //  Schreibversuch
    }

    /**
     * In diesem Fall wurde eine lokale Datei geändert und noch nicht
     * gespeichert. Die Remote Datei wurde auch geändert und ist neuer!
     */
    @Test
    public void testApplyChanges_BOTH_CHANGED_5() {
        List<ChangeSet> changes = new ArrayList<>();
        List<NoteEntity> localList;

        NoteEntity e1_2_remote = e1_2.cloneElement();
        NoteEntity e1_2_local = e1_2.cloneElement();

        e1_2_remote.setLastSavedOn(1000000070L); // 10ms neuer

        e1_2_local.setLastSavedOn(1000000060L);
        e1_2_local.setTitle("Q"); // geändert

        localList = new ArrayList<>();
        localList.add(e1_1);
        localList.add(e1_2_local);
        localList.add(e1_3);

        changes.add(new ChangeSet(ChangeSet.ChangeTyp.BOTH_UPDATED, e1_2_local, e1_2_remote));

        abstractProxyClass.applyChanges(changes, localList);

        assertEquals(3, localList.size());
        assertNotSame(e1_2_local, localList.get(1));
        assertNotSame(e1_2_remote, localList.get(1));
        // assertSame(localList.get(1), tmp); //  das schreiben der Änderungen nach einem Konflikt wurde deaktiviert.
        assertTrue(localList.get(1).getTitle().contains("Konflikt"));
    }

    // ###############################################################
    /**
     * This class is needed to test the abstract Base Class
     *
     */
    class DummyTestClass extends AbstractProvider {

        @Override
        public boolean remoteWrite(NoteEntity newNote) {
            tmp = newNote;
            return true;
        }
    }

}
