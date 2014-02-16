/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.entity;

/**
 *
 * @author Andreas
 */
public class ChangeSet {

    public enum ChangeTyp {

        REMOTE_ADDED, LOCAL_ADDED, REMOTE_UPDATED, LOCAL_UPDATED, LOCAL_DELETED, REMOTE_DELETED, BOTH_UPDATED
    };

    public ChangeTyp changeType;
    public NoteEntity oldNote;
    public NoteEntity newNote;

    public ChangeSet(ChangeTyp changeType, NoteEntity oldNote, NoteEntity newNote) {
        this.changeType = changeType;
        this.oldNote = oldNote;
        this.newNote = newNote;
    }

    @Override
    public String toString() {
        return "ChangeSet{" + "changeType=" + changeType + ", oldNote=" + oldNote + ", newNote=" + newNote + '}';
    }

}
