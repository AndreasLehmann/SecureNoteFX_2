/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary.remote;

import de.andreaslehmann.securenotefx.business.entity.ChangeSet;
import static de.andreaslehmann.securenotefx.business.entity.ChangeSet.ChangeTyp.BOTH_UPDATED;
import static de.andreaslehmann.securenotefx.business.entity.ChangeSet.ChangeTyp.LOCAL_ADDED;
import static de.andreaslehmann.securenotefx.business.entity.ChangeSet.ChangeTyp.LOCAL_DELETED;
import static de.andreaslehmann.securenotefx.business.entity.ChangeSet.ChangeTyp.LOCAL_UPDATED;
import static de.andreaslehmann.securenotefx.business.entity.ChangeSet.ChangeTyp.REMOTE_ADDED;
import static de.andreaslehmann.securenotefx.business.entity.ChangeSet.ChangeTyp.REMOTE_DELETED;
import static de.andreaslehmann.securenotefx.business.entity.ChangeSet.ChangeTyp.REMOTE_UPDATED;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andreas
 */
public abstract class AbstractProvider {

    /**
     * Vergleicht die beiden Eingabelisten und gibt alle Änderungen zwischen den
     * beiden Listen als ChangeSet zuürck.
     *
     * Diese Methode arbeitet mit der isDirty-Methode, die neuere Version sollte
     * mit isSynchronized arbeiten.
     *
     * @see ChangeSet
     *
     * @param remoteList Liste der entfernt gespeicherten Objekte
     * @param localList Liste der Objekte im Speicher
     * @return Liste der Änderungen oder null, wenn eine der Eingabelisten null
     * ist.
     *
     */
    protected List<ChangeSet> compare(List<NoteEntity> remoteList, List<NoteEntity> localList) {

        if (remoteList == null || localList == null) {
            return null;
        }

        List<ChangeSet> changes = new ArrayList<>();
        // Welche Elemente von der Festplatte sind in der Liste im Speicher vorhanden
        for (NoteEntity remoteNote : remoteList) {
            if (localList.contains(remoteNote)) {
                // Element ist bereits vorhanden und unverändert (UUID und lastSaveDate). 
                // Prüfe, ob es in der Zwischenzeit geändert wurde (LOCAL_UPDATED)!
                NoteEntity localNote = localList.get(localList.indexOf(remoteNote));
                if (localNote.isDirty()) {
                    changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_UPDATED, remoteNote, localNote));
                }
            } else {
                // Element ist in der lokalen Liste nicht enthalten.

                //  Es ist neu hinzugekommen (REMOTE_ADDED) oder wurde 
                // von einem anderen Client verändert (REMOTE_UPDATED)
                // oder wurde lokal gelöscht (LOCAL_DELETED).
                boolean found = false;
                // Suche einen 'Vorgänger' mit gleicher UUID
                NoteEntity localNote = null;
                for (NoteEntity localE : localList) {
                    if (localE.getUniqueKey().equals(remoteNote.getUniqueKey())) {
                        found = true;
                        localNote = localE;
                        break;
                    }
                }
                if (found) // es gibt einen Vorgänger
                {
                    if (localNote.isDirty() && (localNote.getLastSavedOn() < remoteNote.getLastSavedOn())) {
                        // Change Konflikt ! Beide geändert!                       
                        changes.add(new ChangeSet(ChangeSet.ChangeTyp.BOTH_UPDATED, localNote, remoteNote));

                    } else if (localNote.getLastSavedOn() > remoteNote.getLastSavedOn()) {
                        //-- Lokales Element neuer!
                        if (localNote.isDeleted()) {
                            changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_DELETED, remoteNote, localNote));
                        } else {
                            changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_UPDATED, remoteNote, localNote));
                        }
                    } else {
                        //-- Remote Element neuer!
                        if (remoteNote.isDeleted()) {
                            changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_DELETED, localNote, remoteNote));
                        } else {
                            changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_UPDATED, localNote, remoteNote));
                        }
                    }
                } else {
                    changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_ADDED, null, remoteNote));
                }

            }
        }
        // Welche lokalen Elemente sind auf der Festplatte nicht gespichert?
        for (NoteEntity localNote : localList) {
            if (!remoteList.contains(localNote)) {
                // Element ist nur lokal vorhanden. Es wurde 'noch' nicht gespeichert.
                // Das Element ist entweder neu oder wurde verändert.
                boolean found = false;
                for (NoteEntity remoteE : remoteList) {
                    if (remoteE.getUniqueKey().equals(localNote.getUniqueKey())) {
                        found = true;
                        break;
                    }
                }
                if (found) // es gibt einen Vorgänger
                {
                    // tue nix, der Fall wird bereits oben behandelt!
                } else {
                    changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_ADDED, null, localNote));
                }
            }
        }
        return changes;
    }

    /**
     * Vergleicht die beiden Eingabelisten und gibt alle Änderungen zwischen den
     * beiden Listen als ChangeSet zuürck.
     *
     * @see ChangeSet
     *
     * @param remoteList Liste der entfernt gespeicherten Objekte
     * @param localList Liste der Objekte im Speicher
     * @return Liste der Änderungen oder null, wenn eine der Eingabelisten null
     * ist.
     */
    protected List<ChangeSet> compareRemote(List<NoteEntity> remoteList, List<NoteEntity> localList) {
        if (remoteList == null || localList == null) {
            return null;
        }

        List<ChangeSet> changes = new ArrayList<>();

        // Suche Notizen, welche lokal, aber nicht remote vorhanden sind (LOCAL_ADDED)
        // oder lokal neuer sind (LOCAL_UPDATED) bzw. remote neuer sind (REMOTE_UPDATED)
        for (NoteEntity localNote : localList) {
            boolean found = false;
            for (NoteEntity remoteNote : remoteList) {
                if (remoteNote.getUniqueKey().equals(localNote.getUniqueKey())) {
                    found = true; // Notiz ist remote bereits vorhanden

                    if (localNote.getLastSavedOn() == remoteNote.getRemoteTimestamp()) { // beide haben den gleichen remote Zeitstempel?
                        localNote.setSyncronized();
                    } else if (localNote.getLastSavedOn() > remoteNote.getRemoteTimestamp()) { // Ist die lokale neuer?
                        // Ja, die lokale ist neuer.
                        // Pürfe, ob remote mittlerweile verändert wurde
                        if (localNote.getRemoteTimestamp() == remoteNote.getRemoteTimestamp()) {
                            // Remote hat sich nichts verändert, kann überschrieben werden
                            changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_UPDATED, remoteNote, localNote));
                        } else {
                            // Remote wurde auch bereits verändert, also gab es einen Konflikt
                            changes.add(new ChangeSet(ChangeSet.ChangeTyp.BOTH_UPDATED, remoteNote, localNote));
                        }
                    } else { // remote zeit ist neuer
                        // Ok, wurde lokal etwas verändert?
                        if (localNote.getLastSavedOn() == localNote.getRemoteTimestamp() && !localNote.isDirty()) {
                            // Lokal wurde nichts verändert - lokale Datei überschreiben!
                            changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_UPDATED, localNote, remoteNote));
                        } else {
                            changes.add(new ChangeSet(ChangeSet.ChangeTyp.BOTH_UPDATED, localNote, remoteNote));
                        }
                    }
                    break;
                }
            }
            if (!found) {
                changes.add(new ChangeSet(ChangeSet.ChangeTyp.LOCAL_ADDED, null, localNote));
            }
        }

        // Suche jetzt in der Remote-Liste nach Notizen, die lokal nicht vorhanden sind.
        for (NoteEntity remoteNote : remoteList) {
            boolean found = false;
            for (NoteEntity localNote : localList) {
                if (remoteNote.getUniqueKey().equals(localNote.getUniqueKey())) {
                    found = true; // Notiz ist remote bereits vorhanden
                    break;
                }
            }
            if (!found) {
                changes.add(new ChangeSet(ChangeSet.ChangeTyp.REMOTE_ADDED, null, remoteNote));
            }
        }

        return changes;
    }

    /**
     * Führt die Änderungen zusammen.
     *
     * @param changes
     * @param activeNoteList
     */
    protected void applyChanges(List<ChangeSet> changes, List<NoteEntity> activeNoteList) {
        int idx;

        for (ChangeSet change : changes) {
            switch (change.changeType) {
                case LOCAL_ADDED:
                    remoteWrite(change.newNote);
                    break;
                case LOCAL_UPDATED:
                    remoteWrite(change.newNote);
                    break;
                case LOCAL_DELETED:
                    remoteWrite(change.newNote);
                    break;
                case REMOTE_ADDED:
                    activeNoteList.add(change.newNote);
                    break;
                case REMOTE_UPDATED:
                    idx = activeNoteList.indexOf(change.oldNote);
                    activeNoteList.set(idx, change.newNote);
                    break;
                case REMOTE_DELETED:
                    if (change.oldNote != null) {
                        idx = activeNoteList.indexOf(change.oldNote);
                        activeNoteList.set(idx, change.newNote);
                    } else {
                        activeNoteList.add(change.newNote);
                    }
                    break;

                case BOTH_UPDATED:

                    if (change.oldNote.isDeleted()) {
                        if (change.newNote.isDeleted()) {
                            idx = activeNoteList.indexOf(change.oldNote);
                            if (idx >= 0) {
                                activeNoteList.set(idx, change.newNote);
                            } else {
                                remoteWrite(change.newNote);
                            }
                        } else {
                            idx = activeNoteList.indexOf(change.oldNote);
                            activeNoteList.set(idx, change.newNote);
                        }
                    } else {
                        if (change.newNote.isDeleted()) {
                            remoteWrite(change.oldNote);
                        } else {
                            NoteEntity merged = merge(change);
                            idx = activeNoteList.indexOf(change.oldNote);
                            activeNoteList.set(idx, merged);
                            remoteWrite(merged);
                        }

                    }

                    break;
                default:
                    throw new UnsupportedOperationException("Unknow Changetype" + change.changeType);
            }
        }

    }

    public abstract boolean remoteWrite(NoteEntity newNote);

    private NoteEntity merge(ChangeSet change) {

        NoteEntity e = change.newNote.cloneElement();

        // merge title:
        e.setTitle("Konflikt: " + e.getTitle() + "/" + change.oldNote.getTitle());
        StringBuilder b = new StringBuilder(e.getBody());
        b.append("<HR/><BR/>Konflikt vom " + change.oldNote.getLastSavedOn());
        b.append("<BR/><HR/>");
        b.append(change.oldNote.getBody());
        e.setBody(b.toString());
        return e;
    }

}
