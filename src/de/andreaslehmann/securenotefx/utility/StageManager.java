package de.andreaslehmann.securenotefx.utility;

import javafx.stage.Stage;

public class StageManager {

    private static StageManager instance;
    private static Stage primaryStage;

    /**
     * Konstruktor ist privat, Klasse darf nicht von außen instanziiert werden.
     */
    private StageManager() {
    }

    /**
     * Statische Methode 'getInstance()Ä liefert die einzige Instanz der Klasse
     * zurück. Ist synchronisiert und somit thread-sicher.
     */
    public synchronized static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        StageManager.primaryStage = primaryStage;
    }
}
