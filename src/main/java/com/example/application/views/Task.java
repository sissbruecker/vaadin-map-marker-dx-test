package com.example.application.views;

import com.example.application.Geolocation;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Task")
@Route(value = "task")
@RouteAlias(value = "")
public class Task extends VerticalLayout {
    private final Map map;
    private final MarkerFeature addressMarker;

    public Task() {
        TextField addressField = new TextField("Address");
        addressField.setWidth("400px");

        Button search = new Button("Search", event -> {
            String address = addressField.getValue();
            try {
                Coordinate coordinate = Geolocation.lookupCoordinates(address);
                showAddressMarker(coordinate);
                addressField.setInvalid(false);
            } catch (Exception e) {
                e.printStackTrace();
                addressField.setErrorMessage("Could not find address");
                addressField.setInvalid(true);
            }
        });
        search.addClickShortcut(Key.ENTER);

        HorizontalLayout addressLayout = new HorizontalLayout(addressField, search);
        addressLayout.setAlignItems(Alignment.BASELINE);
        add(addressLayout);

        /*
         * Tasks:
         * 1. Show a marker label
         *   - Add a label with the text "My address" to the marker
         *   - Make the text bold, use a font-size of 16px and use the font-family Arial
         *   - Change the text color to #831843
         *   - Change the text outline to white with a width of 3px
         *   - Position the text above the marker
         * 2. Allow users to modify the address using drag&drop
         *   - Update the address field when the marker is dropped
         *   - Use the `Geolocation` class to look up the address from the coordinates
         */
        map = new Map();
        add(map);

        addressMarker = new MarkerFeature();

        map.getFeatureLayer().addFeature(addressMarker);
    }

    private void showAddressMarker(Coordinate coordinate) {
        addressMarker.setCoordinates(coordinate);
        map.setCenter(coordinate);
        map.setZoom(13);
    }
}
