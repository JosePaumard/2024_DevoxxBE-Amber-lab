package org.paumard.flightmonitoring.db;

import org.paumard.flightmonitoring.business.model.*;
import org.paumard.flightmonitoring.business.service.DBService;
import org.paumard.flightmonitoring.db.model.*;

import java.util.HashMap;
import java.util.Map;

public class FlightDBService implements DBService {

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

    private static Map<SimpleFlightPK, SimpleFlightEntity> simpleFlights = new HashMap<>();

    public Flight fetchFlight(FlightID flightId) {
        System.out.println("Fetching flight " + flightId);

        var flightPK = switch (flightId) {
            case SimpleFlightID(String id) -> new SimpleFlightPK(id);
        };
        var flightEntity = switch (flightPK) {
            case SimpleFlightPK simpleFlightPK -> simpleFlights.computeIfAbsent(
                    flightPK,
                    _ -> {
                        var from = simpleFlightPK.flightId().substring(0, 2);
                        var to = simpleFlightPK.flightId().substring(2);

                        return new SimpleFlightEntity(
                                flightPK,
                                cities.get(from), cities.get(to),
                                new PriceEntity(100), new PlaneEntity("Airbus A350"));
                    });
        };

        return switch (flightEntity) {
            case SimpleFlightEntity simpleFlightEntity -> {
                var from = new City(simpleFlightEntity.from().name());
                var to = new City(simpleFlightEntity.to().name());
                yield new SimpleFlight(new SimpleFlightID(simpleFlightEntity.id().flightId()), from, to);
            }
        };
    }
}
