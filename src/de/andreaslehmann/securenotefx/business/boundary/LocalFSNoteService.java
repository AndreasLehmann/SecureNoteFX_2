/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary;

import de.andreaslehmann.securenotefx.business.boundary.remote.AbstractProvider;
import de.andreaslehmann.securenotefx.business.boundary.remote.FileSystemStorageProvider;
import de.andreaslehmann.securenotefx.business.boundary.remote.StorageContext;
import de.andreaslehmann.securenotefx.business.boundary.remote.StorageProvider;
import de.andreaslehmann.securenotefx.business.entity.ChangeSet;
import de.andreaslehmann.securenotefx.utility.JsonNoteSerializer;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import de.andreaslehmann.securenotefx.utility.PrefStore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse speichert Notizen auf der lokalen Festplatte ab. Es findet keine
 * synchronisation statt, konkurrierende Zugriff werden nicht berücksichtigt. Es
 * gibt kein caching.
 *
 * TODO: Was ist, wenn zwei Instanzen der Software gestartet werden?
 *
 * @author Andreas
 *
 */
public class LocalFSNoteService {

    @Inject
    private JsonNoteSerializer jsonService;

    /**
     * Hält die Konfiguration zum Remote Storage Provider
     */
    private StorageContext storageCtx = null;

    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
    public String basePath = null;
    private final JSONNameHelper jsonFilenameFilter = new JSONNameHelper();

    public LocalFSNoteService() {
        super();
        this.basePath = PrefStore.instance().get(PrefStore.LOCAL_BASE_PATH, null);
        if (this.basePath == null) {
            log.error("No basepath set !");
        }
    }
    public LocalFSNoteService(String basepath) {
        super();
        this.basePath = basepath;
        if (this.basePath == null) {
            log.error("No basepath set !");
        }
    }

    /**
     * Prüfe ob der BasePath gesetzt ist und erzeug das Verzeichnis, falls nicht
     * bereits vorhanden.
     *
     * @return true, wenn alles bereit ist.
     */
    public boolean init() {
        if (this.basePath == null) {
            log.error("No basepath set !");
            return false;
        }
        File f = new File(this.basePath);
        if (!f.exists()) {
            f.mkdirs();
        }

        this.setupRemoteStorageProvider();

        return true;
    }

    public List<NoteEntity> list() {
        List<NoteEntity> list = new ArrayList<>();

        List<String> directoryListing = readDir();
        for (String path : directoryListing) {
            NoteEntity n = readNoteEntity(path);
            if (n != null) {
                list.add(n);
            }
        }
        return list;
    }

    private List<String> readDir() {
        File dir = new File(basePath);
        File[] files = dir.listFiles(jsonFilenameFilter);
        ArrayList<String> filenames = new ArrayList<>();
        if (files == null) {
            return filenames;
        }

        for (File f : files) {
            if (f.isFile()) {
                filenames.add(f.getAbsolutePath());
            }
        }
        return filenames;
    }

    public NoteEntity readNoteEntity(UUID id) {
        return readNoteEntity(JSONNameHelper.buildFilename(basePath, id));
    }

    private NoteEntity readNoteEntity(String filepath) {

        BufferedReader br = null;
        StringBuilder sb;
        sb = new StringBuilder();
        String line;

        try {
            br = new BufferedReader(new FileReader(filepath));

            line = br.readLine();
            while (line != null) {
                sb.append(line);
                //sb.append("\n"); // Zeilenumbrüche sind nicht nötig.
                line = br.readLine();
            }

        } catch (IOException ex) {
            log.error("unable to read " + filepath, ex);
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                // nix tun...
            }
        }

        NoteEntity n;

        if (log.isDebugEnabled()) {
            log.debug("read json string=" + sb.toString());
        }

        String x = sb.toString();

        n = jsonService.deserialize(sb.toString());
        n.setLastSavedOn(n.getLastSavedOn()); // set dirty Flag == false!

        return n;
    }

    public boolean writeNoteEntity(NoteEntity n) {

        if (!n.isDirty()) {
            return true;
        }

        // update timestamp
        long previous = n.getLastSavedOn();
        n.setLastSavedOn(System.currentTimeMillis());

        try {
            this.rawWriteNoteEntity(n);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LocalFSNoteService.class.getName()).log(Level.SEVERE, null, ex);
            n.setLastSavedOn(previous);//restore last timestamp
            return false;
        } catch (IOException ex) {
            Logger.getLogger(LocalFSNoteService.class.getName()).log(Level.SEVERE, null, ex);
            n.setLastSavedOn(previous);//restore last timestamp
            return false;
        }
        return true;
    }

    /**
     * Das reine schreiben auf DISK ohne Sicherheitsnetz und Businesslogik.
     * Speziell der LastSavedOn - Timestamp wird nicht angefasst.
     *
     * @param newNote
     */
    private void rawWriteNoteEntity(NoteEntity n) throws FileNotFoundException, IOException {
        String filepath = JSONNameHelper.buildFilename(basePath, n.getUniqueKey());

        if (log.isDebugEnabled()) {
            log.debug("write: filepath=" + filepath);
        }

        String json = jsonService.serialize(n);

        File f = new File(filepath);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(f);
            fos.write(json.getBytes());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LocalFSNoteService.class.getName()).log(Level.SEVERE, null, ex);
                // hier kann man nichts mehr machen...
            }
        }
    }

    public void delete(NoteEntity n) {
        n.delete();
        this.writeNoteEntity(n);
    }

    public void undelete(NoteEntity n) {
        n.undelete();
        this.writeNoteEntity(n);
    }

    private void setupRemoteStorageProvider() {
        storageCtx = new StorageContext();
        storageCtx.setProvider(new FileSystemStorageProvider());

    }

    /**
     * Die Methode synchronisiert die Notizen mit dem RemoteStorageProvider.
     * ACHTUNG: Die übergebene Liste wird auch verändert!
     *
     * @param localNotes die Notizenliste, die derzeit im Baum dargestellt wird
     */
    public void remoteSyncNote(List<NoteEntity> localNotes) {

        StorageProvider p = this.storageCtx.getProvider();
        List<ChangeSet> changes = p.syncronize(localNotes);

        for (ChangeSet c : changes) {
            try {
                this.rawWriteNoteEntity(c.newNote);
                c.newNote.setLastSavedOn(c.newNote.getLastSavedOn()); // reset Dirty-Flag
            } catch (IOException ex) {
                log.error("Can't write back changes after remote sync.", ex);
            }
        }

    }

    /**
     * Fallback falls die @Inject methode versagt (z.B. beim Junittest)
     *
     * @param jsonService
     */
    protected void setSerializer(JsonNoteSerializer jsonService) {
        this.jsonService = jsonService;
    }

}
