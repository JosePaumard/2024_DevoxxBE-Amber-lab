package org.paumard.flightmonitoring.business.model;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public record Flight(City from, City to) {

    public Flight {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }

    private static final Map<Flight, Price> pricePerFlight =
            new ConcurrentHashMap<>();

    public Price price() {
        return pricePerFlight.get(this);
    }

    public void updatePrice(Price price) {
        pricePerFlight.put(this, price);
    }
}