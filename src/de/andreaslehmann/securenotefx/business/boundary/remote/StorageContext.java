package de.andreaslehmann.securenotefx.business.boundary.remote;

/**
 * Diese Klasse hält den konkreten Speicher-Kontext (Storagecontext).
 * Die Klasse implementiert den "Kontext" aus dem GoF "Strategie-Muster".
 * 
 * @author Andreas
 */
public class StorageContext {
   private StorageProvider provider;

    public StorageContext() {
        super();
    }

    public StorageProvider getProvider() {
        return provider;
    }

    public void setProvider(StorageProvider provider) {
        this.provider = provider;
    }
   
}
