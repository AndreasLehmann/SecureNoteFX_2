/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteeditor;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Andreas
 */
class HTMLEditorToolbarWrapper {

    static void wrapButtons(ToolBar bar, NoteEditorPresenter ctrl) {

        // sichere den alten EventHanlder
        final EventHandler<? super MouseEvent> oldOnMouseClicked = bar.getOnMouseClicked();

        MyButtonHandler myButtonHandler = new MyButtonHandler(bar.getOnMouseClicked(), ctrl);

        ObservableList<Node> items = bar.getItems();
        for (Node i : items) {
            //System.out.println("attach->" + i.getClass().getName());
            i.setOnMouseClicked(myButtonHandler);
        }
    }

    private static class MyButtonHandler implements EventHandler<MouseEvent> {
        private final EventHandler<? super MouseEvent> oldHandler;
        private final NoteEditorPresenter ctrl;

        public MyButtonHandler(EventHandler<? super MouseEvent> oldHandler, NoteEditorPresenter ctrl) {
            this.oldHandler = oldHandler;
            this.ctrl = ctrl;
        }

        @Override
        public void handle(MouseEvent t) {
            //System.out.println("Proxy detected HTMLEditor click");
            ctrl.bodyEditor_ToolbarClicked();
            // call original handler
            if (oldHandler != null) {
                oldHandler.handle(t);
            }
            t.consume();
        }

    }

}
