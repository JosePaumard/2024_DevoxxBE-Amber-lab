package org.paumard.flightmonitoring.pricemonitoring;

import org.paumard.flightmonitoring.business.model.FlightID;
import org.paumard.flightmonitoring.business.model.Price;
import org.paumard.flightmonitoring.business.service.FlightConsumer;
import org.paumard.flightmonitoring.business.service.PriceMonitoringService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FlightPriceMonitoringService implements PriceMonitoringService {

    private static final Map<FlightID, FlightConsumer> registry = new HashMap<>();

    public void followPrice(FlightID flightID, FlightConsumer consumer) {
        System.out.println("Monitoring the price for " + flightID);
        registry.put(flightID, consumer);
    }

    public void updatePrices() {
        var random = new Random(314L);
        var executor = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            for (var flightConsumer : registry.values()) {
                flightConsumer.updateFlight(new Price(random.nextInt(80, 120)));
            }
        };
        executor.scheduleAtFixedRate(task, 0, 500, TimeUnit.MILLISECONDS);
    }
}