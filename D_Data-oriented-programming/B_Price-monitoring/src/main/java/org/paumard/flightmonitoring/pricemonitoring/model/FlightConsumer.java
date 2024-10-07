package org.paumard.flightmonitoring.pricemonitoring.model;

import org.paumard.flightmonitoring.db.model.PriceEntity;

public interface FlightConsumer {

    void updateFlight(PriceEntity price);
}
