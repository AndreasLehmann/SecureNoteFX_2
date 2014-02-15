 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary;

import de.andreaslehmann.securenotefx.business.boundary.remote.AbstractProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.andreaslehmann.securenotefx.business.entity.ChangeSet;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author AndrremoteNoteas
 * @deprecated just for reference
 *
 */
public class FilebasedNoteService extends AbstractProvider {

    protected static final String FILE_SUFFIX = ".json";
    public final String basePath;
    private final JSONNameFilter jsonFilenameFilter = new JSONNameFilter();

    ListProperty<NoteEntity> noteList = new SimpleListProperty<>(javafx.collections.FXCollections.observableList(new ArrayList<NoteEntity>()));

    private final Gson gson;

    public FilebasedNoteService(String basepath) {
        this.basePath = basepath;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyTypeAdapter());
        gsonBuilder.registerTypeAdapter(SimpleBooleanProperty.class, new SimpleBooleanPropertyTypeAdapter());
        gson = gsonBuilder.create();

    }

    public ListProperty<NoteEntity> list() {
        if (noteList == null || noteList.size() < 1) {
            this.synchronize();
        }

        return noteList;
    }

    private List<String> readDir() {
        File dir = new File(basePath);
        File[] files = dir.listFiles(jsonFilenameFilter);
        ArrayList<String> filenames = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                filenames.add(f.getName());
            }
        }
        return filenames;
    }

    public NoteEntity readNoteEntity(UUID id) {
        return readNoteEntity(buildFilename(id));
    }

    private NoteEntity readNoteEntity(String filepath) {

        Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.FINER, "read: filepath=" + filepath);

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
            Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.SEVERE, null, ex);
                // nix tun...
            }
        }

        NoteEntity n;
        Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.FINEST, "read json string=" + sb.toString());
        n = (NoteEntity) gson.fromJson(sb.toString(), NoteEntity.class);

        return n;
    }

    @Override
    public boolean remoteWrite(NoteEntity n) {
        return writeNoteEntity(n, buildFilename(n.getUniqueKey()));
    }

    protected String buildFilename(UUID id) {
        return basePath + "/" + id + FILE_SUFFIX;
    }

    private boolean writeNoteEntity(NoteEntity n, String filepath) {
        Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.FINER, "write: filepath=" + filepath);

        // update timestamp
        long previous = n.getLastSavedOn();
        n.setLastSavedOn(System.currentTimeMillis());

        String json = gson.toJson(n);
        File f = new File(filepath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(json.getBytes());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.SEVERE, null, ex);
            n.setLastSavedOn(previous);//restore last timestamp
            return false;
        } catch (IOException ex) {
            Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.SEVERE, null, ex);
            n.setLastSavedOn(previous);//restore last timestamp
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.SEVERE, null, ex);
                // hier kann man nichts mehr machen...
            }
        }

        return true;
    }

    //@Override
    public int synchronize() {
        final List<ChangeSet> changes = getChanges();
        applyChanges(changes, this.noteList);
        return changes.size();
    }

    public void delete(NoteEntity e) {
        e.delete();
        this.remoteWrite(e);

    }

    private static class SimpleStringPropertyTypeAdapter implements JsonSerializer<SimpleStringProperty>, JsonDeserializer<SimpleStringProperty> {

        @Override
        public JsonElement serialize(SimpleStringProperty t, Type type, JsonSerializationContext jsc) {
            return new JsonPrimitive(t.getValue());
        }

        @Override
        public SimpleStringProperty deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            return new SimpleStringProperty(je.getAsJsonPrimitive().getAsString());
        }
    }

    private static class SimpleBooleanPropertyTypeAdapter implements JsonSerializer<SimpleBooleanProperty>, JsonDeserializer<SimpleBooleanProperty> {

        @Override
        public JsonElement serialize(SimpleBooleanProperty t, Type type, JsonSerializationContext jsc) {
            return new JsonPrimitive(t.getValue());
        }

        @Override
        public SimpleBooleanProperty deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            return new SimpleBooleanProperty(je.getAsJsonPrimitive().getAsBoolean());
        }
    }

    private static class JSONNameFilter implements FilenameFilter {

        @Override
        public boolean accept(File file, String string) {
            return string.endsWith(FILE_SUFFIX);
        }
    }

    /**
     * Gibt das Datum der letzten Aktualisierung zurück. Um sicher zu sein, dass
     * es immer passt, wird der Timestamp der neuesten Datei zurückgegeben.
     *
     * @return lastSync Timestamp
     */
    public long getLastSync() {
        long latest = 0L;
        for (NoteEntity n : noteList) {

            Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.SEVERE, "lastSync: " + n.getLastSavedOn());

            if (n.getLastSavedOn() > latest) {
                latest = n.getLastSavedOn();
            }
        }
        Logger.getLogger(FilebasedNoteService.class.getName()).log(Level.SEVERE, "latest: " + latest);
        return latest;
    }

    /**
     * Gibt alle Änderungen zwischen den beiden Listes als ChangeSet zuürck.
     *
     * Möglich Ändrerungen: ADD, DELETE, UPDATE, usw. (siehe CHANGE_TYPE)
     *
     * @return Liste der Änderungen
     */
    public List<ChangeSet> getChanges() {
        // Erzeuge die Liste der Remote-Elemente
        List<String> filenameList = readDir();
        List<NoteEntity> remoteList = new ArrayList<>();
        for (Iterator<String> it = filenameList.iterator(); it.hasNext();) {
            NoteEntity remoteNote = readNoteEntity(basePath + "/" + it.next());
            // speichere die geladenen Elemente für später;
            remoteList.add(remoteNote);
        }
        return compare(remoteList, this.noteList.getValue());
    }

}
