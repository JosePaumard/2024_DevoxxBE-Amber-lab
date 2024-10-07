package org.paumard.flightmonitoring.db;

import org.paumard.flightmonitoring.business.model.City;
import org.paumard.flightmonitoring.business.model.Flight;
import org.paumard.flightmonitoring.business.model.FlightID;
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

    private static Map<FlightPK, FlightEntity> flights = new HashMap<>();

    public static FlightDBService getInstance() {
        return new FlightDBService();
    }

    public Flight fetchFlight(FlightID flightId) {
        System.out.println("Fetching flight " + flightId);

        var flightPK = new FlightPK(flightId.id());
        var flightEntity =  flights.computeIfAbsent(flightPK,
                _ -> {
                    var from = flightId.id().substring(0, 2);
                    var to = flightId.id().substring(2);

                    return new FlightEntity(flightPK, cities.get(from), cities.get(to), new PriceEntity(100), new PlaneEntity("Airbus A350"));
                });
        var from = new City(flightEntity.from().name());
        var to = new City(flightEntity.to().name());
        return new Flight(from, to);
    }
}
