package org.paumard.flightmonitoring.business.model;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public record SimpleFlight(SimpleFlightID id, City from, City to) implements Flight {

    public SimpleFlight {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
    }

    private static final Map<SimpleFlightID, Price> pricePerFlight =
            new ConcurrentHashMap<>();

    public static Price price(SimpleFlight flight) {
        return pricePerFlight.get(flight.id());
    }

    public static void updatePrice(SimpleFlightID id, Price price) {
        pricePerFlight.put(id, price);
    }
}
