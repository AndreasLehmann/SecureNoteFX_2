package de.andreaslehmann.securenotefx.business.boundary.remote;

import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.Icon;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andreas
 */
public class FileSystemStorageProviderIntegrationTest {

    FileSystemStorageProvider p = null;
    static Path tempDirectory = null;

    public FileSystemStorageProviderIntegrationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("notefx-testdir");
    }

    @After
    public void tearDown() {
        File f = new File(tempDirectory.toString());
        f.delete();
    }

    //#######################################################################//
    /**
     * Test of getProviderName method, of class FileSystemStorageProvider.
     */
    @Test
    public void testGetProviderName() {
        System.out.println("getProviderName");
        FileSystemStorageProvider instance = new FileSystemStorageProvider();
        String expResult = FileSystemStorageProvider.PROVIDER_NAME;
        String result = instance.getProviderName();
        assertEquals(expResult, result);
    }

    /**
     * This is not a test - it's for demo purpose only.
     */
    @Test
    public void testList() {
        System.out.println("list");
        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());
        // Aktivieren        
        List<NoteEntity> result = p.list();
        assertNotNull(result);
    }

    /**
     * Test of remoteWrite method, of class FileSystemStorageProvider.
     */
    @Test
    public void testRemoteWrite() {
        System.out.println("remoteWrite");
        // Vorbereiten
        NoteEntity note = new NoteEntity("T", "B");
        FileSystemStorageProvider p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());
        boolean expResult = true;
        // Aktivieren  
        boolean result = p.remoteWrite(note);
        // Prüfen
        assertEquals(expResult, result);

    }

    /**
     * Test of getProviderIcon method, of class FileSystemStorageProvider.
     */
    @Test
    public void testGetProviderIcon() {
        System.out.println("getProviderIcon");
        FileSystemStorageProvider instance = new FileSystemStorageProvider();
        Icon expResult = null;
        Icon result = instance.getProviderIcon();
        assertEquals(expResult, result);

    }

    /**
     * Test of init method, of class FileSystemStorageProvider. Fall 1:
     * Verzeichnis existiert nicht
     */
    @Test
    public void testInit_noDir() {
        System.out.println("init_noDir");
        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory("c:/ABC/");
        // Aktivieren
        boolean result = p.init();
        // Prüfen
        assertFalse(result);
    }

    /**
     * Test of init method, of class FileSystemStorageProvider. Fall 2:
     * Verzeichnis exisitiert, aber leer
     */
    @Test
    public void testInit_dirExisitsEmpty() {
        System.out.println("init_dirExisitsEmpty");
        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());

        // Aktivieren
        boolean result = p.init();
        // Prüfen
        assertFalse(result);
    }

    /**
     * Test of init method, of class FileSystemStorageProvider. Fall 3:
     * Verzeichnis exisitiert, config File ist vorhanden.
     */
    @Test
    public void testInit_dirExisitsOK() throws IOException {
        System.out.println("init_dirExisitsOK");
        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());
        // - Leeres Config file erzeugen.
        File f = new File(tempDirectory.toString() + File.separator + FileSystemStorageProvider.configFilename);
        f.createNewFile();
        // Aktivieren
        boolean result = p.init();
        // Prüfen
        assertTrue(result);
    }

//***************************************************************************
// Fehlerfälle
//
//create(force=false)
//1. Verzeichnis ist nicht vorhanden - anlegen, configFile erzeugen
//2. Verzeichnis schon vorhanden und leer, configFile erzeugen
//3. Verzeichnis schon vorhanden und nicht leer, Fehler
//
//create(force=true)
//5. Verzeichnis schon vorhanden und nicht leer (kein ConfigFile), configFile erzeugen
//6. Verzeichnis schon vorhanden und nicht leer (mit ConfigFile), configFile überschreiben
//
//***************************************************************************    
    @Test
    public void testCreate_1() throws IOException {
        System.out.println("create_1");
        // Vorbereiten
        final String non_exisitent_path = "c://mytest12345//";
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(non_exisitent_path);
        boolean force = false;
        boolean expResult = true;
        // Aktivieren
        boolean result = p.create(force);
        // Prüfen
        assertEquals(expResult, result);

        // Aufraümen
        deleteDir(new File(non_exisitent_path));
    }

    @Test
    public void testCreate_2() throws IOException {
        System.out.println("create_2");
        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());
        boolean force = false;
        boolean expResult = true;
        // Aktivieren
        boolean result = p.create(force);
        // Prüfen
        assertEquals(expResult, result);

    }

    @Test
    public void testCreate_3() throws IOException {
        System.out.println("create_3");
        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());
        // Leeres Config file erzeugen.
        File f = new File(tempDirectory.toString() + File.separator + FileSystemStorageProvider.configFilename);
        f.createNewFile();
        boolean force = false;
        boolean expResult = false;
        // Aktivieren
        boolean result = p.create(force);
        // Prüfen
        assertEquals(expResult, result);
    }

    @Test
    public void testCreate_5() throws IOException {
        System.out.println("create_5");
        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());

        // Leeres File (kein Config file) erzeugen.
        File f = new File(tempDirectory.toString() + File.separator + "Trllala.doc");
        f.createNewFile();

        boolean force = true;
        boolean expResult = true;
        // Aktivieren
        boolean result = p.create(force);
        // Prüfen
        assertEquals(expResult, result);
    }

    @Test
    public void testCreate_6() throws IOException {
        System.out.println("create_6");
        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());
        // Leeres Config file erzeugen.
        File f = new File(tempDirectory.toString() + File.separator + FileSystemStorageProvider.configFilename);
        f.createNewFile();
        boolean force = true;
        boolean expResult = true;
        // Aktivieren
        boolean result = p.create(force);
        // Prüfen
        assertEquals(expResult, result);
    }

    /**
     * Test of getConfigFile method, of class FileSystemStorageProvider.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testGetConfigFile_ifExisits() throws IOException {
        System.out.println("getConfigFile_ifExisits");

        // Vorbereiten
        p = new FileSystemStorageProvider();
        p.setBaseDirectory(tempDirectory.toString());
        // Leeres Config file erzeugen.
        File f = new File(tempDirectory.toString() + File.separator + FileSystemStorageProvider.configFilename);
        f.createNewFile();
        // Aktivieren
        File result = p.getConfigFile(false);
        // Prüfe Ergebnis
        assertEquals(f.getAbsoluteFile(), result.getAbsoluteFile());
    }

    /**
     * rekursives löschen von Verzeichnissen
     *
     * @param f das Verzeichnis
     */
    static void deleteDir(File f) {
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {
                deleteDir(file);
            }
            file.delete();
        }
        f.delete();
    }

}
