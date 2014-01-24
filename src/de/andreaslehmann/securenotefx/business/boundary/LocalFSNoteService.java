/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary;

import de.andreaslehmann.securenotefx.business.controller.JsonNoteSerializer;
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
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
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
public class LocalFSNoteService extends AbstractNoteService implements NoteService {

    @Inject
    private JsonNoteSerializer jsonService;

    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
    public String basePath = null;
    private final JSONNameHelper jsonFilenameFilter = new JSONNameHelper();

    ListProperty<NoteEntity> noteListProperty = new SimpleListProperty<>(javafx.collections.FXCollections.observableList(new ArrayList<NoteEntity>()));

    public LocalFSNoteService() {
        super();
        this.basePath = PrefStore.instance().get(PrefStore.LOCAL_BASE_PATH, null);
        if (this.basePath == null) {
            log.error("No basepath set !");
        }
    }

    @Override
    public ReadOnlyListProperty<NoteEntity> list() {
        this.noteListProperty.clear();

        List<String> directoryListing = readDir();
        for (String path : directoryListing) {
            NoteEntity n = readNoteEntity(path);
            if (n != null) {
                this.noteListProperty.add(n);
            }

        }
        return this.noteListProperty;
    }

    private List<String> readDir() {
        File dir = new File(basePath);
        File[] files = dir.listFiles(jsonFilenameFilter);
        ArrayList<String> filenames = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                filenames.add(f.getAbsolutePath());
            }
        }
        return filenames;
    }

    @Override
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

        n = jsonService.deserialize(sb.toString());

        return n;
    }

    @Override
    public boolean writeNoteEntity(NoteEntity n) {
        return writeNoteEntity(n, JSONNameHelper.buildFilename(basePath, n.getUniqueKey()));
    }

    private boolean writeNoteEntity(NoteEntity n, String filepath) {

        if (log.isDebugEnabled()) {
            log.debug("write: filepath=" + filepath);
        }

        // update timestamp
        long previous = n.getLastSavedOn();
        n.setLastSavedOn(System.currentTimeMillis());

        String json = jsonService.serialize(n);

        File f = new File(filepath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(json.getBytes());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LocalFSNoteService.class.getName()).log(Level.SEVERE, null, ex);
            n.setLastSavedOn(previous);//restore last timestamp
            return false;
        } catch (IOException ex) {
            Logger.getLogger(LocalFSNoteService.class.getName()).log(Level.SEVERE, null, ex);
            n.setLastSavedOn(previous);//restore last timestamp
            return false;
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

        return true;
    }

    @Override
    public void delete(NoteEntity e) {
        e.delete();
        this.writeNoteEntity(e);
    }

    /**
     * Speichere alle veränderten Notizen auf Disk.
     */
    public void persistAll() {
        for (NoteEntity note : noteListProperty) {
            if(note.isDirty()){
                writeNoteEntity(note);
            }
        }
                
    }

}
