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
    private static Map<MultilegFlightPK, MultilegFlightEntity> multilegFlights = new HashMap<>();

    public Flight fetchFlight(FlightID flightId) {
        System.out.println("Fetching flight " + flightId);

        var flightPK = switch (flightId) {
            case SimpleFlightID(String id) -> new SimpleFlightPK(id);
            case MultilegFlightID(String id) -> new MultilegFlightPK(id);
        };
        var flightEntity = switch (flightPK) {
            case SimpleFlightPK simpleFlightPK -> simpleFlights.computeIfAbsent(
                    simpleFlightPK,
                    _ -> {
                        var from = simpleFlightPK.flightId().substring(0, 2);
                        var to = simpleFlightPK.flightId().substring(2);

                        return new SimpleFlightEntity(
                                simpleFlightPK,
                                cities.get(from), cities.get(to),
                                new PriceEntity(100), new PlaneEntity("Airbus A350"));
                    });
            case MultilegFlightPK multilegFlightPK -> multilegFlights.computeIfAbsent(
                    multilegFlightPK,
                    _ -> {
                        var from = multilegFlightPK.flightId().substring(0, 2);
                        var via = multilegFlightPK.flightId().substring(2, 4);
                        var to = multilegFlightPK.flightId().substring(4);

                        return new MultilegFlightEntity(
                                multilegFlightPK,
                                cities.get(from), cities.get(via), cities.get(to),
                                new PriceEntity(100), new PlaneEntity("Airbus A350"));
                    });
        };

        return switch (flightPK) {
            case SimpleFlightPK _ -> {
                var simpleFlightEntity = (SimpleFlightEntity)flightEntity;
                var from = new City(simpleFlightEntity.from().name());
                var to = new City(simpleFlightEntity.to().name());
                var id = new SimpleFlightID(simpleFlightEntity.id().flightId());
                yield new SimpleFlight(id, from, to);
            }
            case MultilegFlightPK _ -> {
                var multilegFlightEntity = (MultilegFlightEntity)flightEntity;
                var from = new City(multilegFlightEntity.from().name());
                var via = new City(multilegFlightEntity.via().name());
                var to = new City(multilegFlightEntity.to().name());
                var id = new MultilegFlightID(multilegFlightEntity.id().flightId());
                yield new MultilegFlight(id, from, via, to);
            }
        };

//        return switch (flightEntity) {
//            case SimpleFlightEntity simpleFlightEntity -> {
//                var from = new City(simpleFlightEntity.from().name());
//                var to = new City(simpleFlightEntity.to().name());
//                var id = new SimpleFlightID(simpleFlightEntity.id().flightId());
//                yield new SimpleFlight(id, from, to);
//            }
//            case MultilegFlightEntity multilegFlightEntity -> {
//                var from = new City(multilegFlightEntity.from().name());
//                var via = new City(multilegFlightEntity.via().name());
//                var to = new City(multilegFlightEntity.to().name());
//                var id = new MultilegFlightID(multilegFlightEntity.id().flightId());
//                yield new MultilegFlight(id, from, via, to);
//            }
//            default -> throw new IllegalStateException("Unexpected value: " + flightEntity);
//        };
    }
}
