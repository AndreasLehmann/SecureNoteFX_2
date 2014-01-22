/*  
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.andreaslehmann.securenotefx.utility;

import java.util.prefs.Preferences;

/**
 *
 * @author Andreas
 */
public class PrefStore {

    private static final String ROOTPATH = "de.andreaslehmann.securenotefx";
    private static final Preferences p = Preferences.userRoot().node(ROOTPATH);
    
    //############################################################
    // Alle Preference Schlüssel sind hier defniniert.
    
    /**
     * Ort, an dem die Notizen lokal abgelegt werden.
     */
    public static String LOCAL_BASE_PATH = "LocalBasePath";
    public static String WIN_WIDTH = "WinWidth";
    public static String WIN_HEIGHT = "WinHeight";
    public static String WIN_X = "WinX";
    public static String WIN_Y = "WinY";
    
    //############################################################
    
    public static Preferences instance() {
        return p;
    }
}
