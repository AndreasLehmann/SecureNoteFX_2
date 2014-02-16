/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary.remote;

import de.andreaslehmann.securenotefx.business.entity.ChangeSet;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.util.List;
import java.util.UUID;
import javax.swing.Icon;

/**
 * Gemeinsames Interface aller StorageProvider.
 *
 * Das Interface stellt die "Stategie" im Sinne des GoF "Strategie-Musters" dar.
 *
 * @author Andreas
 */
public interface StorageProvider {

    /**
     * Gibt einen menschenlesbaren String zurück, mit dem der Benutzer sich
     * zwischen den verschiendenen Providern entscheiden kann.
     *
     * @return name des Providers (z.B. Dropbox, OwnCloud, usw.)
     */
    String getProviderName();

    /**
     * Gibt ein Icon zurück, das dem Benutzer in der Auswahl der Provider
     * angezeigt wird. z.B. DropBox Icon.
     *
     * @return Provider Icon
     */
    Icon getProviderIcon();

    /**
     * Prüft, ob der Speicherort verfügbar ist und es sich um ein gültigesn
     * Speicherort handelt.
     *
     * @return false im Fehlerfall
     */
    boolean init();

    /**
     * Richtet den Speicherplatz zur ersten Verwendung ein. Falls der
     * Speicherplatz bereits mit Daten belegt ist oder nicht gefunden werden
     * kann, gibt die Methode 'false' zurück. Über den Parameter 'force' kann
     * die Erzeugung erzwungen werden.
     *
     * @param force erzwingt das Erzeugen
     * @return true, wenn alles geklappt hat
     */
    boolean create(boolean force);

    /**
     * Gibt eine Liste der gespeicherten Notizen zurück.
     *
     * @return Notizliste
     */
    List<NoteEntity> list();

    /**
     * Schreibt eine Änderung zurück. Änderungen können auch neue Notizen sein.
     *
     * @param note die geänderte oder neue Notiz
     * @return true, wenn die Operation geklappt hat
     */
    boolean remoteWrite(NoteEntity note);

    /**
     * Liest eine Notiz.
     *
     * @param id der eindeutige Identifier der Notiz
     * @return die Notiz oder null, wenn diese nicht gefunden wurde
     */
    NoteEntity remoteRead(UUID id);
    
    /**
     * Gleicht die lokaleListe mit den remote Notizen ab. Dabei wird 
     * auch die lokale Lister verändert!
     * 
     * @param localNotes Liste gegen die vergleichen wird.
     * @return die Liste der Änderungen, die durchgeführt wurden
     */
    List<ChangeSet> syncronize (List<NoteEntity> localNotes);
}
