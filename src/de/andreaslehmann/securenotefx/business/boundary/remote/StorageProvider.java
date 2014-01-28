/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.andreaslehmann.securenotefx.business.boundary.remote;

import javax.swing.Icon;

/**
 * Gemeinsames Interface aller StorageProvider.
 * 
 * Das Interface stellt die "Stategie" im Sinne des
 * GoF "Strategie-Musters" dar.
 * 
 * @author Andreas
 */
public interface StorageProvider {
    
    /**
     * Gibt einen menschenlesbaren String zurück,
     * mit dem der Benutzer sich zwischen den verschiendenen
     * Providern entscheiden kann.
     * 
     * @return name des Providers (z.B. Dropbox, OwnCloud, usw.)
     */
    String getProviderName();
    
    /**
     * Gibt ein Icon zurück, das dem Benutzer in der Auswahl der Provider
     * angezeigt wird. z.B. DropBox Icon.
     * 
     * @return  Provider Icon
     */
    Icon getProviderIcon();
    
    
}
