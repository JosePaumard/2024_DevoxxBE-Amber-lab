package org.paumard.flightmonitoring.business.model;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public record MultilegFlight(MultilegFlightID id, City from, City via, City to)
        implements Flight {

    public MultilegFlight {
        Objects.requireNonNull(id);
        Objects.requireNonNull(from);
        Objects.requireNonNull(via);
        Objects.requireNonNull(to);
    }

    private static final Map<MultilegFlightID, Price> pricePerFlight =
            new ConcurrentHashMap<>();

    public static Price price(MultilegFlight flight) {
        return pricePerFlight.get(flight.id());
    }

    public static void updatePrice(MultilegFlightID id, Price price) {
        pricePerFlight.put(id, price);
    }
}