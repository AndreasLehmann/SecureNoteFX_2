/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary.remote;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.Icon;
import org.slf4j.LoggerFactory;

/**
 * Klasse zur Syncronisierung der Notizen auf eine beliebige Stelle im
 * Filesystem. Das könnte auch ein Netzwerkpfad (UNC) sein.
 *
 * Die Klasse stellt einer Implementierung der "KonkretenStategieA" im Sinne des
 * GoF "Strategie-Musters" dar.
 *
 * @author Andreas
 */
public class FileSystemStorageProvider implements StorageProvider {

    //TODO: Dieser Parameter muss dynamisch einstellbar sein.
    protected String baseDirectory = "c:/tmp/_OOP2014/C_Source-Code/network-repo/";
    // Name des Providers für den Benutzer
    protected static final String PROVIDER_NAME = "Dateisystem oder Netzwerklaufwerk";
    // Icon des Providers TODO: provide Icon
    protected static final Icon icon = null;
    // Name der Konfigurationsdatei
    protected static final String configFilename = "config";
    // Logger
    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public Icon getProviderIcon() {
        return icon;
    }

    @Override
    public boolean init() {
        File f = getConfigFile(false);
        return (f != null && f.canWrite());
    }

    @Override
    public boolean create(boolean force) {
        java.io.File f = new File(baseDirectory);
        // ist es ein Verzeichnis und is es schreibbar?
        if (!f.isDirectory() || !f.canWrite()) {
            if (!f.mkdir()) {
                return false;
            }
            return getConfigFile(true) != null;
        }
        // Prüfe Zugriff und Schreibberechtigung.
        if ((!f.canWrite()) || (!f.isDirectory())) {
            return false;
        }
        // config file vorhanden?
        f = getConfigFile(false);
        if (f != null) { // ja, ist vorhanden
            if (force) {
                // Datei vorher löschen
                if (!f.delete()) {
                    return false; // Fehler, falls nicht löschbar
                }
            } else {
                return false;
            }
        }

        // Erzwinge das Erzeugen
        f = getConfigFile(true);
        if (f == null) {
            return false;
        }
        return true;
    }

    @Override
    public List<NoteEntity> list() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remoteWrite(NoteEntity note) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Gibt das Konfigurationfile des Repositories oder null zurück.
     *
     * @param force erzwingt das Erzeugen einer neuen Datei, falls keine
     * vorhanden ist.
     * @return ConfigFile als File-object oder null
     */
    File getConfigFile(boolean force) {
        File f = new File(baseDirectory + File.separator + configFilename);
        if (!f.canRead()) {
            if (force) {
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    log.error("can't force create configfile (" + f.getAbsoluteFile() + ")", ex);
                    return null;
                }
                return f; // Erzeugung erfolgreich
            }
            return null; // keine Datei, Erzeugung nicht erzwungen
        }
        return f; //Datei ist da.
    }

    protected String getBaseDirectory() {
        return baseDirectory;
    }

    protected void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
}
