package com.dlsc.gemsfx.skins;

import com.dlsc.gemsfx.SearchField;
import com.dlsc.gemsfx.SearchTextField;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;

import java.util.Objects;

public class SearchTextFieldHistoryPopupSkin implements Skin<SearchTextFieldHistoryPopup> {

    private final SearchTextFieldHistoryPopup control;
    private final SearchTextField searchTextField;
    private ListView<String> listView;

    public SearchTextFieldHistoryPopupSkin(SearchTextFieldHistoryPopup control) {
        this.control = control;
        searchTextField = control.getSearchTextField();

        initListView();
    }

    private void initListView() {
        listView = new ListView<>() {
            @Override
            public String getUserAgentStylesheet() {
                return Objects.requireNonNull(SearchField.class.getResource("search-text-field.css")).toExternalForm();
            }
        };
        listView.getStyleClass().add("search-history-list-view");

        Bindings.bindContent(listView.getItems(), searchTextField.getUnmodifiableHistory());

        listView.cellFactoryProperty().bind(searchTextField.cellFactoryProperty());
        listView.placeholderProperty().bind(searchTextField.historyPlaceholderProperty());

        listView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                selectHistoryItem();
            }
        });

        listView.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER -> selectHistoryItem();
                case ESCAPE -> control.hide();
            }
        });
    }

    private void selectHistoryItem() {
        String selectedHistory = listView.getSelectionModel().getSelectedItem();
        if (selectedHistory != null) {
            // replace text
            int oldTextLen = control.getSearchTextField().textProperty().getValueSafe().length();
            searchTextField.replaceText(0, oldTextLen, selectedHistory);

            // hide popup
            control.hide();
        }
    }

    /**
     * Resets the selection of the ListView by focusing on it and selecting the first item, scrolling to the top if necessary.
     */
    public void resetSelection() {
        listView.requestFocus();

        if (!listView.getItems().isEmpty()) {
            listView.getSelectionModel().select(0);
            listView.scrollTo(0);
            listView.getFocusModel().focus(0);
        }

    }

    public Node getNode() {
        return listView;
    }

    public SearchTextFieldHistoryPopup getSkinnable() {
        return control;
    }

    public void dispose() {
        Bindings.unbindContent(listView.getItems(), searchTextField.getUnmodifiableHistory());

        listView.prefWidthProperty().unbind();
        listView.maxWidthProperty().unbind();
        listView.minWidthProperty().unbind();

        listView.cellFactoryProperty().unbind();
        listView.placeholderProperty().unbind();
    }

}
