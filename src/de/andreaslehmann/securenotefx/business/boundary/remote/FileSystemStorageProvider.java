/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary.remote;

import de.andreaslehmann.securenotefx.business.boundary.JSONNameHelper;
import de.andreaslehmann.securenotefx.business.entity.ChangeSet;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import de.andreaslehmann.securenotefx.utility.JsonNoteSerializer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
public class FileSystemStorageProvider extends AbstractProvider implements StorageProvider {

    //TODO: Dieser Parameter muss dynamisch einstellbar sein.
    protected String baseDirectory = "c://Temp//remote-repo//";
    // Name des Providers für den Benutzer
    protected static final String PROVIDER_NAME = "Dateisystem oder Netzwerklaufwerk";
    // Icon des Providers TODO: provide Icon
    protected static final Icon icon = null;
    // Name der Konfigurationsdatei
    protected static final String configFilename = "config";
    // Logger
    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    // Helper class to work  with filenames and filters
    private final JSONNameHelper jsonFilenameFilter = new JSONNameHelper();

    private final JsonNoteSerializer jsonService = new JsonNoteSerializer();

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
        List<NoteEntity> list = new ArrayList<>();
        List<String> directoryListing = readDir();
        for (String path : directoryListing) {
            NoteEntity n = remoteReadByPath(path);
            if (n != null) {
                list.add(n);
            }
        }
        return list;
    }

    @Override
    public boolean remoteWrite(NoteEntity note) {
        return this.remoteWriteToPath(note, JSONNameHelper.buildFilename(baseDirectory, note.getUniqueKey()));
    }

    @Override
    public NoteEntity remoteRead(UUID id) {
        return this.remoteReadByPath( JSONNameHelper.buildFilename(baseDirectory, id));
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

    //######################################################################//
    private List<String> readDir() {
        File dir = new File(baseDirectory);
        File[] files = dir.listFiles(jsonFilenameFilter);
        ArrayList<String> filenames = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                filenames.add(f.getAbsolutePath());
            }
        }
        return filenames;
    }

    //######################################################################//
    private NoteEntity remoteReadByPath(String path) {
        BufferedReader br = null;
        StringBuilder sb;
        sb = new StringBuilder();
        String line;

        // read line by line
        try {
            br = new BufferedReader(new FileReader(path));
            line = br.readLine();
            while (line != null) {
                sb.append(line); // speichern
                line = br.readLine(); // nächste zeile
            }

        } catch (IOException ex) {
            log.error("unable to read " + path, ex);
            return null;
        } finally { // tryp to close reader
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                // nix tun...
            }
        }

        // convert json string to entity
        NoteEntity n;
        if (log.isDebugEnabled()) {
            log.debug("read json string=" + sb.toString());
        }
        n = jsonService.deserialize(sb.toString());
        return n;
    }

    //######################################################################//
    private boolean remoteWriteToPath(NoteEntity n, String filepath) {

        if (log.isDebugEnabled()) {
            log.debug("remote write: filepath=" + filepath);
        }
        
        // update timestamp
        long now=System.currentTimeMillis();
        long lastSavedTS_bak = n.getLastSavedOn();
        long lastRemoteTS_bak = n.getRemoteTimestamp();
        n.setLastSavedOn(now);
        n.setRemoteTimestamp(now);
        n.setSyncronized();
        
        String json = jsonService.serialize(n);

        File f = new File(filepath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(json.getBytes());
        } catch (FileNotFoundException ex) {
            log.error("remote write error ", ex);
            n.setLastSavedOn(lastSavedTS_bak);//restore last timestamp
            n.setRemoteTimestamp(lastRemoteTS_bak);
            return false;
        } catch (IOException ex) {
            log.error("remote write error ", ex);
            n.setLastSavedOn(lastSavedTS_bak);//restore last timestamp
            n.setRemoteTimestamp(lastRemoteTS_bak);
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                log.debug("remote write error ", ex);
                // hier kann man nichts mehr machen...
            }
        }
        return true;
    }

    @Override
    public List<ChangeSet> syncronize(List<NoteEntity> localNotes) {
        List<NoteEntity> remoteNotes = this.list();
        List<ChangeSet> changes = super.compareRemote(remoteNotes, localNotes);

        // keine Änderungen? dann austeigen
        if (changes == null || changes.size() < 1) {
            return  new ArrayList<ChangeSet>();
        }
        
        // Listen zusammenführen
        // TODO: Was passiert, wenn eine Notiz remote verändert wurde,
        // die gerade vom Anwender bearbeitet wird?
        super.applyChanges(changes, localNotes); 
        
        return changes;
        
    }

}
