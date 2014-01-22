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
    
    public static String getValue(String key, String def) {
        return p.get(key, def);
    }

    public static void setValue(String key, String value) {
        p.put(key, value);
    }
}
