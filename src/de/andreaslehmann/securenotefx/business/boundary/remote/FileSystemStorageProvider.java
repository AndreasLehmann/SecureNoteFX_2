/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.andreaslehmann.securenotefx.business.boundary.remote;

import javax.swing.Icon;

/**
 * Klasse zur Syncronisierung der Notizen auf eine beliebige
 * Stelle im Filesystem. Das könnte auch ein Netzwerkpfad (UNC) sein.
 * 
 * Die Klasse stellt einer Implementierung der "KonkretenStategieA" im Sinne des
 * GoF "Strategie-Musters" dar.
 * 
 * @author Andreas
 */
public class FileSystemStorageProvider implements StorageProvider {

    @Override
    public String getProviderName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Icon getProviderIcon() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
