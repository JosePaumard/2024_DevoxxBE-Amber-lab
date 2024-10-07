package org.paumard.flightmonitoring.business;

import org.paumard.flightmonitoring.db.FlightDBService;
import org.paumard.flightmonitoring.db.model.FlightEntity;
import org.paumard.flightmonitoring.db.model.FlightPK;
import org.paumard.flightmonitoring.db.model.PriceEntity;
import org.paumard.flightmonitoring.gui.FlightGUI;
import org.paumard.flightmonitoring.pricemonitoring.FlightPriceMonitoringService;
import org.paumard.flightmonitoring.pricemonitoring.model.FlightConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FlightMonitoring {

    private static final Map<FlightPK, FlightEntity> monitoredFlights = new ConcurrentHashMap<>();

    private static final FlightDBService dbService =
            FlightDBService.getInstance();
    private static final FlightPriceMonitoringService priceMonitoringService =
            FlightPriceMonitoringService.getInstance();
    private static final FlightGUI flightGUIService =
            FlightGUI.getInstance();

    public static FlightMonitoring getInstance() {
        priceMonitoringService.updatePrices();
        launchDisplay();
        return new FlightMonitoring();
    }

    public void followFlight(FlightPK flightPK) {
        FlightEntity flightEntity = dbService.fetchFlight(flightPK);
        FlightConsumer flightConsumer = price -> flightEntity.updatePrice(new PriceEntity(price.price()));
        priceMonitoringService.followPrice(flightPK, flightConsumer);
    }

    public void monitorFlight(FlightPK flightPK) {
        var flight = dbService.fetchFlight(flightPK);
        monitoredFlights.put(flightPK, flight);
    }

    public static void launchDisplay() {
        var executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
//            System.out.println("Displaying " + monitoredFlights.size() + " flights");
            for (var flight : monitoredFlights.values()) {
                flightGUIService.displayFlight(flight);
            }
        };
        executor.scheduleAtFixedRate(task, 0, 500, TimeUnit.MILLISECONDS);
    }
}