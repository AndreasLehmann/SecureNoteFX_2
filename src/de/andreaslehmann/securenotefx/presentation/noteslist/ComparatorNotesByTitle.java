package de.andreaslehmann.securenotefx.presentation.noteslist;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author Andreas
 */
class ComparatorNotesByTitle implements Comparator<NoteEntity> {

    static Collator collator;

    public ComparatorNotesByTitle() {
        collator = Collator.getInstance(Locale.GERMAN);
        collator.setStrength(Collator.SECONDARY);// a == A, a < Ä
        
    }

    @Override
    public int compare(NoteEntity n1, NoteEntity n2) {
        return collator.compare(n1.getTitle(),n2.getTitle());
    }

}
