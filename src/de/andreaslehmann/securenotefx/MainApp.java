package de.andreaslehmann.securenotefx;

import com.airhacks.afterburner.injection.InjectionProvider;
import de.andreaslehmann.securenotefx.presentation.securenotefx.SecureNoteFXPresenter;
import de.andreaslehmann.securenotefx.presentation.securenotefx.SecureNoteFXView;
import de.andreaslehmann.securenotefx.utility.PrefStore;
import de.andreaslehmann.securenotefx.utility.StageManager;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void start(final Stage stage) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("BasePath:" + PrefStore.instance().get(PrefStore.LOCAL_BASE_PATH, null));

            // Für Startup-Zwecke
            PrefStore.instance().put(PrefStore.LOCAL_BASE_PATH, "f:/tmp/MySecretNoteStorage_A/");
        }

        StageManager.getInstance().setPrimaryStage(stage);

        final SecureNoteFXView appView = new SecureNoteFXView();
        Scene scene = new Scene(appView.getView());
        stage.setTitle("SecureNote");
        final String uri = getClass().getResource("app.css").toExternalForm();
        log.debug(uri);
        scene.getStylesheets().add(uri);
        stage.setScene(scene);

        // Fensterposition, -höhe und -breite wiederherstellen
        restoreWindowPosition();

        final SecureNoteFXPresenter p = (SecureNoteFXPresenter) appView.getPresenter();

        // Fensterposition, -höhe und -breite sichern, wenn sie verändert wird.
        stage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
                    public void handle(final WindowEvent event) {
                        if (!p.shutdown()) {
                            event.consume();
                        } else {
                            persistWindowPosition();
                        }
                    }
                });

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        InjectionProvider.forgetAll();
    }

    private void persistWindowPosition() {
        Stage stage = StageManager.getInstance().getPrimaryStage();
        // Fensterposition, -höhe und -breite sichern
        PrefStore.instance().putDouble(PrefStore.WIN_X, stage.getX());
        PrefStore.instance().putDouble(PrefStore.WIN_Y, stage.getY());
        PrefStore.instance().putDouble(PrefStore.WIN_WIDTH, stage.getWidth());
        PrefStore.instance().putDouble(PrefStore.WIN_HEIGHT, stage.getHeight());
    }

    private void restoreWindowPosition() {
        Stage stage = StageManager.getInstance().getPrimaryStage();
        // Fensterposition, -höhe und -breite wiederherstellen
        stage.setWidth(PrefStore.instance().getDouble(PrefStore.WIN_WIDTH, 1024.0));
        stage.setHeight(PrefStore.instance().getDouble(PrefStore.WIN_HEIGHT, 600.0));
        stage.setX(PrefStore.instance().getDouble(PrefStore.WIN_X, 250.0));
        stage.setY(PrefStore.instance().getDouble(PrefStore.WIN_Y, 250.0));

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
