package org.paumard.flightmonitoring.business;

import org.paumard.flightmonitoring.business.model.*;
import org.paumard.flightmonitoring.business.service.DBService;
import org.paumard.flightmonitoring.business.service.FlightConsumer;
import org.paumard.flightmonitoring.business.service.FlightGUIService;
import org.paumard.flightmonitoring.business.service.PriceMonitoringService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FlightMonitoring {

    private static final Map<FlightID, Flight> monitoredFlights = new ConcurrentHashMap<>();

    private final DBService dbService;
    private final PriceMonitoringService priceMonitoringService;
    private final FlightGUIService flightGUIService;

    public FlightMonitoring(DBService dbService, FlightGUIService guiService, PriceMonitoringService monitoringService) {
        this.dbService = dbService;
        this.flightGUIService = guiService;
        this.priceMonitoringService = monitoringService;
    }

    public void followFlight(FlightID flightID) {
        var flight = dbService.fetchFlight(flightID);
        FlightConsumer flightConsumer = price -> {
            switch(flight) {
                case SimpleFlight(SimpleFlightID id, City _, City _) -> SimpleFlight.updatePrice(id, price);
                case MultilegFlight(MultilegFlightID id, City _, City _, City _) -> MultilegFlight.updatePrice(id, price);
            }
        };
        priceMonitoringService.followPrice(flightID, flightConsumer);
    }

    public void monitorFlight(FlightID flightID) {
        var flight = dbService.fetchFlight(flightID);
        monitoredFlights.put(flightID, flight);
    }

    public void launchDisplay() {
        var executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            for (var flight : monitoredFlights.values()) {
                flightGUIService.displayFlight(flight);
            }
        };
        executor.scheduleAtFixedRate(task, 0, 500, TimeUnit.MILLISECONDS);
    }
}