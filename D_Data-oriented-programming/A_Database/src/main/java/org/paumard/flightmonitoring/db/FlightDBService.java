package org.paumard.flightmonitoring.db;

import org.paumard.flightmonitoring.db.model.*;

import java.util.HashMap;
import java.util.Map;

public class FlightDBService {

    private static Map<String, CityEntity> cities = Map.ofEntries(
            Map.entry("Pa", new CityEntity("Paris")),
            Map.entry("Lo", new CityEntity("London")),
            Map.entry("Am", new CityEntity("Amsterdam")),
            Map.entry("Fr", new CityEntity("Francfort")),
            Map.entry("NY", new CityEntity("New York")),
            Map.entry("Wa", new CityEntity("Washington")),
            Map.entry("At", new CityEntity("Atlanta")),
            Map.entry("Mi", new CityEntity("Miami"))
    );

    private static Map<FlightPK, FlightEntity> flights = new HashMap<>();

    public static FlightDBService getInstance() {
        return new FlightDBService();
    }

    public FlightEntity fetchFlight(FlightPK flightId) {
        System.out.println("Fetching flight " + flightId);

        return flights.computeIfAbsent(flightId,
                _ -> {
                    var from = flightId.flightId().substring(0, 2);
                    var to = flightId.flightId().substring(2);
                    return new FlightEntity(flightId, cities.get(from), cities.get(to), new PriceEntity(100), new PlaneEntity("Airbus A350"));
                });
    }
}
