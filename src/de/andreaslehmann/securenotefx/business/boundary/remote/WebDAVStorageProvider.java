/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.business.boundary.remote;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import de.andreaslehmann.securenotefx.business.entity.ChangeSet;
import de.andreaslehmann.securenotefx.business.entity.NoteEntity;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.swing.Icon;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andreas
 */
public class WebDAVStorageProvider implements StorageProvider {

    // Name des Providers für den Benutzer
    protected static final String PROVIDER_NAME = "WebDAV Laufwerk";
    // Icon des Providers TODO: provide Icon
    protected static final Icon icon = null;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean create(boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<NoteEntity> list() {
        String uri = "https://andreaslehmann.homeip.net/owncloud/files/webdav.php/Ordner";
        String username = "testusr";
        String passwd = "test1700";
        List<DavResource> resources = null;

        Sardine sardine = SardineFactory.begin(username, passwd);
        try {
            resources = sardine.list(uri);
        } catch (IOException ex) {
            log.error("Can't open uri " + uri, ex);
        }
        if (resources == null) {
            return null;
        }

        for (DavResource res : resources) {
            System.out.println(res); // calls the .toString() method.
            System.out.println(res.getName());
        }
        return null;
    }

    @Override
    public boolean remoteWrite(NoteEntity note) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public NoteEntity remoteRead(UUID id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ChangeSet> syncronize(List<NoteEntity> localNotes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
