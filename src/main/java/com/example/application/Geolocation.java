package com.example.application;

import com.vaadin.flow.component.map.configuration.Coordinate;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JreJsonFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class Geolocation {

    public static Coordinate lookupCoordinates(String address) throws IOException, InterruptedException {
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://photon.komoot.io/api/?q=" + encodedAddress)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = new JreJsonFactory().parse(response.body());
        JsonArray jsonFeatures = json.get("features");
        JsonArray jsonCoordinates = jsonFeatures.getObject(0).getObject("geometry").getArray("coordinates");
        double longitude = jsonCoordinates.getNumber(0);
        double latitude = jsonCoordinates.getNumber(1);

        return new Coordinate(longitude, latitude);
    }

    public static String lookupAddress(Coordinate coordinate) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://photon.komoot.io/reverse/?lon=" + coordinate.getX() + "&lat=" + coordinate.getY())).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = new JreJsonFactory().parse(response.body());
        JsonArray jsonFeatures = json.get("features");
        JsonObject jsonProperties = jsonFeatures.getObject(0).getObject("properties");

        StringJoiner address = new StringJoiner(", ");
        if (jsonProperties.hasKey("street")) {
            address.add(jsonProperties.getString("street"));
        }
        if (jsonProperties.hasKey("city")) {
            address.add(jsonProperties.getString("city"));
        }
        if (jsonProperties.hasKey("country")) {
            address.add(jsonProperties.getString("country"));
        }

        return address.toString();
    }
}
