package org.paumard.flightmonitoring.pricemonitoring;

import org.paumard.flightmonitoring.db.model.FlightPK;
import org.paumard.flightmonitoring.db.model.PriceEntity;
import org.paumard.flightmonitoring.pricemonitoring.model.FlightConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FlightPriceMonitoringService {

    private static final Map<FlightPK, FlightConsumer> registry = new HashMap<>();

    public static FlightPriceMonitoringService getInstance() {
        return new FlightPriceMonitoringService();
    }

    public void followPrice(FlightPK flightPK, FlightConsumer consumer) {
        System.out.println("Monitoring the price for " + flightPK);
        registry.put(flightPK, consumer);
    }

    public void updatePrices() {
        var random = new Random(314L);
        var executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            for (var flightConsumer : registry.values()) {
                flightConsumer.updateFlight(new PriceEntity(random.nextInt(80, 120)));
            }
        };
        executor.scheduleAtFixedRate(task, 0, 500, TimeUnit.MILLISECONDS);
    }
}