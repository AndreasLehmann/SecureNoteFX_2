/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.andreaslehmann.securenotefx.presentation.noteslist;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Eine Wrapper Klasse um ein Kontextmenü für eine ListCell anzuhängen
 */
public class ContextMenuListCell {

    public static <T> Callback<ListView<T>, ListCell<T>> forListView(final ContextMenu contextMenu, final Callback<ListView<T>, ListCell<T>> cellFactory) {
        return new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> listView) {
                ListCell<T> cell = cellFactory.call(listView);
                cell.setContextMenu(contextMenu);
                return cell;
            }
        };
    }
}
